package com.zzc.register;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * Created by ying on 15/7/1.
 * 注册中心
 */
public class RegisterManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static volatile RegisterManager registerManager = null;
    /**
     * 注册者
     */
    private Register register;

    private RegisterManager(Register register){
        this.register = register;
    }

    /**
     * 获取manager
     * @param register
     * @return
     */
    public static RegisterManager getManager(Register register){
        if(registerManager == null){
            synchronized (RegisterManager.class){
                if (registerManager == null){
                    registerManager = new RegisterManager(register);
                }
            }
        }
        return registerManager;
    }

    /**
     * 获取manager
     * @return
     */
    public static RegisterManager getManager(){
        if(registerManager == null){
            throw new IllegalStateException("please call getManager(Register register) init RegisterManager first");
        }
        return registerManager;
    }

    /**
     * 发布服务
     * @param urlConnEntity
     */
    public void publish(UrlConnEntity urlConnEntity){
        this.register.publish(urlConnEntity);
    }

    /**
     * 取消发布服务
     * @param urlConnEntity
     */
    public void unpublish(UrlConnEntity urlConnEntity){
        this.register.unpublish(urlConnEntity);
    }

    /**
     * 从注册中心同步服务列表
     * @param urls
     * @return url->[192.168.8.10:8825,192.168.8.11:8825]
     */
    public RegisterServiceBean syn(Set<String> urls){
        return this.register.syn(urls);
    }
}
