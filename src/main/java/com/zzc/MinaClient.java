package com.zzc;

import java.net.InetSocketAddress;

import com.zzc.handler.ClientHandler;
import com.zzc.main.RpcContext;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.alibaba.fastjson.JSONObject;
import com.zzc.codec.HessianCodecFactory;

public class MinaClient {
	public static void main(String[] args) {
		
		//模拟引入服务
		RpcContext.refer(UserServcie.class);
		
		try {
			IoConnector connector = new NioSocketConnector();
			connector.setConnectTimeoutMillis(3000);//连接超时时间
			//添加hession的编码和解码过滤器
			connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new HessianCodecFactory()));
			connector.setHandler(new ClientHandler());
			
			//连接
			ConnectFuture connectFuture =  connector.connect(new InetSocketAddress("127.0.0.1",8825));
			connectFuture.awaitUninterruptibly();
			
			//模拟调用一次
			new Thread(){
				@Override
				public void run() {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					UserServcie userServcie = (UserServcie) RpcContext.referServicesMap.get(UserServcie.class);
//					userServcie.add(new UserBean(1111111));
					
					UserBean userBean = userServcie.query(222);
					System.out.println(JSONObject.toJSONString(userBean));
				}
			}.start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
