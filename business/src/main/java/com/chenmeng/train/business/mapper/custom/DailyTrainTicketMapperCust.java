package com.chenmeng.train.business.mapper.custom;

import java.util.Date;

/**
 * 选中座位后的事务处理
 *
 * @author 沉梦听雨
 */
public interface DailyTrainTicketMapperCust {

    void updateCountBySell(Date date
            , String trainCode
            , String seatTypeCode
            , Integer minStartIndex
            , Integer maxStartIndex
            , Integer minEndIndex
            , Integer maxEndIndex);
}