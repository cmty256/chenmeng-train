package com.chenmeng.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.chenmeng.train.common.context.LoginMemberContext;
import com.chenmeng.train.common.resp.PageResp;
import com.chenmeng.train.common.util.SnowUtil;
import com.chenmeng.train.business.mapper.DailyTrainTicketMapper;
import com.chenmeng.train.business.model.dto.DailyTrainTicketQueryDTO;
import com.chenmeng.train.business.model.dto.DailyTrainTicketSaveDTO;
import com.chenmeng.train.business.model.entity.DailyTrainTicket;
import com.chenmeng.train.business.model.entity.DailyTrainTicketExample;
import com.chenmeng.train.business.model.vo.DailyTrainTicketQueryVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 余票信息表业务类
 *
 * @author 沉梦听雨
 **/
@Service
public class DailyTrainTicketService {

    @Resource
    private DailyTrainTicketMapper dailyTrainTicketMapper;

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainTicketService.class);

    public void save(DailyTrainTicketSaveDTO req) {
        DateTime now = DateTime.now();
        DailyTrainTicket dailyTrainTicket = BeanUtil.copyProperties(req, DailyTrainTicket.class);
        if (ObjectUtil.isNull(dailyTrainTicket.getId())) {
            dailyTrainTicket.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainTicket.setCreateTime(now);
            dailyTrainTicket.setUpdateTime(now);
            dailyTrainTicketMapper.insert(dailyTrainTicket);
        } else {
            dailyTrainTicket.setUpdateTime(now);
            dailyTrainTicketMapper.updateByPrimaryKey(dailyTrainTicket);
        }
    }

    public PageResp<DailyTrainTicketQueryVO> queryList(DailyTrainTicketQueryDTO req) {
        // 创建一个 DailyTrainTicketExample 对象
        DailyTrainTicketExample dailyTrainTicketExample = new DailyTrainTicketExample();
        // 实现 id 降序
        dailyTrainTicketExample.setOrderByClause("id desc");

        // 创建一个 DailyTrainTicketExample.Criteria 对象（类似 MP 的条件构造器）
        DailyTrainTicketExample.Criteria criteria = dailyTrainTicketExample.createCriteria();

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        // 相当于在下面的查询 sql 尾部插入一个 limit
        PageHelper.startPage(req.getPage(), req.getSize());
        // 根据条件查询
        List<DailyTrainTicket> dailyTrainTicketList = dailyTrainTicketMapper.selectByExample(dailyTrainTicketExample);

        // 获取分页列表的总行数和总页数
        PageInfo<DailyTrainTicket> pageInfo = new PageInfo<>(dailyTrainTicketList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        // 将查询结果转换为 DailyTrainTicketQueryVO 对象
        List<DailyTrainTicketQueryVO> list = BeanUtil.copyToList(dailyTrainTicketList, DailyTrainTicketQueryVO.class);

        // 创建一个 PageResp 对象
        PageResp<DailyTrainTicketQueryVO> pageResp = new PageResp<>();
        // 设置总行数
        pageResp.setTotal(pageInfo.getTotal());
        // 设置查询结果
        pageResp.setList(list);

        return pageResp;
    }

    public void delete(Long id) {
        dailyTrainTicketMapper.deleteByPrimaryKey(id);
    }
}
