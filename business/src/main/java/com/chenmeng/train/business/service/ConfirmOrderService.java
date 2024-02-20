package com.chenmeng.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.chenmeng.train.business.mapper.ConfirmOrderMapper;
import com.chenmeng.train.business.model.dto.ConfirmOrderDoDTO;
import com.chenmeng.train.business.model.dto.ConfirmOrderQueryDTO;
import com.chenmeng.train.business.model.dto.ConfirmOrderSaveDTO;
import com.chenmeng.train.business.model.entity.ConfirmOrder;
import com.chenmeng.train.business.model.entity.ConfirmOrderExample;
import com.chenmeng.train.business.model.vo.ConfirmOrderQueryVO;
import com.chenmeng.train.common.resp.PageResp;
import com.chenmeng.train.common.util.SnowUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 确认订单表业务类
 *
 * @author 沉梦听雨
 **/
@Service
public class ConfirmOrderService {

    @Resource
    private ConfirmOrderMapper confirmOrderMapper;

    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderService.class);

    public void save(ConfirmOrderSaveDTO req) {
        DateTime now = DateTime.now();
        ConfirmOrder confirmOrder = BeanUtil.copyProperties(req, ConfirmOrder.class);
        if (ObjectUtil.isNull(confirmOrder.getId())) {
            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.insert(confirmOrder);
        } else {
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.updateByPrimaryKey(confirmOrder);
        }
    }

    public PageResp<ConfirmOrderQueryVO> queryList(ConfirmOrderQueryDTO req) {
        // 创建一个 ConfirmOrderExample 对象
        ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
        // 实现 id 降序
        confirmOrderExample.setOrderByClause("id desc");

        // 创建一个 ConfirmOrderExample.Criteria 对象（类似 MP 的条件构造器）
        ConfirmOrderExample.Criteria criteria = confirmOrderExample.createCriteria();

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        // 相当于在下面的查询 sql 尾部插入一个 limit
        PageHelper.startPage(req.getPage(), req.getSize());
        // 根据条件查询
        List<ConfirmOrder> confirmOrderList = confirmOrderMapper.selectByExample(confirmOrderExample);

        // 获取分页列表的总行数和总页数
        PageInfo<ConfirmOrder> pageInfo = new PageInfo<>(confirmOrderList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        // 将查询结果转换为 ConfirmOrderQueryVO 对象
        List<ConfirmOrderQueryVO> list = BeanUtil.copyToList(confirmOrderList, ConfirmOrderQueryVO.class);

        // 创建一个 PageResp 对象
        PageResp<ConfirmOrderQueryVO> pageResp = new PageResp<>();
        // 设置总行数
        pageResp.setTotal(pageInfo.getTotal());
        // 设置查询结果
        pageResp.setList(list);

        return pageResp;
    }

    public void delete(Long id) {
        confirmOrderMapper.deleteByPrimaryKey(id);
    }

    public void doConfirm(ConfirmOrderDoDTO dto) {
        // 省略业务数据校验，如：车次是否存在，余票是否存在，车次是否在有效期内，tickets条数>0，同乘客同车次是否已买过

        // 保存确认订单表，状态初始
        // 查出余票记录，需要得到真实的库存
        // 预扣减余票数量，并判断余票是否足够

        // 选座
            // 一个车箱一个车箱的获取座位数据
            // 挑选符合条件的座位，如果这个车箱不满足，则进入下个车箱(多个选座应该在同一个车厢)

        // 选中座位后的事务处理
            // 座位表修改售卖情况sell
            // 余票详情表修改余票
            // 为会员增加购票记录
            // 更新确认订单为成功
    }
}
