package com.zzc.main.config;

/**
 * Created by ying on 15/6/30.
 * 服务统一配置
 */
public class ServerConfig {
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
    private Boolean autoSelectPort;
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
}
