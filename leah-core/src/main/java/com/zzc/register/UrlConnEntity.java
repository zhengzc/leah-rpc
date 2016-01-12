package com.zzc.register;

/**
 * Created by ying on 15/7/1.
 * 注册的url对应的服务连接信息
 */
public class UrlConnEntity {
    /**
     * url
     * key
     */
    private String url;
    /**
     * ip地址+端口
     * 127.0.0.1:8825
     * key
     */
    private String conn;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getConn() {
        return conn;
    }

    public void setConn(String conn) {
        this.conn = conn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UrlConnEntity)) return false;

        UrlConnEntity that = (UrlConnEntity) o;

        if (!conn.equals(that.conn)) return false;
        if (!url.equals(that.url)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = url.hashCode();
        result = 31 * result + conn.hashCode();
        return result;
    }
}
