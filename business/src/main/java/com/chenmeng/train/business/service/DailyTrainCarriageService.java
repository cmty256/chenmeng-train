package com.chenmeng.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.chenmeng.train.common.context.LoginMemberContext;
import com.chenmeng.train.common.resp.PageResp;
import com.chenmeng.train.common.util.SnowUtil;
import com.chenmeng.train.business.mapper.DailyTrainCarriageMapper;
import com.chenmeng.train.business.model.dto.DailyTrainCarriageQueryDTO;
import com.chenmeng.train.business.model.dto.DailyTrainCarriageSaveDTO;
import com.chenmeng.train.business.model.entity.DailyTrainCarriage;
import com.chenmeng.train.business.model.entity.DailyTrainCarriageExample;
import com.chenmeng.train.business.model.vo.DailyTrainCarriageQueryVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 每日车厢表业务类
 *
 * @author 沉梦听雨
 **/
@Service
public class DailyTrainCarriageService {

    @Resource
    private DailyTrainCarriageMapper dailyTrainCarriageMapper;

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainCarriageService.class);

    public void save(DailyTrainCarriageSaveDTO req) {
        DateTime now = DateTime.now();
        DailyTrainCarriage dailyTrainCarriage = BeanUtil.copyProperties(req, DailyTrainCarriage.class);
        if (ObjectUtil.isNull(dailyTrainCarriage.getId())) {
            dailyTrainCarriage.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainCarriage.setCreateTime(now);
            dailyTrainCarriage.setUpdateTime(now);
            dailyTrainCarriageMapper.insert(dailyTrainCarriage);
        } else {
            dailyTrainCarriage.setUpdateTime(now);
            dailyTrainCarriageMapper.updateByPrimaryKey(dailyTrainCarriage);
        }
    }

    public PageResp<DailyTrainCarriageQueryVO> queryList(DailyTrainCarriageQueryDTO req) {
        // 创建一个 DailyTrainCarriageExample 对象
        DailyTrainCarriageExample dailyTrainCarriageExample = new DailyTrainCarriageExample();
        // 实现 id 降序
        dailyTrainCarriageExample.setOrderByClause("id desc");

        // 创建一个 DailyTrainCarriageExample.Criteria 对象（类似 MP 的条件构造器）
        DailyTrainCarriageExample.Criteria criteria = dailyTrainCarriageExample.createCriteria();

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        // 相当于在下面的查询 sql 尾部插入一个 limit
        PageHelper.startPage(req.getPage(), req.getSize());
        // 根据条件查询
        List<DailyTrainCarriage> dailyTrainCarriageList = dailyTrainCarriageMapper.selectByExample(dailyTrainCarriageExample);

        // 获取分页列表的总行数和总页数
        PageInfo<DailyTrainCarriage> pageInfo = new PageInfo<>(dailyTrainCarriageList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        // 将查询结果转换为 DailyTrainCarriageQueryVO 对象
        List<DailyTrainCarriageQueryVO> list = BeanUtil.copyToList(dailyTrainCarriageList, DailyTrainCarriageQueryVO.class);

        // 创建一个 PageResp 对象
        PageResp<DailyTrainCarriageQueryVO> pageResp = new PageResp<>();
        // 设置总行数
        pageResp.setTotal(pageInfo.getTotal());
        // 设置查询结果
        pageResp.setList(list);

        return pageResp;
    }

    public void delete(Long id) {
        dailyTrainCarriageMapper.deleteByPrimaryKey(id);
    }
}
