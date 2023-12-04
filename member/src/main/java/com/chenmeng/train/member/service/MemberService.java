package com.chenmeng.train.member.service;

import com.chenmeng.train.member.mapper.MemberMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 会员业务类
 *
 * @author 沉梦
 * @date 2023/12/04 16:06
 **/
@Service
public class MemberService {

    @Resource
    private MemberMapper memberMapper;

    public int count() {
        return Math.toIntExact(memberMapper.countByExample(null));
    }
}
