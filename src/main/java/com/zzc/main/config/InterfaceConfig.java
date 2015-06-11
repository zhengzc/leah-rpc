package com.zzc.main.config;

/**
 * Created by ying on 15/6/2.
 * 客户端生成每个接口的动态代理的配置信息
 */
public class InterfaceConfig {
    /**
     * 接口的动态代理生成的实现类
     */
    private Object ref;
    /**
     * 接口
     */
    private Class itf;
    /**
     * 调用超时时间
     */
    private long timeout;
    /**
     * 调用类型
     */
    private CallTypeEnum callType;

    public InterfaceConfig(Class<?> itf){
        this.timeout = 5000;//默认都是5000毫秒超时
        this.callType = CallTypeEnum.syn;//默认都是同步调用
        this.itf = itf;
    }

    public InterfaceConfig(Class<?> itf,long timeout){
        this.timeout = timeout;
        this.callType = CallTypeEnum.syn;
        this.itf = itf;
    }


    public InterfaceConfig(Class<?> itf,long timeout,CallTypeEnum callType){
        this.timeout = timeout;
        this.callType = callType;
        this.itf = itf;
    }

    public Object getRef() {
        return ref;
    }

    public void setRef(Object ref) {
        this.ref = ref;
    }

    public Class getItf() {
        return itf;
    }

    public void setItf(Class itf) {
        this.itf = itf;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public CallTypeEnum getCallType() {
        return callType;
    }

    public void setCallType(CallTypeEnum callType) {
        this.callType = callType;
    }
}
