package com.zzc.main;

import com.zzc.codec.HessianCodecFactory;
import com.zzc.handler.ClientHandler;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Created by ying on 15/5/20.
 * rpc服务的客户端
 */
public class LeahClient {
    private final Logger logger = LoggerFactory.getLogger(LeahClient.class);
    /**
     * 连接超时时间
     */
    private final int connectTimeoutMillis = 1000*30;
    /**
     * 断线重连次数
     */
    private final int reconnectorTimes = 5;

    private String ip;
    private int port;

    private IoConnector connector;

    public LeahClient(String ip , int port){
        connector = new NioSocketConnector();

        this.ip = ip;
        this.port = port;
    }

   /**
     * 启动客户端
     */
    public void start(){
        try{
            logger.info("leahClient 开始启动-->ip:{},port:{},connectTimeoutMillis:{}",new Object[]{ip,port,connectTimeoutMillis});

            //启动nio
            connector.setConnectTimeoutMillis(connectTimeoutMillis);//连接超时时间

            //增加断线重连过滤器
            connector.getFilterChain().addFirst("reconnector",new IoFilterAdapter(){
                @Override
                public void destroy() throws Exception {
                    for(int i = 0 ; i < reconnectorTimes ; i++) {
                        if (i != 0) {
                            Thread.sleep(i * 1000);
                        }
                        if(reconnect()){
                            break;
                        }
                    }
                }
            });

            //添加hession的编码和解码过滤器
            connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new HessianCodecFactory()));
            /**
             * 添加ExecutorFilter将业务线程与io线程分离
             * handler中的方法将在新的线程池中调度，作为调用方，不限制调用线程池的大小等参数
             */
            connector.getFilterChain().addLast("exceutor", new ExecutorFilter(Executors.newCachedThreadPool()));
            connector.setHandler(new ClientHandler(ip+":"+port));

            //连接
            ConnectFuture connectFuture =  connector.connect(new InetSocketAddress(ip,port));
            //等待连接创建成功
            connectFuture.awaitUninterruptibly(1000*5);

            IoSession session = connectFuture.getSession();

            if(session.isConnected()){//连接成功
                logger.info("leahClient connected,host:{},port:{}",this.ip,this.port);
            }

        }catch (RuntimeIoException e){
            logger.error(e.getMessage(),e);
        }

    }

    /**
     * 重连
     */
    private Boolean reconnect(){
        try{
            ConnectFuture connectFuture = connector.connect(new InetSocketAddress(ip, port));
            connectFuture.awaitUninterruptibly();
            IoSession session = connectFuture.getSession();
            if(session.isConnected()){//连接成功
                logger.info("leahClient 重新连接成功,host:{},port:{}",ip,port);
                return true;
            }
            return false;
        }catch (RuntimeIoException e){
            logger.error(e.getMessage());
            return false;
        }

    }

    /**
     * 停止服务
     */
    public void stop(){
        this.connector.dispose(true);
    }

}
