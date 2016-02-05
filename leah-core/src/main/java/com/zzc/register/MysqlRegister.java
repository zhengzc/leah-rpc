package com.zzc.register;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.*;

/**
 * Created by ying on 15/7/8.
 * mysql通讯的注册者
 * <p/>
 * CREATE TABLE `leahservice` (
 * `conn` varchar(100) NOT NULL DEFAULT '' COMMENT '连接信息',
 * `url` varchar(100) NOT NULL DEFAULT '' COMMENT '服务url',
 * `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 * `uuid` varchar(100) NOT NULL DEFAULT '' COMMENT '注册者唯一标示',
 * PRIMARY KEY (`conn`,`url`)
 * ) ENGINE=MyISAM DEFAULT CHARSET=utf8;
 */
public class MysqlRegister implements Register {
    private NamedParameterJdbcTemplate jdbcTemplate = null;

    private final String upsertServiceSql = "REPLACE INTO leahservice VALUES (:conn,:url,now(),:uuid)";
    private final String delServiceSql = "DELETE FROM leahservice WHERE conn=:conn and url=:url";
    private final String queryAllSql = "SELECT conn,url FROM leahservice WHERE url IN (:urls)";
    private final String delServiceByConnSql = "DELETE FROM leahservice WHERE conn=:conn";
    private final String queryConnsByUrl = "SELECT conn FROM leahservice WHERE url = :url";
    private final String delServiceByConnAndUUID = "DELETE FROM leahservice WHERE conn=:conn AND uuid <> :uuid";

    /**
     * 用来标示每一个服务发布者的唯一一次发布id
     */
    private String uuid;


    public MysqlRegister(String connectURI, String userName, String password) {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUsername(userName);
        ds.setPassword(password);
        ds.setUrl(connectURI);
        ds.setInitialSize(3); // 初始的连接数；
        ds.setMaxActive(10);
        ds.setMinIdle(3);
        ds.setMaxIdle(10);
        ds.setMaxWait(1000);
        jdbcTemplate = new NamedParameterJdbcTemplate(ds);

        uuid = UUID.randomUUID().toString();
    }

    @Override
    public void publish(UrlConnEntity urlConnEntity) {
        if (StringUtils.isNotBlank(urlConnEntity.getConn())) {//每次发布服务都删除之前的老服务
            Map<String, Object> delParam = new HashMap<String, Object>();
            delParam.put("conn", urlConnEntity.getConn());
            delParam.put("uuid", this.uuid);
            this.jdbcTemplate.update(delServiceByConnAndUUID, delParam);
        }

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("conn", urlConnEntity.getConn());
        param.put("url", urlConnEntity.getUrl());
        param.put("uuid", this.uuid);
        jdbcTemplate.update(upsertServiceSql, param);
    }

    @Override
    public void unpublish(UrlConnEntity urlConnEntity) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("conn", urlConnEntity.getConn());
        param.put("url", urlConnEntity.getUrl());
        jdbcTemplate.update(delServiceSql, param);
    }

    @Override
    public void unpublish(String conn) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("conn", conn);
        jdbcTemplate.update(delServiceByConnSql, param);
    }
/*@Override
    public RegisterServiceBean syn(Set<String> urls) {
        Map<String,Object> param = new HashMap<String, Object>();
        param.put("urls",urls);
        List<Map<String,Object>> ret = this.jdbcTemplate.queryForList(queryAllSql,param);

        Map<String,Set<String>> url2Conns = new HashMap<String, Set<String>>();
        Map<String,Set<String>> conn2Urls = new HashMap<String, Set<String>>();
        for(Map<String,Object> tmp : ret){
            String conn = (String)tmp.get("conn");
            String url = (String)tmp.get("url");
            if(conn2Urls.containsKey(conn)){
                conn2Urls.get(conn).add(url);
            }else{
                Set<String> set = new HashSet<String>();
                set.add(url);
                conn2Urls.put(conn,set);
            }

            if(url2Conns.containsKey(url)){
                url2Conns.get(url).add(conn);
            }else{
                Set<String> set = new HashSet<String>();
                set.add(conn);
                url2Conns.put(url,set);
            }
        }

        RegisterServiceBean registerServiceBean = new RegisterServiceBean();
        registerServiceBean.setConnUrls(conn2Urls);
        registerServiceBean.setUrlConns(url2Conns);

        return registerServiceBean;
    }*/

    @Override
    public Set<String> getAllConn(Set<String> urls) {
        Set<String> ret = new HashSet<String>();

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("urls", urls);
        List<Map<String, Object>> services = this.jdbcTemplate.queryForList(queryAllSql, param);

        for (Map<String, Object> map : services) {
            ret.add(((String) map.get("conn")).trim());
        }
        return ret;
    }

    @Override
    public Set<String> getConns(String url) {
        Set<String> ret = new HashSet<String>();

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("url", url);
        List<String> services = this.jdbcTemplate.queryForList(queryConnsByUrl, param, String.class);

        ret.addAll(services);

        return ret;
    }
}
