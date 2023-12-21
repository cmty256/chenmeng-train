package com.chenmeng.train.${module}.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.chenmeng.train.common.context.LoginMemberContext;
import com.chenmeng.train.common.resp.PageResp;
import com.chenmeng.train.common.util.SnowUtil;
import com.chenmeng.train.${module}.mapper.${Domain}Mapper;
import com.chenmeng.train.${module}.model.dto.${Domain}QueryDTO;
import com.chenmeng.train.${module}.model.dto.${Domain}SaveDTO;
import com.chenmeng.train.${module}.model.entity.${Domain};
import com.chenmeng.train.${module}.model.entity.${Domain}Example;
import com.chenmeng.train.${module}.model.vo.${Domain}QueryVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ${tableNameCn}表业务类
 *
 * @author 沉梦听雨
 **/
@Service
public class ${Domain}Service {

    @Resource
    private ${Domain}Mapper ${domain}Mapper;

    private static final Logger LOG = LoggerFactory.getLogger(${Domain}Service.class);

    public void save(${Domain}SaveDTO req) {
        DateTime now = DateTime.now();
        ${Domain} ${domain} = BeanUtil.copyProperties(req, ${Domain}.class);
        if (ObjectUtil.isNull(${domain}.getId())) {
            ${domain}.setId(SnowUtil.getSnowflakeNextId());
            ${domain}.setCreateTime(now);
            ${domain}.setUpdateTime(now);
            ${domain}Mapper.insert(${domain});
        } else {
            ${domain}.setUpdateTime(now);
            ${domain}Mapper.updateByPrimaryKey(${domain});
        }
    }

    public PageResp<${Domain}QueryVO> queryList(${Domain}QueryDTO req) {
        // 创建一个 ${Domain}Example 对象
        ${Domain}Example ${domain}Example = new ${Domain}Example();
        // 实现 id 降序
        ${domain}Example.setOrderByClause("id desc");

        // 创建一个 ${Domain}Example.Criteria 对象
        ${Domain}Example.Criteria criteria = ${domain}Example.createCriteria();

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        // 相当于在下面的查询 sql 尾部插入一个 limit
        PageHelper.startPage(req.getPage(), req.getSize());
        // 根据条件查询
        List<${Domain}> ${domain}List = ${domain}Mapper.selectByExample(${domain}Example);

        // 获取分页列表的总行数和总页数
        PageInfo<${Domain}> pageInfo = new PageInfo<>(${domain}List);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        // 将查询结果转换为 ${Domain}QueryVO 对象
        List<${Domain}QueryVO> list = BeanUtil.copyToList(${domain}List, ${Domain}QueryVO.class);

        // 创建一个 PageResp 对象
        PageResp<${Domain}QueryVO> pageResp = new PageResp<>();
        // 设置总行数
        pageResp.setTotal(pageInfo.getTotal());
        // 设置查询结果
        pageResp.setList(list);

        return pageResp;
    }

    public void delete(Long id) {
        ${domain}Mapper.deleteByPrimaryKey(id);
    }
}
