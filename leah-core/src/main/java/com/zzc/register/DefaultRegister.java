package com.zzc.register;

import org.apache.mina.util.ConcurrentHashSet;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by ying on 16/1/11.
 * 默认的注册中心
 */
public class DefaultRegister implements Register{

    /**
     * 服务列表
     */
    private Set<UrlConnEntity> services = new ConcurrentHashSet<UrlConnEntity>();

    public void publish(String conn,String url){
        UrlConnEntity urlConnEntity = new UrlConnEntity();
        urlConnEntity.setConn(conn);
        urlConnEntity.setUrl(url);
        services.add(urlConnEntity);
    }

    @Override
    public void publish(UrlConnEntity urlConnEntity) {
        services.add(urlConnEntity);
    }

    @Override
    public void unpublish(UrlConnEntity urlConnEntity) {
        services.remove(urlConnEntity);
    }

    @Override
    public void unpublish(String conn) {
        Iterator<UrlConnEntity> it = services.iterator();
        while(it.hasNext()){
            UrlConnEntity tmp = it.next();
            if(conn.equals(tmp.getConn())){
                it.remove();
            }
        }
    }

    @Override
    public Set<String> getAllConn(Set<String> urls) {
        Set<String> conns = new HashSet<String>();

        for(String url : urls) {
            for (UrlConnEntity tmp : services) {
                if(tmp.getUrl().equals(url)){
                    conns.add(tmp.getConn());
                }
            }
        }
        return conns;
    }

    @Override
    public Set<String> getConns(String url) {
        Set<String> conns = new HashSet<String>();

        for (UrlConnEntity tmp : services) {
            if(tmp.getUrl().equals(url)){
                conns.add(tmp.getConn());
            }
        }
        return conns;
    }
}
