package com.zzc.proxy;

import com.alibaba.fastjson.JSONObject;
import com.zzc.channel.ChannelSubject;
import com.zzc.channel.Invoker;
import com.zzc.channel.impl.DefaultInvoker;
import com.zzc.result.Result;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * cglib 生成动态代理的时候，统一的拦截方法
 * 此方法就是我们拦截方法调用，执行rpc调用的地方
 * @author ying
 *
 */
public class ServiceInterceptor implements MethodInterceptor {
	private ChannelSubject channel;
	private Class<?> itf;
	
	/**
	 * 
	 * @param channel
	 * @param itf 代理的目标接口类
	 */
	public ServiceInterceptor(ChannelSubject channel,Class<?> itf){
		this.channel = channel;
		this.itf = itf;
	}
	
	@Override
	public Object intercept(Object arg0, Method arg1, Object[] arg2,
			MethodProxy arg3) throws Throwable {
		
		//参数类型列表
		List<Class<?>> argumentsType = new ArrayList<Class<?>>();
		for(Object tmp : arg2){
			argumentsType.add(tmp.getClass());
		}
		
		Class[] a = {};
		//构建请求对象
		Invocation invocation = new RpcInvocation(this.channel.genToken(),arg1.getName(), argumentsType.toArray(a), arg2, this.itf);
        System.out.println("请求对象构建成功:"+ JSONObject.toJSONString(invocation));
		/*//发送请求
		channel.write(invocation);
        System.out.println("请求已发送");
		
		//注册监听器
		channel.register(this);

        System.out.println("监听返回注册成功，等待返回");
		//等待返回
		gate.await();
//		gate.await(timeout, unit)
        System.out.println("获取到返回结果");
		
		return result.getResult();*/
        Invoker invoker = new DefaultInvoker(this.channel,invocation);
        Result result =  invoker.doInvoke();
        return result.getResult();
	}
}
