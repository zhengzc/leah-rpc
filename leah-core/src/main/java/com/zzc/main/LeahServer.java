package com.zzc.main;

import com.zzc.codec.HessianCodecFactory;
import com.zzc.handler.ServiceHandler;
import com.zzc.main.config.ServerConfig;
import com.zzc.util.NetUtil;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Created by ying on 15/5/20.
 * rpc服务启动的入口类，此类支持编程式启动rpc服务
 */
public class LeahServer {
    private static final Logger logger = LoggerFactory.getLogger(LeahServer.class);
    private static IoAcceptor acceptor = null;
//    private static NioSocketAcceptor acceptor = null;

    /**
     * 启动服务
     * @throws IOException
     * @return  conn
     */
    public static String start() throws IOException {
        LeahServiceManager leahServiceManager = LeahServiceManager.getManager();
        ServerConfig serverConfig = leahServiceManager.getServerConfig();

        if(leahServiceManager.getServiceSize() == 0){
            throw new IllegalArgumentException("不存在远程服务，远程服务个数为0");
        }
        logger.info("LeahServer 开始启动!");

        acceptor = new NioSocketAcceptor();
        acceptor.getSessionConfig().setReadBufferSize(serverConfig.getReadBufferSize());//设置缓冲区大小
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, serverConfig.getIdleTime());//设置多长时间进入空闲
        //添加hession的编码和解码过滤器
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new HessianCodecFactory()));
        /**
         * 添加ExecutorFilter将业务线程与io线程分离
         * 在服务端我们将采取手动在ServiceHandler中相应的方法中开启新的线程处理业务代码，不采用mina提供的这种方式
         */
        /*acceptor.getFilterChain().addLast("executor",new ExecutorFilter(
                new ThreadPoolExecutor(
                    serverConfig.getCoreServicePoolSize(),
                    serverConfig.getMaxServicePoolSize(),
                    60,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(serverConfig.getWorkQueueSize()),
                    new NamedThreadFactory("leahRpcServices")),
                IoEventType.MESSAGE_RECEIVED
        ));*/
        //设置ioHandler处理业务逻辑
        acceptor.setHandler(new ServiceHandler());

//        acceptor.setReuseAddress(true);//防止重启的时候端口被占用{
        int port = serverConfig.getPort();
        if(serverConfig.getAutoSelectPort()){//自动选择端口
            while(NetUtil.isLoclePortUsing(port)){//端口被占用就+1接着重试
                port++;
            }
        }
        acceptor.bind(new InetSocketAddress(serverConfig.getPort()));//绑定端口

        InetAddress address = InetAddress.getLocalHost();
        String ip = address.getHostAddress();
        logger.info("LeahServer 已启动,监听地址为 {}:{}",ip,serverConfig.getPort());

        return ip+":"+serverConfig.getPort();
    }

    /**
     * 停止服务
     */
    public static void stop(){
        acceptor.unbind();//解绑端口
        acceptor.dispose(true);//停止服务
    }
}
