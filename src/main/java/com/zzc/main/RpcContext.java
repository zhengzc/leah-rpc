package com.zzc.main;

import java.util.HashMap;
import java.util.Map;

/**
 *rpc调用的上下文，此类记录导出服务列表，引用服务列表
 * @author ying
 */
public class RpcContext {
	public static Map<String, Object> exportServicesMap = new HashMap<String, Object>();//导出服务列表
	public static Map<Class<?>, Object> referServicesMap = new HashMap<Class<?>, Object>();//引入服务列表
	
	/**
	 * 导出服务
	 * @param itf 接口名
	 * @param service 接口对应的服务实现
	 */
	public static void export(Class<?> itf,Object service){
		exportServicesMap.put(itf.getName(), service);
	}
	
	/**
	 * 引入服务
	 * @param itf 接口名
	 * @return
	 */
	public static void refer(Class<?> itf){
		referServicesMap.put(itf, null);
	}
}
