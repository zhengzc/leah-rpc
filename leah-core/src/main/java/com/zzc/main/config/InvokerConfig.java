package com.zzc.main.config;

import java.net.URL;

/**
 * Created by ying on 15/6/2.
 * 生成每个接口的动态代理的配置信息
 */
public class InvokerConfig {
    private final String DEFAULT_VERSION = "1.0";
    /**
     * 服务名
     */
    private String serviceUrl;
    /**
     * 接口的动态代理生成的实现类
     */
    private Object ref;
    /**
     * 接口
     */
    private Class itfCls;
    /**
     * 版本号
     */
    private String version;
    /**
     * 调用超时时间
     */
    private long timeout;
    /**
     * 调用类型
     */
    private CallTypeEnum callType;

    public InvokerConfig(String serviceUrl, Class<?> itfCls, long timeout, CallTypeEnum callType) throws Exception {
        this.serviceUrl = serviceUrl;
        this.timeout = timeout;
        this.callType = callType;
        this.itfCls = itfCls;

        URL url = new URL(serviceUrl);
        String path = url.getPath();
        String[] args = path.split("_");
        if (args.length == 2) {
            this.version = args[1];
        } else {
            this.version = DEFAULT_VERSION;
        }
    }

    public Object getRef() {
        return ref;
    }

    public void setRef(Object ref) {
        this.ref = ref;
    }

    public Class getItfCls() {
        return itfCls;
    }

    public void setItfCls(Class itfCls) {
        this.itfCls = itfCls;
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

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public void setCallType(CallTypeEnum callType) {
        this.callType = callType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
