package com.chenmeng.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.chenmeng.train.common.context.LoginMemberContext;
import com.chenmeng.train.common.resp.PageResp;
import com.chenmeng.train.common.util.SnowUtil;
import com.chenmeng.train.business.mapper.SkTokenMapper;
import com.chenmeng.train.business.model.dto.SkTokenQueryDTO;
import com.chenmeng.train.business.model.dto.SkTokenSaveDTO;
import com.chenmeng.train.business.model.entity.SkToken;
import com.chenmeng.train.business.model.entity.SkTokenExample;
import com.chenmeng.train.business.model.vo.SkTokenQueryVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 秒杀令牌表业务类
 *
 * @author 沉梦听雨
 **/
@Service
public class SkTokenService {

    @Resource
    private SkTokenMapper skTokenMapper;

    private static final Logger LOG = LoggerFactory.getLogger(SkTokenService.class);

    public void save(SkTokenSaveDTO req) {
        DateTime now = DateTime.now();
        SkToken skToken = BeanUtil.copyProperties(req, SkToken.class);
        if (ObjectUtil.isNull(skToken.getId())) {
            skToken.setId(SnowUtil.getSnowflakeNextId());
            skToken.setCreateTime(now);
            skToken.setUpdateTime(now);
            skTokenMapper.insert(skToken);
        } else {
            skToken.setUpdateTime(now);
            skTokenMapper.updateByPrimaryKey(skToken);
        }
    }

    public PageResp<SkTokenQueryVO> queryList(SkTokenQueryDTO req) {
        // 创建一个 SkTokenExample 对象
        SkTokenExample skTokenExample = new SkTokenExample();
        // 实现 id 降序
        skTokenExample.setOrderByClause("id desc");

        // 创建一个 SkTokenExample.Criteria 对象（类似 MP 的条件构造器）
        SkTokenExample.Criteria criteria = skTokenExample.createCriteria();

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        // 相当于在下面的查询 sql 尾部插入一个 limit
        PageHelper.startPage(req.getPage(), req.getSize());
        // 根据条件查询
        List<SkToken> skTokenList = skTokenMapper.selectByExample(skTokenExample);

        // 获取分页列表的总行数和总页数
        PageInfo<SkToken> pageInfo = new PageInfo<>(skTokenList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        // 将查询结果转换为 SkTokenQueryVO 对象
        List<SkTokenQueryVO> list = BeanUtil.copyToList(skTokenList, SkTokenQueryVO.class);

        // 创建一个 PageResp 对象
        PageResp<SkTokenQueryVO> pageResp = new PageResp<>();
        // 设置总行数
        pageResp.setTotal(pageInfo.getTotal());
        // 设置查询结果
        pageResp.setList(list);

        return pageResp;
    }

    public void delete(Long id) {
        skTokenMapper.deleteByPrimaryKey(id);
    }
}
