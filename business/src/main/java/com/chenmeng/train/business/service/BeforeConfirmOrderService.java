package com.chenmeng.train.business.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.json.JSONUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.chenmeng.train.business.enums.ConfirmOrderStatusEnum;
import com.chenmeng.train.business.enums.RocketMQTopicEnum;
import com.chenmeng.train.business.mapper.ConfirmOrderMapper;
import com.chenmeng.train.business.model.dto.ConfirmOrderDoDTO;
import com.chenmeng.train.business.model.dto.ConfirmOrderTicketDTO;
import com.chenmeng.train.business.model.entity.ConfirmOrder;
import com.chenmeng.train.common.context.LoginMemberContext;
import com.chenmeng.train.common.exception.BusinessException;
import com.chenmeng.train.common.exception.BusinessExceptionEnum;
import com.chenmeng.train.common.util.SnowUtil;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BeforeConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(BeforeConfirmOrderService.class);

    private final ConfirmOrderMapper confirmOrderMapper;
    private final SkTokenService skTokenService;
    private final RocketMQTemplate rocketMQTemplate;
    private final ConfirmOrderService confirmOrderService;
    ;

    @SentinelResource(value = "beforeDoConfirm", blockHandler = "beforeDoConfirmBlock")
    public Long beforeDoConfirm(ConfirmOrderDoDTO req) {
        Long id = null;
        // 根据前端传值，加入排队人数
        for (int i = 0; i < req.getLineNumber() + 1; i++) {
            req.setMemberId(LoginMemberContext.getId());
            // 校验令牌余量
            boolean validSkToken = skTokenService.validSkToken(req.getDate(),
                    req.getTrainCode(), LoginMemberContext.getId());
            if (validSkToken) {
                LOG.info("令牌校验通过");
            } else {
                LOG.info("令牌校验不通过");
                throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SK_TOKEN_FAIL);
            }

            Date date = req.getDate();
            String trainCode = req.getTrainCode();
            String start = req.getStart();
            String end = req.getEnd();
            List<ConfirmOrderTicketDTO> tickets = req.getTickets();

            // 保存确认订单表，状态初始
            DateTime now = DateTime.now();
            ConfirmOrder confirmOrder = new ConfirmOrder();
            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);
            confirmOrder.setMemberId(req.getMemberId());
            confirmOrder.setDate(date);
            confirmOrder.setTrainCode(trainCode);
            confirmOrder.setStart(start);
            confirmOrder.setEnd(end);
            confirmOrder.setDailyTrainTicketId(req.getDailyTrainTicketId());
            confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
            confirmOrder.setTickets(JSONUtil.toJsonStr(tickets));
            confirmOrderMapper.insert(confirmOrder);

            // 发送MQ排队购票
            String reqJson = JSON.toJSONString(req);
            LOG.info("排队购票，发送mq开始，消息：{}", reqJson);
            rocketMQTemplate.convertAndSend(RocketMQTopicEnum.CONFIRM_ORDER.getCode(), reqJson);
            LOG.info("排队购票，发送mq结束");
            confirmOrderService.doConfirm(req);
            id = confirmOrder.getId();
        }
        return id;
    }

    /**
     * 降级方法，需包含限流方法的所有参数和BlockException参数
     *
     * @param dto
     * @param e
     */
    public void beforeDoConfirmBlock(ConfirmOrderDoDTO dto, BlockException e) {
        LOG.info("购票请求被限流：{}", dto);
        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
    }
}
