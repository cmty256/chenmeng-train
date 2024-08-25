package com.chenmeng.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.chenmeng.train.business.enums.RedisKeyPreEnum;
import com.chenmeng.train.business.mapper.SkTokenMapper;
import com.chenmeng.train.business.mapper.custom.SkTokenMapperCust;
import com.chenmeng.train.business.model.dto.SkTokenQueryDTO;
import com.chenmeng.train.business.model.dto.SkTokenSaveDTO;
import com.chenmeng.train.business.model.entity.SkToken;
import com.chenmeng.train.business.model.entity.SkTokenExample;
import com.chenmeng.train.business.model.vo.SkTokenQueryVO;
import com.chenmeng.train.common.resp.PageResp;
import com.chenmeng.train.common.util.SnowUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 秒杀令牌表业务类
 *
 * @author 沉梦听雨
 **/
@Service
@RequiredArgsConstructor
public class SkTokenService {

    private final SkTokenMapper skTokenMapper;
    private final DailyTrainSeatService dailyTrainSeatService;
    private final DailyTrainStationService dailyTrainStationService;
    private final SkTokenMapperCust skTokenMapperCust;
    private final StringRedisTemplate strRedisTemplate;

    private static final Logger LOG = LoggerFactory.getLogger(SkTokenService.class);

    public void save(SkTokenSaveDTO req) {
        DateTime now = DateTime.now();
        SkToken skToken = BeanUtil.copyProperties(req, SkToken.class);
        if (ObjectUtil.isNull(skToken.getId())) {
            skToken.setId(SnowUtil.getSnowflakeNextId());
            skToken.setCreateTime(now);
            skToken.setUpdateTime(now);
            skTokenMapper.insert(skToken);
        } else {
            skToken.setUpdateTime(now);
            skTokenMapper.updateByPrimaryKey(skToken);
        }
    }

    public PageResp<SkTokenQueryVO> queryList(SkTokenQueryDTO req) {
        // 创建一个 SkTokenExample 对象
        SkTokenExample skTokenExample = new SkTokenExample();
        // 实现 id 降序
        skTokenExample.setOrderByClause("id desc");

        // 创建一个 SkTokenExample.Criteria 对象（类似 MP 的条件构造器）
        SkTokenExample.Criteria criteria = skTokenExample.createCriteria();

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        // 相当于在下面的查询 sql 尾部插入一个 limit
        PageHelper.startPage(req.getPage(), req.getSize());
        // 根据条件查询
        List<SkToken> skTokenList = skTokenMapper.selectByExample(skTokenExample);

        // 获取分页列表的总行数和总页数
        PageInfo<SkToken> pageInfo = new PageInfo<>(skTokenList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        // 将查询结果转换为 SkTokenQueryVO 对象
        List<SkTokenQueryVO> list = BeanUtil.copyToList(skTokenList, SkTokenQueryVO.class);

        // 创建一个 PageResp 对象
        PageResp<SkTokenQueryVO> pageResp = new PageResp<>();
        // 设置总行数
        pageResp.setTotal(pageInfo.getTotal());
        // 设置查询结果
        pageResp.setList(list);

        return pageResp;
    }

    public void delete(Long id) {
        skTokenMapper.deleteByPrimaryKey(id);
    }

    /**
     * 初始化
     */
    @Transactional(rollbackFor = Exception.class)
    public void genDaily(Date date, String trainCode) {
        LOG.info("删除日期【{}】车次【{}】的令牌记录", DateUtil.formatDate(date), trainCode);
        SkTokenExample skTokenExample = new SkTokenExample();
        skTokenExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
        skTokenMapper.deleteByExample(skTokenExample);

        DateTime now = DateTime.now();
        SkToken skToken = new SkToken();
        skToken.setDate(date);
        skToken.setTrainCode(trainCode);
        skToken.setId(SnowUtil.getSnowflakeNextId());
        skToken.setCreateTime(now);
        skToken.setUpdateTime(now);

        int seatCount = dailyTrainSeatService.countSeat(date, trainCode);
        LOG.info("车次【{}】座位数：{}", trainCode, seatCount);

        long stationCount = dailyTrainStationService.countByTrainCode(date, trainCode);
        LOG.info("车次【{}】到站数：{}", trainCode, stationCount);

        // 3/4需要根据实际卖票比例来定，一趟火车最多可以卖（seatCount * stationCount）张火车票
        int count = (int) (seatCount * stationCount); // * 3/4);
        LOG.info("车次【{}】初始生成令牌数：{}", trainCode, count);
        skToken.setCount(count);

        skTokenMapper.insert(skToken);
    }

    /**
     * 校验令牌
     */
    public boolean validSkToken(Date date, String trainCode, Long memberId) {
        LOG.info("会员【{}】获取日期【{}】车次【{}】的令牌开始", memberId, DateUtil.formatDate(date), trainCode);

        // 1、使用令牌锁防止机器人抢票
        // 先获取令牌锁，再校验令牌余量，防止机器人抢票，lockKey就是令牌，用来表示【谁能做什么】的一个凭证
        String lockKey = RedisKeyPreEnum.SK_TOKEN + "-" + DateUtil.formatDate(date) + "-" + trainCode + "-" + memberId;
        Boolean setIfAbsent = strRedisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockKey, 5, TimeUnit.SECONDS); // 后续不用释放, 5秒过期
        if (Boolean.TRUE.equals(setIfAbsent)) {
            LOG.info("恭喜，抢到令牌锁了！lockKey：{}", lockKey);
        } else {
            LOG.info("很遗憾，没抢到令牌锁！lockKey：{}", lockKey);
            return false;
        }

        // 2、使用缓存加速令牌锁功能
        String skTokenCountKey = RedisKeyPreEnum.SK_TOKEN_COUNT + "-" + DateUtil.formatDate(date) + "-" + trainCode;
        Object skTokenCount = strRedisTemplate.opsForValue().get(skTokenCountKey);
        if (skTokenCount != null) {
            LOG.info("缓存中有该车次令牌大闸的key：{}", skTokenCountKey);
            Long count = strRedisTemplate.opsForValue().decrement(skTokenCountKey, 1);
            if (count == null || count < 0L) {
                LOG.error("获取令牌失败：{}", skTokenCountKey);
                return false;
            } else {
                LOG.info("获取令牌后，令牌余数：{}", count);
                // 令牌大闸过期时间设置为60秒，避免【缓存穿透】
                strRedisTemplate.expire(skTokenCountKey, 60, TimeUnit.SECONDS);
                // 每获取5个令牌更新一次数据库
                if (count % 5 == 0) {
                    skTokenMapperCust.decrease(date, trainCode, 5);
                }
                return true;
            }
        } else {
            LOG.info("缓存中没有该车次令牌大闸的key：{}", skTokenCountKey);
            // 检查是否还有令牌
            SkTokenExample skTokenExample = new SkTokenExample();
            skTokenExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
            List<SkToken> tokenCountList = skTokenMapper.selectByExample(skTokenExample);
            if (CollUtil.isEmpty(tokenCountList)) {
                LOG.info("找不到日期【{}】车次【{}】的令牌记录", DateUtil.formatDate(date), trainCode);
                return false;
            }

            SkToken skToken = tokenCountList.get(0);
            if (skToken.getCount() <= 0) {
                LOG.info("日期【{}】车次【{}】的令牌余量为0", DateUtil.formatDate(date), trainCode);
                return false;
            }

            // 令牌还有余量
            // 令牌余数-1
            Integer count = skToken.getCount() - 1;
            skToken.setCount(count);
            LOG.info("将该车次令牌大闸放入缓存中，key: {}， count: {}", skTokenCountKey, count);
            // 不需要更新数据库，只要放缓存即可
            strRedisTemplate.opsForValue()
                    .set(skTokenCountKey, String.valueOf(count), 60, TimeUnit.SECONDS);
            return true;
        }

        // // 令牌约等于库存，令牌没有了，就不再卖票，不需要再进入购票主流程去判断库存，判断令牌肯定比判断库存效率高
        // int updateCount = skTokenMapperCust.decrease(date, trainCode, 1);
        // if (updateCount > 0) {
        //     return true;
        // } else {
        //     return false;
        // }
    }
}
