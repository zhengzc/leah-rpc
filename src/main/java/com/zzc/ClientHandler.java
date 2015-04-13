package com.zzc;

import java.util.Map.Entry;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.zzc.channel.ChannelSubject;
import com.zzc.channel.impl.DefaultChannelObserver;
import com.zzc.proxy.ProxyFactory;
import com.zzc.result.Result;

public class ClientHandler extends IoHandlerAdapter{
	
	private ChannelSubject channelSubject;//通道
	
	@Override
	public void sessionOpened(IoSession session) throws Exception {
		System.out.println("start");
		
		//初始化通道
		channelSubject = new DefaultChannelObserver(session);
		
		//为注册接口生成动态代理
		for(Entry<Class<?>, Object> entry : RpcUtil.referServicesMap.entrySet()){//遍历待处理引用列表
			final Class<?> itf = entry.getKey();
			//创建动态代理
			Object tmp = ProxyFactory.getProxy(channelSubject, itf);
			
			entry.setValue(tmp);
		}
		System.out.println("end");
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		super.exceptionCaught(session, cause);
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		Result result = (Result)message;//获取结果
		this.channelSubject.notifyOberver(result);//调用通知
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		super.messageSent(session, message);
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
	}
}
