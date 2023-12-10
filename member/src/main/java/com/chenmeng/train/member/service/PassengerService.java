package com.chenmeng.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.chenmeng.train.common.util.SnowUtil;
import com.chenmeng.train.member.mapper.PassengerMapper;
import com.chenmeng.train.member.model.dto.PassengerSaveReq;
import com.chenmeng.train.member.model.entity.Passenger;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 乘车人表业务类
 *
 * @author 沉梦听雨
 **/
@Service
public class PassengerService {

    @Resource
    private PassengerMapper passengerMapper;

    public void save(PassengerSaveReq req) {
        DateTime now = DateTime.now();
        Passenger passenger = BeanUtil.copyProperties(req, Passenger.class);
        passenger.setId(SnowUtil.getSnowflakeNextId());
        passenger.setCreateTime(now);
        passenger.setUpdateTime(now);
        passengerMapper.insert(passenger);
    }
}
