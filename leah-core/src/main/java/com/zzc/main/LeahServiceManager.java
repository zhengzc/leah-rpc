package com.zzc.main;

import com.zzc.main.config.ServerConfig;
import com.zzc.register.Register;
import com.zzc.register.UrlConnEntity;
import com.zzc.spring.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ying on 15/6/29.
 */
public class LeahServiceManager {
    private final Logger logger = LoggerFactory.getLogger(LeahServiceManager.class);
    /**
     * 服务的统一配置
     */
    private ServerConfig serverConfig;
    /**
     * 所有服务存储在这里
     */
    private Map<String,Object> services = new ConcurrentHashMap<String, Object>();

    private static volatile LeahServiceManager leahServiceManager = null;

    private LeahServiceManager(){}

    public static LeahServiceManager getManager(){
        if(leahServiceManager == null){
            synchronized (LeahServiceManager.class){
                if(leahServiceManager == null){
                    leahServiceManager = new LeahServiceManager();
                }
            }
        }
        return leahServiceManager;
    }

    /**
     * @param serviceUrl
     * @param obj
     */
    public void addService(String serviceUrl,Object obj){
        services.put(serviceUrl,obj);
    }

    /**
     * @param serviceUrl
     * @return
     */
    public Object getService(String serviceUrl){
        return services.get(serviceUrl);
    }

    /**
     * 获取注册服务个数
     * @return
     */
    public int getServiceSize(){
        return services.size();
    }

    /**
     * 启动服务
     */
    public void online() throws IOException{
        String conn = LeahServer.start();
        this.publish(conn);
    }

    /**
     * 停止服务
     */
    public void offline() throws IOException{
        LeahServer.stop();
    }

    /**
     * 发布服务
     */
    public void publish(String conn){
        Register register = SpringContext.getBean(Register.class);
        for(String serviceUrl : this.services.keySet()){
            logger.info("publish service url is : {}", serviceUrl);
            UrlConnEntity  urlConnEntity = new UrlConnEntity();
            urlConnEntity.setUrl(serviceUrl);
            urlConnEntity.setConn(conn);
            register.publish(urlConnEntity);
        }
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }
}
