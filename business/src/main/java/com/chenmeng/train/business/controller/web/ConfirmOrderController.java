package com.chenmeng.train.business.controller.web;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.chenmeng.train.business.model.dto.ConfirmOrderDoDTO;
import com.chenmeng.train.business.service.ConfirmOrderService;
import com.chenmeng.train.common.exception.BusinessExceptionEnum;
import com.chenmeng.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 确认订单表前端控制器
 *
 * @author 沉梦听雨
 **/
@RestController
@RequestMapping("/confirm-order")
public class ConfirmOrderController {

    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderController.class);

    @Resource
    private ConfirmOrderService confirmOrderService;

    /**
     * 确认订单（抢票）
     * <p>
     * 接口的资源名称不要和接口路径一致，会导致限流后走不到降级方法中
     * 对接口的限流可以不用提前为每个接口加资源
     *
     * @param dto
     * @return
     */
    @SentinelResource(value = "confirmOrderDo", blockHandler = "doConfirmBlock")
    @PostMapping("/do")
    public CommonResp<Object> doConfirm(@Valid @RequestBody ConfirmOrderDoDTO dto) {
        confirmOrderService.doConfirm(dto);
        return new CommonResp<>();
    }

    /**
     * 降级方法，需包含限流方法的所有参数和BlockException参数
     *
     * @param dto
     * @param e
     */
    public CommonResp<Object> doConfirmBlock(ConfirmOrderDoDTO dto, BlockException e) {
        LOG.info("购票请求被限流：{}", dto);
        // throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
        CommonResp<Object> commonResp = new CommonResp<>();
        commonResp.setSuccess(false);
        commonResp.setMessage(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION.getDesc());
        return commonResp;
    }
}
