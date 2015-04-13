package com.zzc.proxy;

import java.io.Serializable;

/**
 * @author ying
 * 发起rpc调用的时候，调用信息传递封装在此类中
 */
public class RpcInvocatioin implements Invocation,Serializable {
	/**
	 * 方法名
	 */
	private String methodName;
	/**
	 * 参数类型列表
	 */
	private Class[] argumentsType;
	/**
	 * 参数列表
	 */
	private Object[] arguments;
	/**
	 * 调用的接口名
	 */
	private Class<?> itf;
	/**
	 * 每次调用的token，作为唯一标示
	 */
	private String token;
	
	/**
	 * 
	 * @param methodName 方法名
	 * @param argumentsType 参数类型列表
	 * @param arguments 参数列表
	 * @param itf 接口名
	 */
	public RpcInvocatioin(String token,String methodName,Class[] argumentsType,Object[] arguments,Class<?> itf){
		this.token = token;
		this.methodName = methodName;
		this.arguments= arguments;
		this.argumentsType = argumentsType;
		this.itf = itf;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Class[] getArgumentsType() {
		return argumentsType;
	}

	public void setArgumentsType(Class[] argumentsType) {
		this.argumentsType = argumentsType;
	}

	public Object[] getArguments() {
		return arguments;
	}

	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}

	public Class<?> getItf() {
		return itf;
	}

	public void setItf(Class<?> itf) {
		this.itf = itf;
	}

	@Override
	public Class<?> getInterface() {
		return this.itf;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
