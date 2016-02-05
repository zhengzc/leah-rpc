package com.zzc.proxy.result.impl;

import java.io.Serializable;

import com.zzc.proxy.result.Result;

/**
 * 默认的返回值
 *
 * @author ying
 */
public class DefaultResult implements Result, Serializable {
    private String token;
    private Object result;
    private Throwable throwable;
//    private transient Boolean isSuccess;

    private static final long serialVersionUID = -917929309672614713L;

    public DefaultResult(String token) {
        this.token = token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public String getToken() {
        return this.token;
    }

    @Override
    public Object getResult() {
        return this.result;
    }

    @Override
    public Throwable getException() {
        return this.throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

//    public Boolean getIsSuccess() {
//        return isSuccess;
//    }
//
//    public void setIsSuccess(Boolean isSuccess) {
//        this.isSuccess = isSuccess;
//    }
}
