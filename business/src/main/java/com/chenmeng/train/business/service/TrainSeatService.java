package com.chenmeng.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.chenmeng.train.common.context.LoginMemberContext;
import com.chenmeng.train.common.resp.PageResp;
import com.chenmeng.train.common.util.SnowUtil;
import com.chenmeng.train.business.mapper.TrainSeatMapper;
import com.chenmeng.train.business.model.dto.TrainSeatQueryDTO;
import com.chenmeng.train.business.model.dto.TrainSeatSaveDTO;
import com.chenmeng.train.business.model.entity.TrainSeat;
import com.chenmeng.train.business.model.entity.TrainSeatExample;
import com.chenmeng.train.business.model.vo.TrainSeatQueryVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 座位表业务类
 *
 * @author 沉梦听雨
 **/
@Service
public class TrainSeatService {

    @Resource
    private TrainSeatMapper trainSeatMapper;

    private static final Logger LOG = LoggerFactory.getLogger(TrainSeatService.class);

    public void save(TrainSeatSaveDTO req) {
        DateTime now = DateTime.now();
        TrainSeat trainSeat = BeanUtil.copyProperties(req, TrainSeat.class);
        if (ObjectUtil.isNull(trainSeat.getId())) {
            trainSeat.setId(SnowUtil.getSnowflakeNextId());
            trainSeat.setCreateTime(now);
            trainSeat.setUpdateTime(now);
            trainSeatMapper.insert(trainSeat);
        } else {
            trainSeat.setUpdateTime(now);
            trainSeatMapper.updateByPrimaryKey(trainSeat);
        }
    }

    public PageResp<TrainSeatQueryVO> queryList(TrainSeatQueryDTO req) {
        // 创建一个 TrainSeatExample 对象
        TrainSeatExample trainSeatExample = new TrainSeatExample();
        // 实现 id 降序
        trainSeatExample.setOrderByClause("id desc");

        // 创建一个 TrainSeatExample.Criteria 对象
        TrainSeatExample.Criteria criteria = trainSeatExample.createCriteria();

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        // 相当于在下面的查询 sql 尾部插入一个 limit
        PageHelper.startPage(req.getPage(), req.getSize());
        // 根据条件查询
        List<TrainSeat> trainSeatList = trainSeatMapper.selectByExample(trainSeatExample);

        // 获取分页列表的总行数和总页数
        PageInfo<TrainSeat> pageInfo = new PageInfo<>(trainSeatList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        // 将查询结果转换为 TrainSeatQueryVO 对象
        List<TrainSeatQueryVO> list = BeanUtil.copyToList(trainSeatList, TrainSeatQueryVO.class);

        // 创建一个 PageResp 对象
        PageResp<TrainSeatQueryVO> pageResp = new PageResp<>();
        // 设置总行数
        pageResp.setTotal(pageInfo.getTotal());
        // 设置查询结果
        pageResp.setList(list);

        return pageResp;
    }

    public void delete(Long id) {
        trainSeatMapper.deleteByPrimaryKey(id);
    }
}
