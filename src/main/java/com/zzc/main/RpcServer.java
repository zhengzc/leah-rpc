package com.zzc.main;

import com.zzc.codec.HessianCodecFactory;
import com.zzc.handler.ServiceHandler;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by ying on 15/5/20.
 * rpc服务启动的入口类，此类支持编程式启动rpc服务
 */
public class RpcServer {
    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);
    /**
     * 读取数据缓冲区大小
     */
    private static int readBufferSize = 2048;
    /**
     * 通道多长时间进入空闲状态
     */
    private static int idleTime = 10;
    /**
     * 默认服务端开启端口号
     */
    private static int port = 8825;

    /**
     * 启动服务
     * @throws IOException
     */
    public static void start() throws IOException {
        if(RpcContext.exportServicesMap.size() == 0){
            throw new IllegalArgumentException("no services is export,exportServices size is 0");
        }
        logger.info("RpcServer is starting!");

        IoAcceptor acceptor = new NioSocketAcceptor();
        acceptor.getSessionConfig().setReadBufferSize(readBufferSize);//设置缓冲区大小
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, idleTime);//设置多长时间进入空闲
        //添加hession的编码和解码过滤器
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new HessianCodecFactory()));
        //设置ioHandler处理业务逻辑
        acceptor.setHandler(new ServiceHandler());

        acceptor.bind(new InetSocketAddress(port));//绑定端口

        logger.info("RpcServer is started,listen port is {}",port);
    }


    public static void setReadBufferSize(int readBufferSize) {
        RpcServer.readBufferSize = readBufferSize;
    }

    public static void setIdleTime(int idleTime) {
        RpcServer.idleTime = idleTime;
    }

    public static void setPort(int port) {
        RpcServer.port = port;
    }
}
