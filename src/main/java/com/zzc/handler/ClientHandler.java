package com.zzc.handler;

import java.util.Map.Entry;

import com.alibaba.fastjson.JSONObject;
import com.zzc.main.RpcContext;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.zzc.channel.ChannelSubject;
import com.zzc.channel.impl.DefaultChannelSubject;
import com.zzc.proxy.ProxyFactory;
import com.zzc.result.Result;

/**
 * 客户端handler，此处是客户端执行远程调用的入口
 */
public class ClientHandler extends IoHandlerAdapter{
	
	private ChannelSubject channelSubject;//通道
	
	@Override
	public void sessionOpened(IoSession session) throws Exception {

	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		cause.printStackTrace();
	}

    /**
     * 收到消息的时候直接强制转化为目标结果，调用通道的通知功能，激活等待的线程返回
     * 可以认为这是一个订阅者模式
     * @param session
     * @param message
     * @throws Exception
     */
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
        System.out.println("Client mssageReceived:"+ JSONObject.toJSONString(message));
		Result result = (Result)message;//获取结果
		this.channelSubject.notifyOberver(result);//调用通知
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		super.messageSent(session, message);
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
        System.out.println("ClientHandler sessionCreated start");

        /**
         * 接口的动态代理放在 sessionCreated方法中初始化，因为在mina中，sessionCreated事件只能与IoProcessor在同一线程上执行
         */
        //初始化通道
        channelSubject = new DefaultChannelSubject(session);

        //为注册接口生成动态代理
        for(Entry<Class<?>, Object> entry : RpcContext.referServicesMap.entrySet()){//遍历待处理引用列表
            final Class<?> itf = entry.getKey();
            //创建动态代理
            Object tmp = ProxyFactory.getProxy(channelSubject, itf);

            entry.setValue(tmp);
        }
        System.out.println("ClientHandler sessionCreated end");
	}
}
