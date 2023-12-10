package com.chenmeng.train.common.exception;

/**
 * 业务异常类
 *
 * @author 沉梦听雨
 */
public class BusinessException extends RuntimeException {

    private BusinessExceptionEnum e;

    public BusinessException(BusinessExceptionEnum e) {
        this.e = e;
    }

    public BusinessExceptionEnum getE() {
        return e;
    }

    public void setE(BusinessExceptionEnum e) {
        this.e = e;
    }

    /**
     * 不写入堆栈信息，提高性能
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
