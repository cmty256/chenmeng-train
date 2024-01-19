package com.chenmeng.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.chenmeng.train.common.context.LoginMemberContext;
import com.chenmeng.train.common.resp.PageResp;
import com.chenmeng.train.common.util.SnowUtil;
import com.chenmeng.train.business.mapper.DailyTrainStationMapper;
import com.chenmeng.train.business.model.dto.DailyTrainStationQueryDTO;
import com.chenmeng.train.business.model.dto.DailyTrainStationSaveDTO;
import com.chenmeng.train.business.model.entity.DailyTrainStation;
import com.chenmeng.train.business.model.entity.DailyTrainStationExample;
import com.chenmeng.train.business.model.vo.DailyTrainStationQueryVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 每日车站表业务类
 *
 * @author 沉梦听雨
 **/
@Service
public class DailyTrainStationService {

    @Resource
    private DailyTrainStationMapper dailyTrainStationMapper;

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainStationService.class);

    public void save(DailyTrainStationSaveDTO req) {
        DateTime now = DateTime.now();
        DailyTrainStation dailyTrainStation = BeanUtil.copyProperties(req, DailyTrainStation.class);
        if (ObjectUtil.isNull(dailyTrainStation.getId())) {
            dailyTrainStation.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainStation.setCreateTime(now);
            dailyTrainStation.setUpdateTime(now);
            dailyTrainStationMapper.insert(dailyTrainStation);
        } else {
            dailyTrainStation.setUpdateTime(now);
            dailyTrainStationMapper.updateByPrimaryKey(dailyTrainStation);
        }
    }

    public PageResp<DailyTrainStationQueryVO> queryList(DailyTrainStationQueryDTO req) {
        // 创建一个 DailyTrainStationExample 对象
        DailyTrainStationExample dailyTrainStationExample = new DailyTrainStationExample();
        // 实现 id 降序
        dailyTrainStationExample.setOrderByClause("id desc");

        // 创建一个 DailyTrainStationExample.Criteria 对象（类似 MP 的条件构造器）
        DailyTrainStationExample.Criteria criteria = dailyTrainStationExample.createCriteria();

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        // 相当于在下面的查询 sql 尾部插入一个 limit
        PageHelper.startPage(req.getPage(), req.getSize());
        // 根据条件查询
        List<DailyTrainStation> dailyTrainStationList = dailyTrainStationMapper.selectByExample(dailyTrainStationExample);

        // 获取分页列表的总行数和总页数
        PageInfo<DailyTrainStation> pageInfo = new PageInfo<>(dailyTrainStationList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        // 将查询结果转换为 DailyTrainStationQueryVO 对象
        List<DailyTrainStationQueryVO> list = BeanUtil.copyToList(dailyTrainStationList, DailyTrainStationQueryVO.class);

        // 创建一个 PageResp 对象
        PageResp<DailyTrainStationQueryVO> pageResp = new PageResp<>();
        // 设置总行数
        pageResp.setTotal(pageInfo.getTotal());
        // 设置查询结果
        pageResp.setList(list);

        return pageResp;
    }

    public void delete(Long id) {
        dailyTrainStationMapper.deleteByPrimaryKey(id);
    }
}
