package com.chenmeng.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.chenmeng.train.common.context.LoginMemberContext;
import com.chenmeng.train.common.resp.PageResp;
import com.chenmeng.train.common.util.SnowUtil;
import com.chenmeng.train.business.mapper.DailyTrainMapper;
import com.chenmeng.train.business.model.dto.DailyTrainQueryDTO;
import com.chenmeng.train.business.model.dto.DailyTrainSaveDTO;
import com.chenmeng.train.business.model.entity.DailyTrain;
import com.chenmeng.train.business.model.entity.DailyTrainExample;
import com.chenmeng.train.business.model.vo.DailyTrainQueryVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 每日车次表业务类
 *
 * @author 沉梦听雨
 **/
@Service
public class DailyTrainService {

    @Resource
    private DailyTrainMapper dailyTrainMapper;

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
        // 实现 id 降序
        dailyTrainExample.setOrderByClause("id desc");

        // 创建一个 DailyTrainExample.Criteria 对象（类似 MP 的条件构造器）
        DailyTrainExample.Criteria criteria = dailyTrainExample.createCriteria();

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
}
