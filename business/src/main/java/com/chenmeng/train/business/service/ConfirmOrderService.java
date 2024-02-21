package com.chenmeng.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.chenmeng.train.business.enums.ConfirmOrderStatusEnum;
import com.chenmeng.train.business.enums.SeatColEnum;
import com.chenmeng.train.business.enums.SeatTypeEnum;
import com.chenmeng.train.business.mapper.ConfirmOrderMapper;
import com.chenmeng.train.business.model.dto.ConfirmOrderDoDTO;
import com.chenmeng.train.business.model.dto.ConfirmOrderQueryDTO;
import com.chenmeng.train.business.model.dto.ConfirmOrderSaveDTO;
import com.chenmeng.train.business.model.dto.ConfirmOrderTicketDTO;
import com.chenmeng.train.business.model.entity.ConfirmOrder;
import com.chenmeng.train.business.model.entity.ConfirmOrderExample;
import com.chenmeng.train.business.model.entity.DailyTrainTicket;
import com.chenmeng.train.business.model.vo.ConfirmOrderQueryVO;
import com.chenmeng.train.common.exception.BusinessException;
import com.chenmeng.train.common.exception.BusinessExceptionEnum;
import com.chenmeng.train.common.resp.PageResp;
import com.chenmeng.train.common.util.SnowUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.chenmeng.train.business.constant.NumberConstant.TWO;

/**
 * 确认订单表业务类
 *
 * @author 沉梦听雨
 **/
@Service
public class ConfirmOrderService {

    @Resource
    private ConfirmOrderMapper confirmOrderMapper;

    @Resource
    private DailyTrainTicketService dailyTrainTicketService;

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

    public void doConfirm(ConfirmOrderDoDTO dto) {
        // 省略业务数据校验，如：车次是否存在，余票是否存在，车次是否在有效期内，tickets条数>0，同乘客同车次是否已买过

        Date date = dto.getDate();
        String trainCode = dto.getTrainCode();
        String start = dto.getStart();
        String end = dto.getEnd();
        List<ConfirmOrderTicketDTO> tickets = dto.getTickets();
        // 2、保存确认订单表，状态初始
        DateTime now = DateTime.now();
        ConfirmOrder confirmOrder = new ConfirmOrder();
        confirmOrder.setId(SnowUtil.getSnowflakeNextId());
        confirmOrder.setCreateTime(now);
        confirmOrder.setUpdateTime(now);
        confirmOrder.setMemberId(dto.getMemberId());
        confirmOrder.setDate(date);
        confirmOrder.setTrainCode(trainCode);
        confirmOrder.setStart(start);
        confirmOrder.setEnd(end);
        confirmOrder.setDailyTrainTicketId(dto.getDailyTrainTicketId());
        confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
        confirmOrder.setTickets(JSON.toJSONString(dto.getTickets()));
        confirmOrderMapper.insert(confirmOrder);
        // 3、查出余票记录，需要得到真实的库存
        DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(date, trainCode, start, end);
        LOG.info("查出余票记录：{}", dailyTrainTicket);
        // 4、预扣减余票数量，并判断余票是否足够
        reduceTickets(dto, dailyTrainTicket);

        // 5、计算相对第一个座位的偏移值，比如选择的是C1,D2，则偏移值是：[0,5]，比如选择的是A1,B1,C1，则偏移值是：[0,1,2]
        // 获取用户选的座位
        ConfirmOrderTicketDTO ticketReq0 = tickets.get(0);
        if (StrUtil.isNotBlank(ticketReq0.getSeat())) {
            LOG.info("本次购票有选座");
            // 5.1、查出本次选座的座位类型都有哪些列，用于计算所选座位与第一个座位的偏离值，示例：[EDZ_A，EDZ_B，EDZ_C，EDZ_D，EDZ_F]
            List<SeatColEnum> colEnumList = SeatColEnum.getColsByType(ticketReq0.getSeatTypeCode());
            LOG.info("本次选座的座位类型包含的列：{}", colEnumList);

            // 5.2、组成和前端两排选座一样的列表，用于作参照的座位列表，示例：referSeatList = [A1, C1, D1, F1, A2, C2, D2, F2]
            List<String> referSeatList = new ArrayList<>();
            for (int i = 1; i <= TWO; i++) {
                for (SeatColEnum seatColEnum : colEnumList) {
                    referSeatList.add(seatColEnum.getCode() + i);
                }
            }
            LOG.info("用于作参照的两排座位：{}", referSeatList);

            // 5.3、计算绝对偏移值，即：在参照座位列表中的位置，例如：[1, 3]
            List<Integer> aboluteOffsetList = new ArrayList<>();
            for (ConfirmOrderTicketDTO ticketReq : tickets) {
                int index = referSeatList.indexOf(ticketReq.getSeat());
                aboluteOffsetList.add(index);
            }
            LOG.info("计算得到所有座位的绝对偏移值：{}", aboluteOffsetList);

            // 5.4、计算相对偏移值，例如：[0, 2]
            List<Integer> offsetList = new ArrayList<>();
            for (Integer index : aboluteOffsetList) {
                int offset = index - aboluteOffsetList.get(0);
                offsetList.add(offset);
            }
            LOG.info("计算得到所有座位的相对第一个座位的偏移值：{}", offsetList);
        } else {
            LOG.info("本次购票没有选座");
        }

        // 选座
            // 一个车箱一个车箱的获取座位数据
            // 挑选符合条件的座位，如果这个车箱不满足，则进入下个车箱(多个选座应该在同一个车厢)

        // 选中座位后的事务处理
            // 座位表修改售卖情况sell
            // 余票详情表修改余票
            // 为会员增加购票记录
            // 更新确认订单为成功
    }

    /**
     * 预扣减余票数量，并判断余票是否足够
     *
     * @param req
     * @param dailyTrainTicket
     */
    private static void reduceTickets(ConfirmOrderDoDTO req, DailyTrainTicket dailyTrainTicket) {
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
}
