package com.zzc;

import com.zzc.codec.HessianCodecFactory;
import com.zzc.handler.ServiceHandler;
import com.zzc.main.config.RpcContext;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;

public class MinaServer {
	
	public static void main(String[] args) {
		
		//模拟发布一个服务
		RpcContext.export(UserServcie.class, new UserServcieImpl());
		
		try {
			IoAcceptor acceptor = new NioSocketAcceptor();
			acceptor.getSessionConfig().setReadBufferSize(2048);//设置缓冲区大小
			acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);//设置多长时间进入空闲
			//添加hession的编码和解码过滤器
			acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new HessianCodecFactory()));
			//设置ioHandler处理业务逻辑
			acceptor.setHandler(new ServiceHandler());
			
			acceptor.bind(new InetSocketAddress(8825));//绑定端口
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
