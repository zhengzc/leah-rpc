package com.zzc.register;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.*;

/**
 * Created by ying on 15/7/8.
 * mysql通讯的注册者
 *
 * CREATE TABLE `leahservice` (
 `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
 `conn` varchar(100) NOT NULL DEFAULT '' COMMENT '连接信息',
 `url` varchar(300) NOT NULL DEFAULT '' COMMENT '服务url',
 `adddate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 PRIMARY KEY (`id`)
 ) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
 */
public class MysqlRegister implements Register {
    private NamedParameterJdbcTemplate jdbcTemplate = null;

    private final String insertServiceSql = "INSERT INTO leahservice (conn,url) value (:conn,:url)";
    private final String delServiceSql = "DELETE FROM leahservice WHERE conn=:conn and url=:url";
    private final String queryAllSql = "SELECT id,conn,url FROM leahservice WHERE url IN (:urls)";
    private final String delServiceByConnSql = "DELETE FROM leahservice WHERE conn=:conn";


    public MysqlRegister(String connectURI,String userName,String password){
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
    }

    @Override
    public void publish(UrlConnEntity urlConnEntity) {
        Map<String,Object> param = new HashMap<String, Object>();
        param.put("conn",urlConnEntity.getConn());

        jdbcTemplate.update(delServiceByConnSql,param);

        param.put("url",urlConnEntity.getUrl());
        jdbcTemplate.update(insertServiceSql,param);
    }

    @Override
    public void unpublish(UrlConnEntity urlConnEntity) {
        Map<String,Object> param = new HashMap<String, Object>();
        param.put("conn",urlConnEntity.getConn());
        param.put("url",urlConnEntity.getUrl());
        jdbcTemplate.update(delServiceSql,param);
    }

    @Override
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
    }
}
