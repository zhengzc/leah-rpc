package com.zzc.handler;

import java.util.Map.Entry;

import com.alibaba.fastjson.JSONObject;
import com.zzc.main.config.InterfaceConfig;
import com.zzc.main.config.RpcContext;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.zzc.channel.ChannelSubject;
import com.zzc.channel.impl.DefaultChannelSubject;
import com.zzc.proxy.ProxyFactory;
import com.zzc.proxy.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端handler，此处是客户端执行远程调用的入口
 */
public class ClientHandler extends IoHandlerAdapter{
    private final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
	
	private ChannelSubject channelSubject;//通道
	
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
		this.channelSubject.notifyObserver(result);//调用通知
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

        //为注册接口生成动态代理
        for(Entry<Class<?>, InterfaceConfig> entry : RpcContext.getReferServices().entrySet()){//遍历待处理引用列表
            final Class<?> itf = entry.getKey();
            InterfaceConfig itfCfg = entry.getValue();
            //创建动态代理
            Object obj = ProxyFactory.getProxy(channelSubject, itf);

            //设置生成的动态代理对象
            itfCfg.setItf(itf);
            itfCfg.setRef(obj);
            entry.setValue(itfCfg);
        }
        logger.debug("client create refer proxy end");
	}
}
