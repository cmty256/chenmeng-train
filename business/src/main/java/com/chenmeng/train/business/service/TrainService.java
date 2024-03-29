package com.chenmeng.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.chenmeng.train.business.mapper.TrainMapper;
import com.chenmeng.train.business.model.dto.TrainQueryDTO;
import com.chenmeng.train.business.model.dto.TrainSaveDTO;
import com.chenmeng.train.business.model.entity.Train;
import com.chenmeng.train.business.model.entity.TrainExample;
import com.chenmeng.train.business.model.vo.TrainQueryVO;
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

import java.util.List;

/**
 * 车次表业务类
 *
 * @author 沉梦听雨
 **/
@Service
public class TrainService {

    @Resource
    private TrainMapper trainMapper;

    private static final Logger LOG = LoggerFactory.getLogger(TrainService.class);

    public void save(TrainSaveDTO req) {
        DateTime now = DateTime.now();
        Train train = BeanUtil.copyProperties(req, Train.class);
        if (ObjectUtil.isNull(train.getId())) {

            // 保存之前，先校验唯一键是否存在
            Train trainDB = selectByUnique(req.getCode());
            if (ObjectUtil.isNotEmpty(trainDB)) {
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_CODE_UNIQUE_ERROR);
            }

            train.setId(SnowUtil.getSnowflakeNextId());
            train.setCreateTime(now);
            train.setUpdateTime(now);
            trainMapper.insert(train);
        } else {
            train.setUpdateTime(now);
            trainMapper.updateByPrimaryKey(train);
        }
    }

    public PageResp<TrainQueryVO> queryList(TrainQueryDTO req) {
        // 创建一个 TrainExample 对象
        TrainExample trainExample = new TrainExample();
        // 按 车次编号 升序
        trainExample.setOrderByClause("code asc");

        // 创建一个 TrainExample.Criteria 对象
        TrainExample.Criteria criteria = trainExample.createCriteria();

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        // 相当于在下面的查询 sql 尾部插入一个 limit
        PageHelper.startPage(req.getPage(), req.getSize());
        // 根据条件查询
        List<Train> trainList = trainMapper.selectByExample(trainExample);

        // 获取分页列表的总行数和总页数
        PageInfo<Train> pageInfo = new PageInfo<>(trainList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        // 将查询结果转换为 TrainQueryVO 对象
        List<TrainQueryVO> list = BeanUtil.copyToList(trainList, TrainQueryVO.class);

        // 创建一个 PageResp 对象
        PageResp<TrainQueryVO> pageResp = new PageResp<>();
        // 设置总行数
        pageResp.setTotal(pageInfo.getTotal());
        // 设置查询结果
        pageResp.setList(list);

        return pageResp;
    }

    public void delete(Long id) {
        trainMapper.deleteByPrimaryKey(id);
    }

    public List<TrainQueryVO> queryAll() {
        List<Train> trainList = this.selectAll();
        return BeanUtil.copyToList(trainList, TrainQueryVO.class);
    }

    public List<Train> selectAll() {
        TrainExample trainExample = new TrainExample();
        // 根据车次编号升序
        trainExample.setOrderByClause("code asc");
        return trainMapper.selectByExample(trainExample);
    }

    private Train selectByUnique(String code) {
        TrainExample trainExample = new TrainExample();
        trainExample.createCriteria()
                .andCodeEqualTo(code);
        List<Train> list = trainMapper.selectByExample(trainExample);
        if (CollUtil.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }
}
