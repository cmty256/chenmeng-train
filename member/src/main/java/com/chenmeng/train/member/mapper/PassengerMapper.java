package com.chenmeng.train.member.mapper;

import com.chenmeng.train.member.model.entity.Passenger;
import com.chenmeng.train.member.model.entity.PassengerExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 沉梦
 */
public interface PassengerMapper {
    long countByExample(PassengerExample example);

    int deleteByExample(PassengerExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Passenger record);

    int insertSelective(Passenger record);

    List<Passenger> selectByExample(PassengerExample example);

    Passenger selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Passenger record, @Param("example") PassengerExample example);

    int updateByExample(@Param("record") Passenger record, @Param("example") PassengerExample example);

    int updateByPrimaryKeySelective(Passenger record);

    int updateByPrimaryKey(Passenger record);
}