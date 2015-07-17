package com.zzc.handler;

import com.zzc.main.LeahServiceManager;
import com.zzc.main.config.ServerConfig;
import com.zzc.proxy.Invocation;
import com.zzc.proxy.ProxyFactory;
import com.zzc.proxy.result.impl.DefaultResult;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Created by ying on 15/5/18.
 * 服务端handler,服务端收到消息将在这里开始处理
 */
public class ServiceHandler extends IoHandlerAdapter {
    private final Logger logger = LoggerFactory.getLogger(ServiceHandler.class);

    /**
     * 执行真正方法调用的线程池
     */
    private Executor threadPool;

    public ServiceHandler(){
        LeahServiceManager leahServiceManager = LeahServiceManager.getManager();
        ServerConfig serverConfig = leahServiceManager.getServerConfig();

        //初始化工作队列
        this.threadPool = new ThreadPoolExecutor(
                serverConfig.getCoreServicePoolSize(),
                serverConfig.getMaxServicePoolSize(),
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(serverConfig.getWorkQueueSize())
        );
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        logger.debug("session created");
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        logger.debug("session opened");
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        logger.debug("session closed");
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        logger.debug("session idle");
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        logger.debug("exception caught");
        logger.error(cause.getMessage(), cause);
    }

    /**
     * 收到消息
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        logger.debug("message Received-->message is {}", message.toString());
        //客户端收到的消息统一转换为Invocation对象
        /*Invocation invocation = (Invocation) message;
        DefaultResult result = new DefaultResult(invocation.getToken());
        try {
            //执行调用
            Object obj = ProxyFactory.doInvoker(invocation);
            //装配返回结果 返回结果统一装配为Result
            result.setResult(obj);
        }catch (Exception e){
            //如果出现异常，写入异常信息
            result.setThrowable(e);
            logger.error(e.getMessage(),e);
        }

        //写入返回数据
        session.write(result);*/

        this.threadPool.execute(new RpcService(session,(Invocation) message));
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        logger.debug("message sent");
    }


    /**
     * 服务端每次都用新的线程处理远程调用
     * 此类就是每次收到请求的时候发起的调用类
     */
    class RpcService implements Runnable{
        private IoSession session;
        private Invocation invocation;

        public RpcService(IoSession session,Invocation invocation){
            this.session = session;
            this.invocation = invocation;
        }
        @Override
        public void run() {
            //客户端收到的消息统一转换为Invocation对象
            DefaultResult result = new DefaultResult(invocation.getToken());
            try {
                //执行调用
                Object obj = ProxyFactory.doInvoker(invocation);
                //装配返回结果 返回结果统一装配为Result
                result.setResult(obj);
            }catch (Exception e){
                //如果出现异常，写入异常信息
                result.setThrowable(e);
                logger.error(e.getMessage(),e);
            }

            //写入返回数据
            session.write(result);

        }
    }
}
