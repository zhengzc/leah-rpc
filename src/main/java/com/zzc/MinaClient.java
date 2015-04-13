package com.zzc;

import java.net.InetSocketAddress;
import java.util.Map.Entry;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.zzc.codec.HessianCodecFactory;
import com.zzc.proxy.ProxyFactory;

public class MinaClient {
	public static void main(String[] args) {
		
		//模拟引入服务
		RpcUtil.refer(UserServcie.class);
		
		try {
			IoConnector connector = new NioSocketConnector();
			connector.setConnectTimeoutMillis(3000);//连接超时时间
			//添加hession的编码和解码过滤器
			connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new HessianCodecFactory()));
			//设置ioHandler处理业务逻辑
			connector.setHandler(new IoHandlerAdapter(){

				@Override
				public void sessionOpened(IoSession session) throws Exception {
					System.out.println("start");
//					for(int i = 0 ; i < 100 ; i++){
//						session.write(new UserBean(i));
//					}
					
					for(Entry<Class<?>, Object> entry : RpcUtil.referServicesMap.entrySet()){//遍历待处理引用列表
						final Class<?> itf = entry.getKey();
						//创建动态代理
						Object tmp = ProxyFactory.getProxy(session, itf);
						
						entry.setValue(tmp);
					}
					
//					//构建请求对象
//					Invocation invocation = new RpcInvocatioin("add", new Class[]{UserBean.class}, new Object[]{new UserBean(11)}, UserServcie.class);
//					//发送请求
//					session.write(invocation);
//					System.out.println("end");
				}
				
				@Override
			    public void exceptionCaught( IoSession session, Throwable cause ) throws Exception{
			        cause.printStackTrace();
			    }
			});
			
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
					UserServcie userServcie = (UserServcie)RpcUtil.referServicesMap.get(UserServcie.class);
					userServcie.add(new UserBean(1111111));
				}
			}.start();;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
