package com.zzc.register;

import java.util.Set;

/**
 * Created by ying on 15/7/1.
 * 注册中心接口
 */
public interface Register {
    /**
     * 发布服务
     * @param urlConnEntity
     */
    public void publish(UrlConnEntity urlConnEntity);

    /**
     * 取消发布服务
     * @param urlConnEntity
     */
    public void unpublish(UrlConnEntity urlConnEntity);

    /**
     * 从注册中心同步服务列表
     * @param urls
     * @return
     */
    public RegisterServiceBean syn(Set<String> urls);


}
