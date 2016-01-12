package com.zzc.handler;

import com.alibaba.fastjson.JSONObject;
import com.zzc.channel.ChannelSubject;
import com.zzc.channel.impl.DefaultChannelSubject;
import com.zzc.proxy.result.Result;
import com.zzc.register.ChannelManager;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端handler，此处是客户端执行远程调用的入口
 */
public class ClientHandler extends IoHandlerAdapter{
    private final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
	private String conn;
	private static ChannelSubject channelSubject;//通道

    public ClientHandler(String conn){
        this.conn  = conn;
    }

    /**
     * 获取channelSubject
     * @return
     */
    public ChannelSubject getChannelSubject(){
        return channelSubject;
    }

	
	@Override
	public void sessionOpened(IoSession session) throws Exception {
        logger.debug("session opened");
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        logger.debug("exception caught");
        logger.error(cause.getMessage(), cause);
	}

    /**
     * 收到消息的时候直接强制转化为目标结果，调用通道的通知功能，激活等待的线程返回
     * 可以认为这是一个订阅者模式
     * @param session
     * @param message
     * @throws Exception
     */
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
        logger.debug("message received:{}",JSONObject.toJSONString(message));
		Result result = (Result)message;//获取结果
		channelSubject.notifyObserver(result);//调用通知
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
        logger.debug("message sent");
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
        logger.debug("session created");
        /**
         * 接口的动态代理放在 sessionCreated方法中初始化，因为在mina中，sessionCreated事件只能与IoProcessor在同一线程上执行
         */
        logger.debug("client start create refer implements proxy");
        //初始化通道
        channelSubject = new DefaultChannelSubject(session);
        //将channel增加到manager中
        ChannelManager.getManager().addChannel(this.conn,channelSubject);

        /**
         * 为注册接口生成动态代理
         *
         * 生成代理不再依赖channel，这段动态代理可以考虑放在别的位置
         *
         * 此处需要增加将channel注册到channel管理器，方便负载均衡
         */
//        for(Entry<Class<?>, InvokerConfig> entry : LeahContext.getReferServices().entrySet()){//遍历待处理引用列表
//            final Class<?> itf = entry.getKey();
//            InvokerConfig itfCfg = entry.getValue();
//            //创建动态代理
//            Object obj = ProxyFactory.getProxy(itfCfg);
//
//            //设置生成的动态代理对象
//            itfCfg.setItfCls(itf);
//            itfCfg.setRef(obj);
//            entry.setValue(itfCfg);
//        }
        logger.debug("client create refer proxy end");
	}
}
