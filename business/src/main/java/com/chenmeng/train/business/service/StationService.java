package com.chenmeng.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.chenmeng.train.business.mapper.StationMapper;
import com.chenmeng.train.business.model.dto.StationQueryDTO;
import com.chenmeng.train.business.model.dto.StationSaveDTO;
import com.chenmeng.train.business.model.entity.Station;
import com.chenmeng.train.business.model.entity.StationExample;
import com.chenmeng.train.business.model.vo.StationQueryVO;
import com.chenmeng.train.common.exception.BusinessException;
import com.chenmeng.train.common.exception.BusinessExceptionEnum;
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
 * 车站表业务类
 *
 * @author 沉梦听雨
 **/
@Service
public class StationService {

    @Resource
    private StationMapper stationMapper;

    private static final Logger LOG = LoggerFactory.getLogger(StationService.class);

    public void save(StationSaveDTO req) {
        DateTime now = DateTime.now();
        Station station = BeanUtil.copyProperties(req, Station.class);
        if (ObjectUtil.isNull(station.getId())) {

            // 保存之前，先校验唯一键是否存在
            Station stationDB = selectByUnique(req.getName());
            if (ObjectUtil.isNotEmpty(stationDB)) {
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_STATION_NAME_UNIQUE_ERROR);
            }

            station.setId(SnowUtil.getSnowflakeNextId());
            station.setCreateTime(now);
            station.setUpdateTime(now);
            stationMapper.insert(station);
        } else {
            station.setUpdateTime(now);
            stationMapper.updateByPrimaryKey(station);
        }
    }

    public PageResp<StationQueryVO> queryList(StationQueryDTO req) {
        // 创建一个 StationExample 对象
        StationExample stationExample = new StationExample();
        // 实现 id 降序
        stationExample.setOrderByClause("id desc");

        // 创建一个 StationExample.Criteria 对象
        StationExample.Criteria criteria = stationExample.createCriteria();

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        // 相当于在下面的查询 sql 尾部插入一个 limit
        PageHelper.startPage(req.getPage(), req.getSize());
        // 根据条件查询
        List<Station> stationList = stationMapper.selectByExample(stationExample);

        // 获取分页列表的总行数和总页数
        PageInfo<Station> pageInfo = new PageInfo<>(stationList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        // 将查询结果转换为 StationQueryVO 对象
        List<StationQueryVO> list = BeanUtil.copyToList(stationList, StationQueryVO.class);

        // 创建一个 PageResp 对象
        PageResp<StationQueryVO> pageResp = new PageResp<>();
        // 设置总行数
        pageResp.setTotal(pageInfo.getTotal());
        // 设置查询结果
        pageResp.setList(list);

        return pageResp;
    }

    public void delete(Long id) {
        stationMapper.deleteByPrimaryKey(id);
    }

    public List<StationQueryVO> queryAll() {
        StationExample stationExample = new StationExample();
        // 根据站名拼音升序
        stationExample.setOrderByClause("name_pinyin asc");
        List<Station> stationList = stationMapper.selectByExample(stationExample);
        return BeanUtil.copyToList(stationList, StationQueryVO.class);
    }

    private Station selectByUnique(String name) {
        StationExample stationExample = new StationExample();
        stationExample.createCriteria().andNameEqualTo(name);
        List<Station> list = stationMapper.selectByExample(stationExample);
        if (CollUtil.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }
}
