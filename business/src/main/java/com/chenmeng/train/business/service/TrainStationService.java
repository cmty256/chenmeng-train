package com.chenmeng.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.chenmeng.train.business.mapper.TrainStationMapper;
import com.chenmeng.train.business.model.dto.TrainStationQueryDTO;
import com.chenmeng.train.business.model.dto.TrainStationSaveDTO;
import com.chenmeng.train.business.model.entity.TrainStation;
import com.chenmeng.train.business.model.entity.TrainStationExample;
import com.chenmeng.train.business.model.vo.TrainStationQueryVO;
import com.chenmeng.train.common.resp.PageResp;
import com.chenmeng.train.common.util.SnowUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 列车车站表业务类
 *
 * @author 沉梦听雨
 **/
@Service
public class TrainStationService {

    @Resource
    private TrainStationMapper trainStationMapper;

    private static final Logger LOG = LoggerFactory.getLogger(TrainStationService.class);

    public void save(TrainStationSaveDTO req) {
        DateTime now = DateTime.now();
        TrainStation trainStation = BeanUtil.copyProperties(req, TrainStation.class);
        if (ObjectUtil.isNull(trainStation.getId())) {
            trainStation.setId(SnowUtil.getSnowflakeNextId());
            trainStation.setCreateTime(now);
            trainStation.setUpdateTime(now);
            trainStationMapper.insert(trainStation);
        } else {
            trainStation.setUpdateTime(now);
            trainStationMapper.updateByPrimaryKey(trainStation);
        }
    }

    public PageResp<TrainStationQueryVO> queryList(TrainStationQueryDTO req) {
        // 创建一个 TrainStationExample 对象
        TrainStationExample trainStationExample = new TrainStationExample();
        // 实现 车次编号、站序 升序查询
        trainStationExample.setOrderByClause("train_code asc, `index` asc");
        // 创建一个 TrainStationExample.Criteria 对象
        TrainStationExample.Criteria criteria = trainStationExample.createCriteria();
        if (ObjectUtil.isNotEmpty(req.getTrainCode())) {
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        // 相当于在下面的查询 sql 尾部插入一个 limit
        PageHelper.startPage(req.getPage(), req.getSize());
        // 根据条件查询
        List<TrainStation> trainStationList = trainStationMapper.selectByExample(trainStationExample);

        // 获取分页列表的总行数和总页数
        PageInfo<TrainStation> pageInfo = new PageInfo<>(trainStationList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        // 将查询结果转换为 TrainStationQueryVO 对象
        List<TrainStationQueryVO> list = BeanUtil.copyToList(trainStationList, TrainStationQueryVO.class);

        // 创建一个 PageResp 对象
        PageResp<TrainStationQueryVO> pageResp = new PageResp<>();
        // 设置总行数
        pageResp.setTotal(pageInfo.getTotal());
        // 设置查询结果
        pageResp.setList(list);

        return pageResp;
    }

    public void delete(Long id) {
        trainStationMapper.deleteByPrimaryKey(id);
    }
}
