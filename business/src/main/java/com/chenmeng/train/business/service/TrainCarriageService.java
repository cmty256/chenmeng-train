package com.chenmeng.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.chenmeng.train.business.mapper.TrainCarriageMapper;
import com.chenmeng.train.business.model.dto.TrainCarriageQueryDTO;
import com.chenmeng.train.business.model.dto.TrainCarriageSaveDTO;
import com.chenmeng.train.business.model.entity.TrainCarriage;
import com.chenmeng.train.business.model.entity.TrainCarriageExample;
import com.chenmeng.train.business.model.vo.TrainCarriageQueryVO;
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
 * 列车车厢表业务类
 *
 * @author 沉梦听雨
 **/
@Service
public class TrainCarriageService {

    @Resource
    private TrainCarriageMapper trainCarriageMapper;

    private static final Logger LOG = LoggerFactory.getLogger(TrainCarriageService.class);

    public void save(TrainCarriageSaveDTO req) {
        DateTime now = DateTime.now();
        TrainCarriage trainCarriage = BeanUtil.copyProperties(req, TrainCarriage.class);
        if (ObjectUtil.isNull(trainCarriage.getId())) {
            trainCarriage.setId(SnowUtil.getSnowflakeNextId());
            trainCarriage.setCreateTime(now);
            trainCarriage.setUpdateTime(now);
            trainCarriageMapper.insert(trainCarriage);
        } else {
            trainCarriage.setUpdateTime(now);
            trainCarriageMapper.updateByPrimaryKey(trainCarriage);
        }
    }

    public PageResp<TrainCarriageQueryVO> queryList(TrainCarriageQueryDTO req) {
        // 创建一个 TrainCarriageExample 对象
        TrainCarriageExample trainCarriageExample = new TrainCarriageExample();
        // 实现 车次编号、车厢号 升序查询
        trainCarriageExample.setOrderByClause("train_code asc, `index` asc");
        // 创建一个 TrainStationExample.Criteria 对象
        TrainCarriageExample.Criteria criteria = trainCarriageExample.createCriteria();
        if (ObjectUtil.isNotEmpty(req.getTrainCode())) {
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        // 相当于在下面的查询 sql 尾部插入一个 limit
        PageHelper.startPage(req.getPage(), req.getSize());
        // 根据条件查询
        List<TrainCarriage> trainCarriageList = trainCarriageMapper.selectByExample(trainCarriageExample);

        // 获取分页列表的总行数和总页数
        PageInfo<TrainCarriage> pageInfo = new PageInfo<>(trainCarriageList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        // 将查询结果转换为 TrainCarriageQueryVO 对象
        List<TrainCarriageQueryVO> list = BeanUtil.copyToList(trainCarriageList, TrainCarriageQueryVO.class);

        // 创建一个 PageResp 对象
        PageResp<TrainCarriageQueryVO> pageResp = new PageResp<>();
        // 设置总行数
        pageResp.setTotal(pageInfo.getTotal());
        // 设置查询结果
        pageResp.setList(list);

        return pageResp;
    }

    public void delete(Long id) {
        trainCarriageMapper.deleteByPrimaryKey(id);
    }
}
