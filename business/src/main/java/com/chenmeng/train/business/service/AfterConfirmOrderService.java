package com.chenmeng.train.business.service;

import com.chenmeng.train.business.mapper.DailyTrainSeatMapper;
import com.chenmeng.train.business.model.dto.ConfirmOrderTicketDTO;
import com.chenmeng.train.business.model.entity.ConfirmOrder;
import com.chenmeng.train.business.model.entity.DailyTrainSeat;
import com.chenmeng.train.business.model.entity.DailyTrainTicket;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 选中座位后的事务处理
 *
 * @author 沉梦听雨
 */
@Service
public class AfterConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(AfterConfirmOrderService.class);

    @Resource
    private DailyTrainSeatMapper dailyTrainSeatMapper;

    /**
     * 选中座位后事务处理：
     *  根据最终选中座位列表，修改座位表售卖情况sell
     *  余票详情表修改余票
     *  为会员增加购票记录
     *  更新确认订单为成功
     */
    @Transactional(rollbackFor = Exception.class)
    public void afterDoConfirm(DailyTrainTicket dailyTrainTicket, List<DailyTrainSeat> finalSeatList,
                               List<ConfirmOrderTicketDTO> tickets, ConfirmOrder confirmOrder) throws Exception {
        for (int j = 0; j < finalSeatList.size(); j++) {
            // 1、修改座位表售卖情况sell
            DailyTrainSeat dailyTrainSeat = finalSeatList.get(j);
            DailyTrainSeat seatForUpdate = new DailyTrainSeat();
            seatForUpdate.setId(dailyTrainSeat.getId());
            seatForUpdate.setSell(dailyTrainSeat.getSell());
            seatForUpdate.setUpdateTime(new Date());
            // 只更新指定的列
            dailyTrainSeatMapper.updateByPrimaryKeySelective(seatForUpdate);
        }
    }
}
