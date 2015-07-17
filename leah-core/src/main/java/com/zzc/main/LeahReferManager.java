package com.zzc.main;


import com.zzc.main.config.CallTypeEnum;
import com.zzc.main.config.InvokerConfig;
import com.zzc.proxy.ProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ying on 15/7/1.
 * 引用服务工厂
 */
public class LeahReferManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static volatile LeahReferManager leahReferManager = null;

    /**
     * 调用者
     */
    private Map<String,InvokerConfig> cacheInvoker = new ConcurrentHashMap<String, InvokerConfig>();

    private LeahReferManager(){}

    public static LeahReferManager getManager(){
        if(leahReferManager == null){
            synchronized (LeahReferManager.class){
                if(leahReferManager == null){
                    leahReferManager = new LeahReferManager();
                }
            }
        }
        return leahReferManager;
    }

    public InvokerConfig addInvoker(String serviceUrl,Class<?> itfCls) throws Exception{
        return this.addInvoker(serviceUrl,itfCls,5000, CallTypeEnum.syn);
    }

    public InvokerConfig addInvoker(String serviceUrl,Class<?> itfCls, long timeout) throws Exception{
        return this.addInvoker(serviceUrl,itfCls, timeout, CallTypeEnum.syn);
    }

    public InvokerConfig addInvoker(String serviceUrl,Class<?> itfCls, long timeout, CallTypeEnum callType) throws Exception{
        InvokerConfig invokerConfig = new InvokerConfig(serviceUrl,itfCls,timeout,callType);
        this.addInvoker(invokerConfig);
        return invokerConfig;
    }

    /**
     * 添加引用
     * @param invokerConfig
     */
    public void addInvoker(InvokerConfig invokerConfig){
        if(invokerConfig.getRef() == null){
            invokerConfig.setRef(ProxyFactory.getProxy(invokerConfig));
        }
        this.cacheInvoker.put(invokerConfig.getServiceUrl(),invokerConfig);
    }

    /**
     * @param serviceUrl
     * @return
     */
    public InvokerConfig getInvoker(String serviceUrl){
        return this.cacheInvoker.get(serviceUrl);
    }

    /**
     * 获取所有invokerConfig对应的url
     * @return
     */
    public Set<String> getAllInvokerUrl(){
        return this.cacheInvoker.keySet();
    }
}
