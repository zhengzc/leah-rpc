package com.zzc.util;

import java.util.Map;

/**
 * Created by ying on 15/7/10.
 * url字符串解析类,解析形如
 * http://127.0.0.1:8080/xxx?param1=v1
 * jdbc://127.0.0.1:8090/sdfa?param1=v1
 */
public class URL {
    private String url;
    private String protocol;
    private String host;
    private String port;
    private String path;
    private String queryStr;
    private Map<String,String> param;

    public URL(String url){
        this.url = url;

    }
}
