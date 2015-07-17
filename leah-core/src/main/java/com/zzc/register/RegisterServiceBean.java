package com.zzc.register;

import java.util.Map;
import java.util.Set;

/**
 * Created by ying on 15/7/2.
 * 注册服务对象
 */
public class RegisterServiceBean {
    /**
     * 每个服务注册的url信息
     * 192.168.8.10:8825->[com.zzc.userService_1.0,com.zzc.userService_2.0]
     */
    private Map<String,Set<String>> connUrls;

    /**
     * 每个url对应的服务信息
     * url->[192.168.8.10:8825,192.168.8.11:8825]
     */
    private Map<String,Set<String>> urlConns;

    public Map<String, Set<String>> getUrlConns() {
        return urlConns;
    }

    public Map<String, Set<String>> getConnUrls() {
        return connUrls;
    }

    public void setConnUrls(Map<String, Set<String>> connUrls) {
        this.connUrls = connUrls;
    }

    public void setUrlConns(Map<String, Set<String>> urlConns) {
        this.urlConns = urlConns;
    }
}
