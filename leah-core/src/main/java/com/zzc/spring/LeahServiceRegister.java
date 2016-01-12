package com.zzc.spring;

import com.zzc.main.LeahServer;
import com.zzc.main.LeahServiceManager;
import com.zzc.main.config.ServerConfig;
import com.zzc.register.Register;
import com.zzc.register.UrlConnEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Map;

/**
 * Created by ying on 15/6/26.
 */
public class LeahServiceRegister {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //间隔多久重新发布一次服务
    private static final int PUBLISH_TIME = 15 * 1000;

    @Autowired
    private Register register;

    /**
     * 读取数据缓冲区大小
     */
    private int readBufferSize;
    /**
     * 通道多长时间进入空闲状态
     */
    private int idleTime;
    /**
     * 默认服务端开启端口号
     */
    private int port;
    /**
     * 是否自动选择端口
     */
    private Boolean autoSelectPort = true;
    /**
     * 核心服务线程池大小
     */
    private int coreServicePoolSize;
    /**
     * 最大服务线程池大小
     */
    private int maxServicePoolSize;
    /**
     * 工作队列大小
     */
    private int workQueueSize;
    /**
     * 所有服务
     * key 服务名 http://www.zhengzhichao.com.cn/com.dingmei.UserService_1.0
     * value 服务实例
     */
    private Map<String,Object> services;

    public LeahServiceRegister(){
        this.readBufferSize = 2048;
        this.idleTime = 10;
        this.port = 8825;
        this.autoSelectPort = true;
        this.coreServicePoolSize = 50;
        this.maxServicePoolSize = 300;
        this.workQueueSize = 300;
    }

    /**
     * 初始化方法
     */
    public void init(){
        LeahServiceManager leahServiceManager = LeahServiceManager.getManager();

        //基础配置
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setReadBufferSize(this.readBufferSize);
        serverConfig.setIdleTime(this.idleTime);
        serverConfig.setPort(this.port);
        serverConfig.setAutoSelectPort(this.autoSelectPort);
        serverConfig.setCoreServicePoolSize(this.coreServicePoolSize);
        serverConfig.setMaxServicePoolSize(this.maxServicePoolSize);
        serverConfig.setWorkQueueSize(this.workQueueSize);
        leahServiceManager.setServerConfig(serverConfig);

        //服务配置
        for(Map.Entry<String,Object> entry : this.services.entrySet()){
            String serviceUrl = entry.getKey();
            leahServiceManager.addService(serviceUrl,entry.getValue());
        }

        //启动服务
        try {
            this.online();
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
    }

    /**
     * 发布服务
     */
    private void publish(final String conn){
        if(register != null){
            //定时发布，防止被误删
            Thread t = new Thread(){
                @Override
                public void run() {
                    for(String serviceUrl : services.keySet()){
                        logger.info("发布服务: {}", serviceUrl);
                        UrlConnEntity urlConnEntity = new UrlConnEntity();
                        urlConnEntity.setUrl(serviceUrl);
                        urlConnEntity.setConn(conn);
                        register.publish(urlConnEntity);
                    }

                    try {
                        Thread.sleep(PUBLISH_TIME);
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage(),e);
                    }
                }
            };

            t.start();
        }
    }

    /**
     * 启动服务
     */
    public void online() throws IOException {
        String conn = LeahServer.start();
        this.publish(conn);
    }

    public int getReadBufferSize() {
        return readBufferSize;
    }

    public void setReadBufferSize(int readBufferSize) {
        this.readBufferSize = readBufferSize;
    }

    public int getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(int idleTime) {
        this.idleTime = idleTime;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Boolean getAutoSelectPort() {
        return autoSelectPort;
    }

    public void setAutoSelectPort(Boolean autoSelectPort) {
        this.autoSelectPort = autoSelectPort;
    }

    public int getCoreServicePoolSize() {
        return coreServicePoolSize;
    }

    public void setCoreServicePoolSize(int coreServicePoolSize) {
        this.coreServicePoolSize = coreServicePoolSize;
    }

    public int getMaxServicePoolSize() {
        return maxServicePoolSize;
    }

    public void setMaxServicePoolSize(int maxServicePoolSize) {
        this.maxServicePoolSize = maxServicePoolSize;
    }

    public int getWorkQueueSize() {
        return workQueueSize;
    }

    public void setWorkQueueSize(int workQueueSize) {
        this.workQueueSize = workQueueSize;
    }

    public Map<String, Object> getServices() {
        return services;
    }

    public void setServices(Map<String, Object> services) {
        this.services = services;
    }

    public void setRegister(Register register) {
        this.register = register;
    }
}
