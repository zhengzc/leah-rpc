package com.zzc.main;

import com.zzc.codec.HessianCodecFactory;
import com.zzc.handler.ClientHandler;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;

/**
 * Created by ying on 15/5/20.
 * rpc服务的客户端
 */
public class RpcClient {
    /**
     * 连接超时时间
     */
    private static int connectTimeoutMillis = 3000;
    private static String ip = "127.0.0.1";
    private static int port = 8825;

    /**
     * 启动客户端
     */
    public static void start(){
        IoConnector connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(3000);//连接超时时间
        //添加hession的编码和解码过滤器
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new HessianCodecFactory()));
        connector.setHandler(new ClientHandler());

        //连接
        ConnectFuture connectFuture =  connector.connect(new InetSocketAddress("127.0.0.1",8825));
        //等待连接创建成功
        connectFuture.awaitUninterruptibly();
    }

    public static void setConnectTimeoutMillis(int connectTimeoutMillis) {
        RpcClient.connectTimeoutMillis = connectTimeoutMillis;
    }

    public static void setIp(String ip) {
        RpcClient.ip = ip;
    }

    public static void setPort(int port) {
        RpcClient.port = port;
    }
}
