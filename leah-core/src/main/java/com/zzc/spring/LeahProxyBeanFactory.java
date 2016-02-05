package com.zzc.spring;

import com.zzc.main.LeahReferManager;
import com.zzc.main.config.CallTypeEnum;
import com.zzc.main.config.InvokerConfig;
import com.zzc.proxy.ProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * Created by ying on 15/6/25.
 * 与spring集成的时候，所有客户端实现类将由此工厂创建
 */
public class LeahProxyBeanFactory implements FactoryBean {
    private final Logger logger = LoggerFactory.getLogger(LeahProxyBeanFactory.class);

    /**
     * 主要配置参数
     */
    private String itf;//接口名称
    private long timeout;//超时时间
    private String callType;//调用类型
    private String serviceUrl;//调用服务名

    //其他属性
    private ClassLoader classLoader;//类加载器
    private Class itfCls;//类
    private Object obj;//代理实现类

    public LeahProxyBeanFactory() {
        this.classLoader = LeahProxyBeanFactory.class.getClassLoader();
        this.timeout = 5000;
        this.callType = CallTypeEnum.syn.getValue();
    }

    /**
     * 定义一个初始化方法
     */
    public void init() throws Exception {
        logger.info("init {} begin", this.itf);

        if (this.itf == null || this.itf.trim().length() == 0) {
            throw new IllegalArgumentException("invalid interface:" + this.itf);
        }
        //生成代理
        this.itfCls = this.classLoader.loadClass(this.itf.trim());

        //构建调用配置
        InvokerConfig invokerConfig = new InvokerConfig(this.serviceUrl, this.itfCls, timeout, CallTypeEnum.getCallType(this.callType));

        //生成代理
        this.obj = ProxyFactory.getProxy(invokerConfig);

        invokerConfig.setRef(this.obj);

        //添加到引用工厂中
        LeahReferManager leahReferManager = LeahReferManager.getManager();
        leahReferManager.addInvoker(invokerConfig);

        logger.info("init {} end", this.itf);
    }

    @Override
    public Object getObject() throws Exception {
        return this.obj;
    }

    @Override
    public Class<?> getObjectType() {
        return this.itfCls;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setItf(String itf) {
        this.itf = itf;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getItf() {
        return itf;
    }

    public long getTimeout() {
        return timeout;
    }

    public String getCallType() {
        return callType;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public Class getItfCls() {
        return itfCls;
    }

    public void setItfCls(Class itfCls) {
        this.itfCls = itfCls;
    }

    public Object getObj() {
        return obj;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }
}
