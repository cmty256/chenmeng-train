package com.chenmeng.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.chenmeng.train.common.req.MemberTicketReq;
import com.chenmeng.train.common.resp.PageResp;
import com.chenmeng.train.common.util.SnowUtil;
import com.chenmeng.train.member.mapper.TicketMapper;
import com.chenmeng.train.member.model.dto.TicketQueryDTO;
import com.chenmeng.train.member.model.entity.Ticket;
import com.chenmeng.train.member.model.entity.TicketExample;
import com.chenmeng.train.member.model.vo.TicketQueryVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 车票表业务类
 *
 * @author 沉梦听雨
 **/
@Service
public class TicketService {

    @Resource
    private TicketMapper ticketMapper;

    private static final Logger LOG = LoggerFactory.getLogger(TicketService.class);

    /**
     * 会员购买车票后新增保存
     *
     * @param req
     */
    public void save(MemberTicketReq req) throws Exception {
        DateTime now = DateTime.now();
        Ticket ticket = BeanUtil.copyProperties(req, Ticket.class);
        ticket.setId(SnowUtil.getSnowflakeNextId());
        ticket.setCreateTime(now);
        ticket.setUpdateTime(now);
        ticketMapper.insert(ticket);
    }

    public PageResp<TicketQueryVO> queryList(TicketQueryDTO req) {
        // 创建一个 TicketExample 对象
        TicketExample ticketExample = new TicketExample();
        // 实现 id 降序
        ticketExample.setOrderByClause("id desc");

        // 创建一个 TicketExample.Criteria 对象（类似 MP 的条件构造器）
        TicketExample.Criteria criteria = ticketExample.createCriteria();

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        // 相当于在下面的查询 sql 尾部插入一个 limit
        PageHelper.startPage(req.getPage(), req.getSize());
        // 根据条件查询
        List<Ticket> ticketList = ticketMapper.selectByExample(ticketExample);

        // 获取分页列表的总行数和总页数
        PageInfo<Ticket> pageInfo = new PageInfo<>(ticketList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        // 将查询结果转换为 TicketQueryVO 对象
        List<TicketQueryVO> list = BeanUtil.copyToList(ticketList, TicketQueryVO.class);

        // 创建一个 PageResp 对象
        PageResp<TicketQueryVO> pageResp = new PageResp<>();
        // 设置总行数
        pageResp.setTotal(pageInfo.getTotal());
        // 设置查询结果
        pageResp.setList(list);

        return pageResp;
    }

    public void delete(Long id) {
        ticketMapper.deleteByPrimaryKey(id);
    }
}
