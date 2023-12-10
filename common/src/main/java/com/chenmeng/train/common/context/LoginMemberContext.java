package com.chenmeng.train.common.context;

import com.chenmeng.train.common.vo.MemberLoginVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 线程本地变量, 用于存储登录用户信息
 *
 * @author 沉梦听雨
 */
public class LoginMemberContext {

    private static final Logger LOG = LoggerFactory.getLogger(LoginMemberContext.class);

    /**
     * 创建一个线程本地变量, 类型值为 MemberLoginVO
     */
    private static ThreadLocal<MemberLoginVO> member = new ThreadLocal<>();

    /**
     * 获取登录用户信息
     *
     * @return 登录用户信息
     */
    public static MemberLoginVO getMember() {
        return member.get();
    }

    /**
     * 设置登录用户信息
     *
     * @param member 登录用户信息
     */
    public static void setMember(MemberLoginVO member) {
        LoginMemberContext.member.set(member);
    }

    /**
     * 获取登录用户id
     *
     * @return 登录用户id
     */
    public static Long getId() {
        try {
            return member.get().getId();
        } catch (Exception e) {
            LOG.error("获取登录会员信息异常", e);
            throw e;
        }
    }
}
