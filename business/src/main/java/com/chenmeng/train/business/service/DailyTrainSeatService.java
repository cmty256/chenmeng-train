package com.chenmeng.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.chenmeng.train.business.mapper.DailyTrainSeatMapper;
import com.chenmeng.train.business.model.dto.DailyTrainSeatQueryDTO;
import com.chenmeng.train.business.model.dto.DailyTrainSeatSaveDTO;
import com.chenmeng.train.business.model.entity.DailyTrainSeat;
import com.chenmeng.train.business.model.entity.DailyTrainSeatExample;
import com.chenmeng.train.business.model.entity.TrainSeat;
import com.chenmeng.train.business.model.entity.TrainStation;
import com.chenmeng.train.business.model.vo.DailyTrainSeatQueryVO;
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
 * 每日座位表业务类
 *
 * @author 沉梦听雨
 **/
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DailyTrainSeatService {

    private final DailyTrainSeatMapper dailyTrainSeatMapper;
    private final TrainStationService trainStationService;
    private final TrainSeatService trainSeatService;

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainSeatService.class);

    public void save(DailyTrainSeatSaveDTO req) {
        DateTime now = DateTime.now();
        DailyTrainSeat dailyTrainSeat = BeanUtil.copyProperties(req, DailyTrainSeat.class);
        if (ObjectUtil.isNull(dailyTrainSeat.getId())) {
            dailyTrainSeat.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainSeat.setCreateTime(now);
            dailyTrainSeat.setUpdateTime(now);
            dailyTrainSeatMapper.insert(dailyTrainSeat);
        } else {
            dailyTrainSeat.setUpdateTime(now);
            dailyTrainSeatMapper.updateByPrimaryKey(dailyTrainSeat);
        }
    }

    public PageResp<DailyTrainSeatQueryVO> queryList(DailyTrainSeatQueryDTO req) {
        // 创建一个 DailyTrainSeatExample 对象
        DailyTrainSeatExample dailyTrainSeatExample = new DailyTrainSeatExample();
        dailyTrainSeatExample.setOrderByClause("date desc, train_code asc, carriage_index asc, carriage_seat_index asc");
        DailyTrainSeatExample.Criteria criteria = dailyTrainSeatExample.createCriteria();
        if (ObjectUtil.isNotEmpty(req.getTrainCode())) {
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        // 相当于在下面的查询 sql 尾部插入一个 limit
        PageHelper.startPage(req.getPage(), req.getSize());
        // 根据条件查询
        List<DailyTrainSeat> dailyTrainSeatList = dailyTrainSeatMapper.selectByExample(dailyTrainSeatExample);

        // 获取分页列表的总行数和总页数
        PageInfo<DailyTrainSeat> pageInfo = new PageInfo<>(dailyTrainSeatList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        // 将查询结果转换为 DailyTrainSeatQueryVO 对象
        List<DailyTrainSeatQueryVO> list = BeanUtil.copyToList(dailyTrainSeatList, DailyTrainSeatQueryVO.class);

        // 创建一个 PageResp 对象
        PageResp<DailyTrainSeatQueryVO> pageResp = new PageResp<>();
        // 设置总行数
        pageResp.setTotal(pageInfo.getTotal());
        // 设置查询结果
        pageResp.setList(list);

        return pageResp;
    }

    public void delete(Long id) {
        dailyTrainSeatMapper.deleteByPrimaryKey(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void genDaily(Date date, String trainCode) {
        LOG.info("生成日期【{}】车次【{}】的座位信息开始", DateUtil.formatDate(date), trainCode);

        // 删除某日某车次的座位信息
        DailyTrainSeatExample dailyTrainSeatExample = new DailyTrainSeatExample();
        dailyTrainSeatExample.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode);
        dailyTrainSeatMapper.deleteByExample(dailyTrainSeatExample);

        List<TrainStation> stationList = trainStationService.selectByTrainCode(trainCode);
        // 售卖情况：初始为 sell 补 0，5 个站就补 4 个 0
        String sell = StrUtil.fillBefore("", '0', stationList.size() - 1);

        // 查出某车次的所有的座位信息
        List<TrainSeat> seatList = trainSeatService.selectByTrainCode(trainCode);
        if (CollUtil.isEmpty(seatList)) {
            LOG.info("该车次【没有座位】基础数据，生成该车次的座位信息结束");
            return;
        }

        for (TrainSeat trainSeat : seatList) {
            DateTime now = DateTime.now();
            DailyTrainSeat dailyTrainSeat = BeanUtil.copyProperties(trainSeat, DailyTrainSeat.class);
            dailyTrainSeat.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainSeat.setCreateTime(now);
            dailyTrainSeat.setUpdateTime(now);
            dailyTrainSeat.setDate(date);
            dailyTrainSeat.setSell(sell);
            dailyTrainSeatMapper.insert(dailyTrainSeat);
        }

        LOG.info("生成日期【{}】车次【{}】的座位信息结束", DateUtil.formatDate(date), trainCode);
    }

    public int countSeat(Date date, String trainCode, String seatType) {
        DailyTrainSeatExample example = new DailyTrainSeatExample();
        DailyTrainSeatExample.Criteria criteria = example.createCriteria();
        criteria.andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode);
        if (StrUtil.isNotBlank(seatType)) {
            criteria.andSeatTypeEqualTo(seatType);
        }

        long num = dailyTrainSeatMapper.countByExample(example);

        if (num == 0L) {
            // -1 表示没有这种座位
            return -1;
        }
        return (int) num;
    }

    public List<DailyTrainSeat> selectByCarriage(Date date, String trainCode, Integer carriageIndex) {
        DailyTrainSeatExample example = new DailyTrainSeatExample();
        example.setOrderByClause("carriage_seat_index asc");
        example.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode)
                .andCarriageIndexEqualTo(carriageIndex);
        return dailyTrainSeatMapper.selectByExample(example);
    }
}
