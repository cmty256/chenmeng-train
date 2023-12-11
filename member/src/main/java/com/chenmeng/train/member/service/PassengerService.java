package com.chenmeng.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.chenmeng.train.common.context.LoginMemberContext;
import com.chenmeng.train.common.resp.PageResp;
import com.chenmeng.train.common.util.SnowUtil;
import com.chenmeng.train.member.mapper.PassengerMapper;
import com.chenmeng.train.member.model.dto.PassengerQueryReq;
import com.chenmeng.train.member.model.dto.PassengerSaveReq;
import com.chenmeng.train.member.model.entity.Passenger;
import com.chenmeng.train.member.model.entity.PassengerExample;
import com.chenmeng.train.member.model.vo.PassengerQueryVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 乘车人表业务类
 *
 * @author 沉梦听雨
 **/
@Service
public class PassengerService {

    @Resource
    private PassengerMapper passengerMapper;

    private static final Logger LOG = LoggerFactory.getLogger(PassengerService.class);

    public void save(PassengerSaveReq req) {
        DateTime now = DateTime.now();
        Passenger passenger = BeanUtil.copyProperties(req, Passenger.class);
        if (ObjectUtil.isNull(passenger.getId())) {
            passenger.setMemberId(LoginMemberContext.getId());
            passenger.setId(SnowUtil.getSnowflakeNextId());
            passenger.setCreateTime(now);
            passenger.setUpdateTime(now);
            passengerMapper.insert(passenger);
        } else {
            passenger.setUpdateTime(now);
            passengerMapper.updateByPrimaryKey(passenger);
        }
    }

    public PageResp<PassengerQueryVO> queryList(PassengerQueryReq req) {
        // 创建一个 PassengerExample 对象
        PassengerExample passengerExample = new PassengerExample();
        // 实现 id 降序
        passengerExample.setOrderByClause("id desc");

        // 创建一个 PassengerExample.Criteria 对象
        PassengerExample.Criteria criteria = passengerExample.createCriteria();
        // 如果 req 中的 memberId 不为空，则添加查询条件
        if (ObjectUtil.isNotNull(req.getMemberId())) {
            criteria.andMemberIdEqualTo(req.getMemberId());
        }

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        // 相当于在下面的查询 sql 尾部插入一个 limit
        PageHelper.startPage(req.getPage(), req.getSize());
        // 根据条件查询
        List<Passenger> passengerList = passengerMapper.selectByExample(passengerExample);

        // 获取分页列表的总行数和总页数
        PageInfo<Passenger> pageInfo = new PageInfo<>(passengerList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        // 将查询结果转换为 PassengerQueryVO 对象
        List<PassengerQueryVO> list = BeanUtil.copyToList(passengerList, PassengerQueryVO.class);

        // 创建一个 PageResp 对象
        PageResp<PassengerQueryVO> pageResp = new PageResp<>();
        // 设置总行数
        pageResp.setTotal(pageInfo.getTotal());
        // 设置查询结果
        pageResp.setList(list);

        return pageResp;
    }
}
