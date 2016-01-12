package com.zzc.main;

import com.zzc.main.config.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ying on 15/6/29.
 */
public class LeahServiceManager {
//    //间隔多久重新发布一次服务
//    private static final int PUBLISH_TIME = 15 * 1000;
    private static final Logger logger = LoggerFactory.getLogger(LeahServiceManager.class);
    /**
     * 服务的统一配置
     */
    private ServerConfig serverConfig;
    /**
     * 所有服务存储在这里
     */
    private Map<String,Object> services = new ConcurrentHashMap<String, Object>();

    private static volatile LeahServiceManager leahServiceManager = new LeahServiceManager();

    private LeahServiceManager(){

    }

    public static LeahServiceManager getManager(){
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

//    /**
//     * 启动服务
//     */
//    public void online() throws IOException{
//        String conn = LeahServer.start();
//        this.publish(conn);
//    }
//
//    /**
//     * 停止服务
//     */
//    public void offline() throws IOException{
//        LeahServer.stop();
//    }

//    /**
//     * 发布服务
//     */
//    private void publish(final String conn){
//        if(register != null){
//            //定时发布，防止被误删
//            Thread t = new Thread(){
//                @Override
//                public void run() {
//                    for(String serviceUrl : services.keySet()){
//                        logger.info("发布服务: {}", serviceUrl);
//                        UrlConnEntity  urlConnEntity = new UrlConnEntity();
//                        urlConnEntity.setUrl(serviceUrl);
//                        urlConnEntity.setConn(conn);
//                        register.publish(urlConnEntity);
//                    }
//
//                    try {
//                        Thread.sleep(PUBLISH_TIME);
//                    } catch (InterruptedException e) {
//                        logger.error(e.getMessage(),e);
//                    }
//                }
//            };
//
//            t.start();
//        }
//    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }
}
