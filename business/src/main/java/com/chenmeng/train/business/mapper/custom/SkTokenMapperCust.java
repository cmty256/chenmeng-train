package com.chenmeng.train.business.mapper.custom;

import java.util.Date;

/**
 * 自定义秒杀令牌 Mapper
 *
 * @author 沉梦听雨
 */
public interface SkTokenMapperCust {

    /**
     * 减少令牌库存
     *
     * @param date 日期
     * @param trainCode 车次
     * @param decreaseCount 扣减数量
     * @return
     */
    int decrease(Date date, String trainCode, int decreaseCount);
}
