package com.zzc.main.config;

import java.util.HashMap;
import java.util.Map;

/**
 *rpc调用的上下文，此类记录导出服务列表，引用服务列表
 * @author ying
 */
public class RpcContext {
    /**
     * 服务端存储的接口名->接口对应的实现
     */
	private static Map<String, Object> exportServicesMap = new HashMap<String, Object>();//导出服务列表
    /**
     * 客户端存储接口的名
     */
	private static Map<Class<?>, InterfaceConfig> referServicesMap = new HashMap<Class<?>, InterfaceConfig>();//引入服务列表
	
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
	public static InterfaceConfig refer(Class<?> itf){
        InterfaceConfig interfaceConfig = new InterfaceConfig(itf);
		referServicesMap.put(itf, interfaceConfig);
        return interfaceConfig;
	}

    /**
     * 获取导出服务
     * @return
     */
    public static Map<String,Object> getExportServices(){
        return exportServicesMap;
    }

    /**
     * 获取引用服务列表
     * @return
     */
    public static Map<Class<?>,InterfaceConfig> getReferServices(){
        return referServicesMap;
    }
}
