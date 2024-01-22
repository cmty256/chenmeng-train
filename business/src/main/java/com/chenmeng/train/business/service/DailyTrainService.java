package com.chenmeng.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.chenmeng.train.business.mapper.DailyTrainMapper;
import com.chenmeng.train.business.model.dto.DailyTrainQueryDTO;
import com.chenmeng.train.business.model.dto.DailyTrainSaveDTO;
import com.chenmeng.train.business.model.entity.DailyTrain;
import com.chenmeng.train.business.model.entity.DailyTrainExample;
import com.chenmeng.train.business.model.entity.Train;
import com.chenmeng.train.business.model.vo.DailyTrainQueryVO;
import com.chenmeng.train.common.resp.PageResp;
import com.chenmeng.train.common.util.SnowUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 每日车次表业务类
 *
 * @author 沉梦听雨
 **/
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DailyTrainService {

    private final DailyTrainMapper dailyTrainMapper;
    private final TrainService trainService;
    private final DailyTrainStationService dailyTrainStationService;
    private final DailyTrainCarriageService dailyTrainCarriageService;
    private final DailyTrainSeatService dailyTrainSeatService;

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainService.class);

    public void save(DailyTrainSaveDTO req) {
        DateTime now = DateTime.now();
        DailyTrain dailyTrain = BeanUtil.copyProperties(req, DailyTrain.class);
        if (ObjectUtil.isNull(dailyTrain.getId())) {
            dailyTrain.setId(SnowUtil.getSnowflakeNextId());
            dailyTrain.setCreateTime(now);
            dailyTrain.setUpdateTime(now);
            dailyTrainMapper.insert(dailyTrain);
        } else {
            dailyTrain.setUpdateTime(now);
            dailyTrainMapper.updateByPrimaryKey(dailyTrain);
        }
    }

    public PageResp<DailyTrainQueryVO> queryList(DailyTrainQueryDTO req) {
        // 创建一个 DailyTrainExample 对象
        DailyTrainExample dailyTrainExample = new DailyTrainExample();
        // 先按 日期 降序，再按 车次编号 升序
        dailyTrainExample.setOrderByClause("date desc, code asc");
        // 创建一个 DailyTrainExample.Criteria 对象（类似 MP 的条件构造器）
        DailyTrainExample.Criteria criteria = dailyTrainExample.createCriteria();
        if (ObjectUtil.isNotNull(req.getDate())) {
            criteria.andDateEqualTo(req.getDate());
        }
        if (ObjectUtil.isNotEmpty(req.getCode())) {
            criteria.andCodeEqualTo(req.getCode());
        }

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        // 相当于在下面的查询 sql 尾部插入一个 limit
        PageHelper.startPage(req.getPage(), req.getSize());
        // 根据条件查询
        List<DailyTrain> dailyTrainList = dailyTrainMapper.selectByExample(dailyTrainExample);

        // 获取分页列表的总行数和总页数
        PageInfo<DailyTrain> pageInfo = new PageInfo<>(dailyTrainList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        // 将查询结果转换为 DailyTrainQueryVO 对象
        List<DailyTrainQueryVO> list = BeanUtil.copyToList(dailyTrainList, DailyTrainQueryVO.class);

        // 创建一个 PageResp 对象
        PageResp<DailyTrainQueryVO> pageResp = new PageResp<>();
        // 设置总行数
        pageResp.setTotal(pageInfo.getTotal());
        // 设置查询结果
        pageResp.setList(list);

        return pageResp;
    }

    public void delete(Long id) {
        dailyTrainMapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据 时间 生成某日所有车次信息，包括车次、车站、车厢、座位
     * @param date
     */
    @Transactional(rollbackFor = Exception.class)
    public void genDaily(Date date) {
        List<Train> trainList = trainService.selectAll();
        if (CollUtil.isEmpty(trainList)) {
            LOG.info("没有车次基础数据，任务结束");
            return;
        }

        // 写法注意：车次之间低耦合，车次内部高内聚
        for (Train train : trainList) {
            genDailyTrain(date, train);
        }
    }

    /**
     * 生成某日列车信息（车次之间低耦合，车次内部高内聚）
     *
     * @param date
     * @param train
     */
    public void genDailyTrain(Date date, Train train) {
        LOG.info("生成日期【{}】车次【{}】的信息开始", DateUtil.formatDate(date), train.getCode());

        // 1、删除该车次已有的数据
        DailyTrainExample dailyTrainExample = new DailyTrainExample();
        dailyTrainExample.createCriteria()
                .andDateEqualTo(date)
                .andCodeEqualTo(train.getCode());
        dailyTrainMapper.deleteByExample(dailyTrainExample);

        // 2.1、生成该车次的数据
        DateTime now = DateTime.now();
        DailyTrain dailyTrain = BeanUtil.copyProperties(train, DailyTrain.class);
        dailyTrain.setId(SnowUtil.getSnowflakeNextId());
        dailyTrain.setCreateTime(now);
        dailyTrain.setUpdateTime(now);
        dailyTrain.setDate(date);
        dailyTrainMapper.insert(dailyTrain);

        // 2.2、生成该车次的车站数据
        dailyTrainStationService.genDaily(date, train.getCode());

        // 2.3、生成该车次的车厢数据
        dailyTrainCarriageService.genDaily(date, train.getCode());

        // 2.4、生成该车次的座位数据
        dailyTrainSeatService.genDaily(date, train.getCode());

        LOG.info("生成日期【{}】车次【{}】的信息结束", DateUtil.formatDate(date), train.getCode());
    }
}
