package com.zzc;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.junit.Test;

import com.alibaba.fastjson.JSONObject;
import com.zzc.proxy.JavassistWrapper;

import junit.framework.TestCase;

public class JavassistWrapTest extends TestCase {
	
	@Test
	public void testCreateClass() throws Exception{
		UserServcie userServcie = new UserServcieImpl();
		try {
			//生成代理类
			JavassistWrapper wrapper = JavassistWrapper.create(UserServcie.class);
			
			//测试
			wrapper.invoke(userServcie, "add", UserServcie.class.getMethod("add",UserBean.class).getParameterTypes(), new Object[]{new UserBean(1122)});
			
			Object ret1 = wrapper.invoke(userServcie, "query", 
					UserServcie.class.getMethod("query", Integer.TYPE,String.class).getParameterTypes(),
					new Object[]{122,"嘎嘎"});
			
			System.out.println(JSONObject.toJSON(ret1));
			
			Object ret2 = wrapper.invoke(userServcie, "query", 
					UserServcie.class.getMethod("query", Integer.TYPE).getParameterTypes(),
					new Object[]{1233});
			
			System.out.println(JSONObject.toJSON(ret2));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@Test
	public void testCglibProxy() {
		final UserServcie userServcieImpl = new UserServcieImpl();
		
		Enhancer enhancer = new Enhancer();
		enhancer.setInterfaces(new Class[]{UserServcie.class});
//		enhancer.setSuperclass(UserServcieImpl.class);
		enhancer.setCallback(new MethodInterceptor() {
			
			@Override
			public Object intercept(Object arg0, Method arg1, Object[] arg2,
					MethodProxy arg3) throws Throwable {
				Object obj = arg3.invoke(userServcieImpl, arg2);
				return null;
			}
		});
//		enhancer.setClassLoader(UserServcieImpl.class.getClassLoader());
		UserServcie userServcie = (UserServcie)enhancer.create();
		System.out.println(userServcie);
		userServcie.add(new UserBean(111));
	}
}
