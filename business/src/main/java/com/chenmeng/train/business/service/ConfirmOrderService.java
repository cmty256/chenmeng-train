package com.chenmeng.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson2.JSON;
import com.chenmeng.train.business.enums.ConfirmOrderStatusEnum;
import com.chenmeng.train.business.enums.RedisKeyPreEnum;
import com.chenmeng.train.business.enums.SeatColEnum;
import com.chenmeng.train.business.enums.SeatTypeEnum;
import com.chenmeng.train.business.mapper.ConfirmOrderMapper;
import com.chenmeng.train.business.model.dto.ConfirmOrderDoDTO;
import com.chenmeng.train.business.model.dto.ConfirmOrderQueryDTO;
import com.chenmeng.train.business.model.dto.ConfirmOrderSaveDTO;
import com.chenmeng.train.business.model.dto.ConfirmOrderTicketDTO;
import com.chenmeng.train.business.model.entity.*;
import com.chenmeng.train.business.model.vo.ConfirmOrderQueryVO;
import com.chenmeng.train.common.exception.BusinessException;
import com.chenmeng.train.common.exception.BusinessExceptionEnum;
import com.chenmeng.train.common.resp.PageResp;
import com.chenmeng.train.common.util.SnowUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 确认订单表业务类
 *
 * @author 沉梦听雨
 **/
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ConfirmOrderService {

    private final ConfirmOrderMapper confirmOrderMapper;
    private final DailyTrainTicketService dailyTrainTicketService;
    private final DailyTrainCarriageService dailyTrainCarriageService;
    private final DailyTrainSeatService dailyTrainSeatService;
    private final AfterConfirmOrderService afterConfirmOrderService;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;

    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderService.class);

    public void save(ConfirmOrderSaveDTO req) {
        DateTime now = DateTime.now();
        ConfirmOrder confirmOrder = BeanUtil.copyProperties(req, ConfirmOrder.class);
        if (ObjectUtil.isNull(confirmOrder.getId())) {
            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.insert(confirmOrder);
        } else {
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.updateByPrimaryKey(confirmOrder);
        }
    }

    public PageResp<ConfirmOrderQueryVO> queryList(ConfirmOrderQueryDTO req) {
        // 创建一个 ConfirmOrderExample 对象
        ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
        // 实现 id 降序
        confirmOrderExample.setOrderByClause("id desc");

        // 创建一个 ConfirmOrderExample.Criteria 对象（类似 MP 的条件构造器）
        ConfirmOrderExample.Criteria criteria = confirmOrderExample.createCriteria();

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        // 相当于在下面的查询 sql 尾部插入一个 limit
        PageHelper.startPage(req.getPage(), req.getSize());
        // 根据条件查询
        List<ConfirmOrder> confirmOrderList = confirmOrderMapper.selectByExample(confirmOrderExample);

        // 获取分页列表的总行数和总页数
        PageInfo<ConfirmOrder> pageInfo = new PageInfo<>(confirmOrderList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        // 将查询结果转换为 ConfirmOrderQueryVO 对象
        List<ConfirmOrderQueryVO> list = BeanUtil.copyToList(confirmOrderList, ConfirmOrderQueryVO.class);

        // 创建一个 PageResp 对象
        PageResp<ConfirmOrderQueryVO> pageResp = new PageResp<>();
        // 设置总行数
        pageResp.setTotal(pageInfo.getTotal());
        // 设置查询结果
        pageResp.setList(list);

        return pageResp;
    }

    public void delete(Long id) {
        confirmOrderMapper.deleteByPrimaryKey(id);
    }

    @SentinelResource(value = "doConfirm", blockHandler = "doConfirmBlock")
    public void doConfirm(ConfirmOrderDoDTO dto) {
        // 获取分布式锁
        String lockKey = RedisKeyPreEnum.CONFIRM_ORDER + "-" + DateUtil.formatDate(dto.getDate()) + "-" + dto.getTrainCode();
        // setIfAbsent就是对应redis的setnx
        Boolean setIfAbsent = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 10, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(setIfAbsent)) {
            LOG.info("恭喜，抢到锁了！【购票锁】lockKey：{}", lockKey);
        } else {
            LOG.info("没抢到【购票锁】，有其它消费线程正在出票，不做任何处理");
            return;
        }

        RLock lock = null;
        try {
            // 使用redisson，自带看门狗
            lock = redissonClient.getLock(lockKey);

            /*
              waitTime – the maximum time to acquire the lock 等待获取锁时间(最大尝试获得锁的时间)，超时返回false
              leaseTime – lease time 锁时长，即n秒后自动释放锁
              time unit – time unit 时间单位
             */
            // boolean tryLock = lock.tryLock(30, 10, TimeUnit.SECONDS); // 不带看门狗
            boolean tryLock = lock.tryLock(0, TimeUnit.SECONDS); // 带看门狗 - 默认30秒刷新获取
            if (tryLock) {
                LOG.info("恭喜，抢到【购票锁】了！");
            } else {
                // 只是没抢到锁，并不知道票抢完了没，所以提示稍候再试
                LOG.info("很遗憾，没抢到【购票锁】");
                throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);
            }

            while (true) {
                // 取确认订单表的记录，同日期车次，状态是I，分页处理，每次取N条
                ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
                confirmOrderExample.setOrderByClause("id asc");
                ConfirmOrderExample.Criteria criteria = confirmOrderExample.createCriteria();
                criteria.andDateEqualTo(dto.getDate())
                        .andTrainCodeEqualTo(dto.getTrainCode())
                        .andStatusEqualTo(ConfirmOrderStatusEnum.INIT.getCode());
                PageHelper.startPage(1, 5);
                List<ConfirmOrder> list = confirmOrderMapper.selectByExampleWithBLOBs(confirmOrderExample);

                if (CollUtil.isEmpty(list)) {
                    LOG.info("没有需要处理的订单，结束循环");
                    break;
                } else {
                    LOG.info("本次处理{}条订单", list.size());
                }

                // 一条一条的卖
                list.forEach(confirmOrder -> {
                    try {
                        sell(confirmOrder);
                    } catch (BusinessException e) {
                        if (e.getE().equals(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR)) {
                            LOG.info("本订单余票不足，继续售卖下一个订单");
                            confirmOrder.setStatus(ConfirmOrderStatusEnum.EMPTY.getCode());
                            updateStatus(confirmOrder);
                        } else {
                            throw e;
                        }
                    }
                });
            }
        } catch (InterruptedException e) {
            LOG.error("购票异常", e);
        } finally {
            // region try finally不能包含加锁的那段代码，否则加锁失败会走到finally里，从而释放别的线程的锁

            // LOG.info("购票流程结束，释放锁！");
            // if (null != lock && lock.isHeldByCurrentThread()) {
            //     lock.unlock();
            // }

            // endregion
            LOG.info("购票流程结束，释放锁！lockKey：{}", lockKey);
            stringRedisTemplate.delete(lockKey);
        }
    }

    /**
     * 更新状态
     * @param confirmOrder
     */
    public void updateStatus(ConfirmOrder confirmOrder) {
        ConfirmOrder confirmOrderForUpdate = new ConfirmOrder();
        confirmOrderForUpdate.setId(confirmOrder.getId());
        confirmOrderForUpdate.setUpdateTime(new Date());
        confirmOrderForUpdate.setStatus(confirmOrder.getStatus());
        confirmOrderMapper.updateByPrimaryKeySelective(confirmOrderForUpdate);
    }

    /**
     * 售票
     * @param confirmOrder
     */
    private void sell(ConfirmOrder confirmOrder) {
        // 为了演示排队效果，每次出票增加200毫秒延时
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // 构造ConfirmOrderDoReq
        ConfirmOrderDoDTO req = new ConfirmOrderDoDTO();
        req.setMemberId(confirmOrder.getMemberId());
        req.setDate(confirmOrder.getDate());
        req.setTrainCode(confirmOrder.getTrainCode());
        req.setStart(confirmOrder.getStart());
        req.setEnd(confirmOrder.getEnd());
        req.setDailyTrainTicketId(confirmOrder.getDailyTrainTicketId());
        req.setTickets(JSON.parseArray(confirmOrder.getTickets(), ConfirmOrderTicketDTO.class));
        req.setImageCode("");
        req.setImageCodeToken("");
        req.setLogId("");

        // 省略业务数据校验，如：车次是否存在，余票是否存在，车次是否在有效期内，tickets条数>0，同乘客同车次是否已买过

        // 将订单设置成处理中，避免重复处理
        LOG.info("将确认订单更新成处理中，避免重复处理，confirm_order.id: {}", confirmOrder.getId());
        confirmOrder.setStatus(ConfirmOrderStatusEnum.PENDING.getCode());
        updateStatus(confirmOrder);

        Date date = req.getDate();
        String trainCode = req.getTrainCode();
        String start = req.getStart();
        String end = req.getEnd();
        List<ConfirmOrderTicketDTO> tickets = req.getTickets();

        // 查出余票记录，需要得到真实的库存
        DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(date, trainCode, start, end);
        LOG.info("查出余票记录：{}", dailyTrainTicket);

        // 预扣减余票数量，并判断余票是否足够
        reduceTickets(req, dailyTrainTicket);

        // 最终地选座结果
        List<DailyTrainSeat> finalSeatList = new ArrayList<>();
        // 计算相对第一个座位的偏移值
        // 比如选择的是C1,D2，则偏移值是：[0,5]
        // 比如选择的是A1,B1,C1，则偏移值是：[0,1,2]
        ConfirmOrderTicketDTO ticketReq0 = tickets.get(0);
        if (StrUtil.isNotBlank(ticketReq0.getSeat())) {
            LOG.info("本次购票有选座");
            // 查出本次选座的座位类型都有哪些列，用于计算所选座位与第一个座位的偏离值
            List<SeatColEnum> colEnumList = SeatColEnum.getColsByType(ticketReq0.getSeatTypeCode());
            LOG.info("本次选座的座位类型包含的列：{}", colEnumList);

            // 组成和前端两排选座一样的列表，用于作参照的座位列表，例：referSeatList = {A1, C1, D1, F1, A2, C2, D2, F2}
            List<String> referSeatList = new ArrayList<>();
            for (int i = 1; i <= 2; i++) {
                for (SeatColEnum seatColEnum : colEnumList) {
                    referSeatList.add(seatColEnum.getCode() + i);
                }
            }
            LOG.info("用于作参照的两排座位：{}", referSeatList);

            List<Integer> offsetList = new ArrayList<>();
            // 绝对偏移值，即：在参照座位列表中的位置
            List<Integer> aboluteOffsetList = new ArrayList<>();
            for (ConfirmOrderTicketDTO ticketReq : tickets) {
                int index = referSeatList.indexOf(ticketReq.getSeat());
                aboluteOffsetList.add(index);
            }
            LOG.info("计算得到所有座位的绝对偏移值：{}", aboluteOffsetList);
            for (Integer index : aboluteOffsetList) {
                int offset = index - aboluteOffsetList.get(0);
                offsetList.add(offset);
            }
            LOG.info("计算得到所有座位的相对第一个座位的偏移值：{}", offsetList);

            getSeat(finalSeatList,
                    date,
                    trainCode,
                    ticketReq0.getSeatTypeCode(),
                    ticketReq0.getSeat().split("")[0], // 从A1得到A
                    offsetList,
                    dailyTrainTicket.getStartIndex(),
                    dailyTrainTicket.getEndIndex()
            );

        } else {
            LOG.info("本次购票没有选座");
            for (ConfirmOrderTicketDTO ticketReq : tickets) {
                getSeat(finalSeatList,
                        date,
                        trainCode,
                        ticketReq.getSeatTypeCode(),
                        null,
                        null,
                        dailyTrainTicket.getStartIndex(),
                        dailyTrainTicket.getEndIndex()
                );
            }
        }

        LOG.info("最终选座：{}", finalSeatList);

        // 选中座位后事务处理：
        // 座位表修改售卖情况sell；
        // 余票详情表修改余票；
        // 为会员增加购票记录
        // 更新确认订单为成功
        try {
            afterConfirmOrderService.afterDoConfirm(dailyTrainTicket, finalSeatList, tickets, confirmOrder);
        } catch (Exception e) {
            LOG.error("保存购票信息失败", e);
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_EXCEPTION);
        }
    }

    /**
     * 选座：挑座位，如果有选座，则一次性挑完，如果无选座，则一个一个挑
     *
     * @param finalSeatList 最终地选座结果
     * @param date
     * @param trainCode
     * @param seatType
     * @param column        列号
     * @param offsetList    相对偏移值列表
     * @param startIndex
     * @param endIndex
     */
    private void getSeat(List<DailyTrainSeat> finalSeatList, Date date, String trainCode,
                         String seatType, String column, List<Integer> offsetList,
                         Integer startIndex, Integer endIndex) {
        List<DailyTrainCarriage> carriageList = dailyTrainCarriageService.selectBySeatType(date, trainCode, seatType);
        LOG.info("共查出{}个符合条件的车厢", carriageList.size());

        // 一个车箱一个车箱的获取座位数据
        for (DailyTrainCarriage dailyTrainCarriage : carriageList) {
            Integer carriageIndex = dailyTrainCarriage.getIndex();
            LOG.info("开始从{}号车厢选座", carriageIndex);

            List<DailyTrainSeat> totalSeatList = dailyTrainSeatService.selectByCarriage(date, trainCode, carriageIndex);
            LOG.info("{}号车厢的座位数为：{}", carriageIndex, totalSeatList.size());

            // 相当于一个临时选座列表
            List<DailyTrainSeat> getSeatList = new ArrayList<>();
            for (int i = 0; i < totalSeatList.size(); i++) {
                DailyTrainSeat dailyTrainSeat = totalSeatList.get(i);
                Integer seatIndex = dailyTrainSeat.getCarriageSeatIndex();
                String col = dailyTrainSeat.getCol();

                // 1、判断当前座位不能是被选中过的座位
                boolean alreadyChooseFlag = false;
                for (DailyTrainSeat finalSeat : finalSeatList) {
                    if (finalSeat.getId().equals(dailyTrainSeat.getId())) {
                        alreadyChooseFlag = true;
                        break;
                    }
                }
                if (alreadyChooseFlag) {
                    LOG.info("座位{}被选中过，不能重复选中，继续判断下一个座位", seatIndex);
                    continue;
                }
                // 2、判断column，有值的话要比对列号 -- 用户选的座位的列号
                if (StrUtil.isBlank(column)) {
                    LOG.info("无选座");
                } else {
                    if (!column.equals(col)) {
                        LOG.info("座位{}列值不对，继续判断下一个座位，当前列值：{}，目标列值：{}", seatIndex, col, column);
                        continue;
                    }
                }
                // 3、根据座位销售详情判断本次是否可选 -- 选出第一个座位
                boolean isChoose = calSell(dailyTrainSeat, startIndex, endIndex);
                if (isChoose) {
                    LOG.info("选中座位");
                    getSeatList.add(dailyTrainSeat);
                } else {
                    continue;
                }

                // 4、根据offset选剩下的座位
                boolean isGetAllOffsetSeat = true;
                if (CollUtil.isNotEmpty(offsetList)) {
                    LOG.info("有偏移值：{}，校验偏移的座位是否可选", offsetList);
                    // 从索引1开始，索引0就是当前已选中的票
                    for (int j = 1; j < offsetList.size(); j++) {
                        Integer offset = offsetList.get(j);
                        // 座位在库的索引是从1开始
                        // int nextIndex = seatIndex + offset - 1;
                        int nextIndex = i + offset;

                        // 有选座时，一定是在同一个车箱
                        if (nextIndex >= totalSeatList.size()) {
                            LOG.info("座位{}不可选，偏移后的索引超出了这个车箱的座位数", nextIndex);
                            isGetAllOffsetSeat = false;
                            break;
                        }

                        DailyTrainSeat nextDailyTrainSeat = totalSeatList.get(nextIndex);
                        // 根据座位销售详情判断本次是否可选
                        boolean isChooseNext = calSell(nextDailyTrainSeat, startIndex, endIndex);
                        if (isChooseNext) {
                            LOG.info("座位{}被选中", nextDailyTrainSeat.getCarriageSeatIndex());
                            getSeatList.add(nextDailyTrainSeat);
                        } else {
                            LOG.info("座位{}不可选", nextDailyTrainSeat.getCarriageSeatIndex());
                            isGetAllOffsetSeat = false;
                            break;
                        }
                    }
                }
                // 判断座位没有全部选中的情况，需要清空临时选座列表并且重新循环
                if (!isGetAllOffsetSeat) {
                    getSeatList = new ArrayList<>();
                    continue;
                }

                // 5、保存选好的座位
                finalSeatList.addAll(getSeatList);
                return;
            }
        }
    }

    /**
     * 计算某座位在区间内是否可卖
     * 例：sell=10001 -- 1代表区间0~1 0代表区间1~4(1~2 2~3 3~4) 1代表区间4~5
     * 本次购买区间站1~4，则区间已售信息为：000
     * 全部是0，表示这个区间可买；只要有1，就表示区间内已售过票
     * <p>
     * 选中后，要计算购票后的sell，比如原来是10001，本次购买区间站1~4
     * 方案：构造本次购票造成的售卖信息01110，和原sell 10001按位或运算，最终得到11111
     */
    private boolean calSell(DailyTrainSeat dailyTrainSeat, Integer startIndex, Integer endIndex) {
        // 00001, 00000
        String sell = dailyTrainSeat.getSell();
        //  000, 000
        String sellPart = sell.substring(startIndex, endIndex);
        if (Integer.parseInt(sellPart) > 0) {
            LOG.info("座位{}在本次车站区间{}~{}已售过票，不可选中该座位", dailyTrainSeat.getCarriageSeatIndex(), startIndex, endIndex);
            return false;
        } else {
            LOG.info("座位{}在本次车站区间{}~{}未售过票，可选中该座位", dailyTrainSeat.getCarriageSeatIndex(), startIndex, endIndex);
            //  111,   111
            String curSell = sellPart.replace('0', '1');
            // 0111,  0111
            curSell = StrUtil.fillBefore(curSell, '0', endIndex);
            // 01110, 01110
            curSell = StrUtil.fillAfter(curSell, '0', sell.length());

            // 当前区间售票信息curSell 01110与库里的已售信息sell 00001按位与，即可得到该座位卖出此票后的售票详情
            // 15(01111), 14(01110 = 01110|00000) -- 二进制字符串转成相应的整数，然后进行【按位或】运算
            int newSellInt = NumberUtil.binaryToInt(curSell) | NumberUtil.binaryToInt(sell);
            //  1111,  1110 -- 整数转成相应的二进制字符串
            String newSell = NumberUtil.getBinaryStr(newSellInt);
            // 01111, 01110 -- 填充 0 直到字符串长度为售票信息（sell的）长度
            newSell = StrUtil.fillBefore(newSell, '0', sell.length());
            LOG.info("座位{}被选中，原售票信息：{}，车站区间：{}~{}，即：{}，最终售票信息：{}"
                    , dailyTrainSeat.getCarriageSeatIndex(), sell, startIndex, endIndex, curSell, newSell);
            dailyTrainSeat.setSell(newSell);
            return true;
        }
    }

    /**
     * 预扣减余票数量，并判断余票是否足够
     *
     * @param req
     * @param dailyTrainTicket
     */
    private void reduceTickets(ConfirmOrderDoDTO req, DailyTrainTicket dailyTrainTicket) {
        for (ConfirmOrderTicketDTO ticketReq : req.getTickets()) {
            String seatTypeCode = ticketReq.getSeatTypeCode();
            SeatTypeEnum seatTypeEnum = EnumUtil.getBy(SeatTypeEnum::getCode, seatTypeCode);
            switch (seatTypeEnum) {
                case YDZ -> {
                    int countLeft = dailyTrainTicket.getYdz() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYdz(countLeft);
                }
                case EDZ -> {
                    int countLeft = dailyTrainTicket.getEdz() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setEdz(countLeft);
                }
                case RW -> {
                    int countLeft = dailyTrainTicket.getRw() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setRw(countLeft);
                }
                case YW -> {
                    int countLeft = dailyTrainTicket.getYw() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYw(countLeft);
                }
                default -> throw new IllegalStateException("Unexpected value: " + seatTypeEnum);
            }
        }
    }

    /**
     * 降级方法，需包含限流方法的所有参数和BlockException参数
     *
     * @param dto
     * @param e
     */
    public void doConfirmBlock(ConfirmOrderDoDTO dto, BlockException e) {
        LOG.info("购票请求被限流：{}", dto);
        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
    }
}
