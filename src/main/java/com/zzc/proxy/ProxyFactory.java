package com.zzc.proxy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.cglib.proxy.Enhancer;

import org.apache.mina.core.session.IoSession;

import com.zzc.RpcUtil;
import com.zzc.channel.ChannelSubject;

/**
 * 生成代理类
 * 包括生成客户端代理和服务端的包装类
 * @author ying
 *
 */
public class ProxyFactory {
	/**
	 * 存储代理类的缓存
	 */
	private static Map<String, Enhancer> proxyObjectCache = new ConcurrentHashMap<String, Enhancer>();
	/**
	 * 存储包装类的缓存
	 */
	private static Map<String, JavassistWrapper> wrapObjectCache = new ConcurrentHashMap<String, JavassistWrapper>();
	
	/**
	 * 生成客户端接口的代理类
	 * 接口相同的代理类只生成一次
	 * @param session 
	 * @param itf 接口
	 * @return
	 */
	public static <T>T getProxy(ChannelSubject channel,Class<T> itf){
		String itfName = itf.getName();
		if(proxyObjectCache.containsKey(itfName)){
			return (T)proxyObjectCache.get(itfName).create();
		}else{
			//创建动态代理
			Enhancer enhancer = new Enhancer();
			enhancer.setInterfaces(new Class[]{itf});
			enhancer.setCallback(new ServiceInterceptor(channel, itf));
			
			//添加到缓存
			proxyObjectCache.put(itfName, enhancer);
			
			return (T)enhancer.create();
			
		}
		
	}
	
	/**
	 * 执行方法调用并返回结果
	 * @param invocation 调用信息
	 * @return
	 * @throws Exception
	 */
	public static Object doInvoker(Invocation invocation) throws Exception{
		Class<?> itf = invocation.getInterface();//接口名
		String itfName = itf.getName();
		//处理不同的服务
		Object service = RpcUtil.exportServicesMap.get(itf.getName());//实现类
		String methodName = invocation.getMethodName();//方法名
		Class<?>[] paramType = invocation.getArgumentsType();//参数类型列表
		Object[] value = invocation.getArguments();//参数值列表
		
		//处理paramType中的基本类型
//		for(int i = 0 ; i < paramType.length ; i++){
//			if(isWrapClass(paramType[i])){
//				paramType[i] = paramType[i].getField("TYPE").;
//			}
//		}
		
		if(wrapObjectCache.containsKey(itfName)){//如果存在
			JavassistWrapper wrap = wrapObjectCache.get(itfName);
			return wrap.invoke(service, methodName, paramType, value);
		}else{
			JavassistWrapper wrap = JavassistWrapper.create(itf);//创建一个新的
			wrapObjectCache.put(itfName, wrap);//添加到缓存
			Object obj = wrap.invoke(service, methodName, paramType, value);
			return obj;
		}
	}
}
