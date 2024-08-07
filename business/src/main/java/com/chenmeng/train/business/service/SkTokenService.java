package com.chenmeng.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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
        // 令牌约等于库存，令牌没有了，就不再卖票，不需要再进入购票主流程去判断库存，判断令牌肯定比判断库存效率高
        int updateCount = skTokenMapperCust.decrease(date, trainCode, 1);
        if (updateCount > 0) {
            return true;
        } else {
            return false;
        }
    }
}
