package com.zzc;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.alibaba.fastjson.JSONObject;
import com.zzc.codec.HessianCodecFactory;
import com.zzc.proxy.Invocation;
import com.zzc.proxy.JavassistWrapper;
import com.zzc.proxy.ProxyFactory;
import com.zzc.result.Result;
import com.zzc.result.impl.DefaultResult;

public class MinaServer {
	
	public static void main(String[] args) {
		
		//模拟发布一个服务
		RpcUtil.export(UserServcie.class, new UserServcieImpl());
		
		try {
			IoAcceptor acceptor = new NioSocketAcceptor();
			acceptor.getSessionConfig().setReadBufferSize(2048);//设置缓冲区大小
			acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);//设置多长时间进入空闲
			//添加hession的编码和解码过滤器
			acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new HessianCodecFactory()));
			//设置ioHandler处理业务逻辑
			acceptor.setHandler(new IoHandlerAdapter(){

				@Override
				public void messageReceived(IoSession session, Object message)
						throws Exception {
//					System.out.println("serverMsg--------->"+JSONObject.toJSON(message));
					System.out.println("serverMsg--------->start");
					Invocation invocation = (Invocation)message;
					
					//执行调用
					Object obj = ProxyFactory.doInvoker(invocation);
					
					//装配返回结果
					Result result = new DefaultResult(invocation.getToken(),obj);
					
					//返回
					session.write(result);
					
					System.out.println("serverMsg--------->end");
//					System.out.println("--->"+JSONObject.toJSONString(obj));
				}
				
				@Override
			    public void exceptionCaught( IoSession session, Throwable cause ) throws Exception{
			        cause.printStackTrace();
			    }
			});
			
			acceptor.bind(new InetSocketAddress(8825));//绑定端口
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
