package com.zzc.register;

import java.util.Set;

/**
 * Created by ying on 15/7/1.
 * 注册中心接口
 */
public interface Register {
    /**
     * 发布服务,幂等操作，即便是重复调用，结果也一样
     *
     * @param urlConnEntity
     */
    public void publish(UrlConnEntity urlConnEntity);

    /**
     * 取消发布服务
     *
     * @param urlConnEntity
     */
    public void unpublish(UrlConnEntity urlConnEntity);

    /**
     * 取消发布服务
     *
     * @param conn 127.0.0.1:8825
     */
    public void unpublish(String conn);

    /**
     * 从注册中心同步服务列表
     * @param urls
     * @return
     */
//    public RegisterServiceBean syn(Set<String> urls);

    /**
     * 获取所有的服务链接地址列表
     *
     * @param urls 服务keys
     * @return
     */
    public Set<String> getAllConn(Set<String> urls);

    /**
     * 获取某一个服务的链接地址
     *
     * @param url 服务key
     * @return
     */
    public Set<String> getConns(String url);
}
