package com.zzc.proxy;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.zzc.exception.LeahException;
import com.zzc.main.LeahServiceManager;
import com.zzc.main.config.InvokerConfig;
import com.zzc.util.ClassHelper;
import net.sf.cglib.proxy.Enhancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 生成代理类
 * 包括生成客户端代理和服务端的包装类
 *
 * @author ying
 */
public class ProxyFactory {
    private static final Logger logger = LoggerFactory.getLogger(ProxyFactory.class);
    private static final String DEFAULT_URL_PRE = "http://www.zhengzhichao.com.cn/";
    /**
     * 存储代理类的缓存
     */
    private static Map<String, Object> proxyObjectCache = new ConcurrentHashMap<String, Object>();
    /**
     * 存储包装类的缓存
     */
    private static Map<String, JavassistWrapper> wrapObjectCache = new ConcurrentHashMap<String, JavassistWrapper>();

    /**
     * 生成客户端接口的代理类
     * 接口相同的代理类只生成一次
     *
     * @param invokerConfig 接口
     * @return
     */
    public static <T> T getProxy(InvokerConfig invokerConfig) {
        /**
         * 这里可以优化一下，为什么每次都要创建新的代理类？我们直接将代理类缓存行不？
         */
        String serviceUrl = invokerConfig.getServiceUrl();

        if (proxyObjectCache.containsKey(serviceUrl)) {
            return (T) proxyObjectCache.get(serviceUrl);
        } else {
            //创建动态代理
            Enhancer enhancer = new Enhancer();
            enhancer.setInterfaces(new Class[]{invokerConfig.getItfCls()});
            enhancer.setCallback(new ServiceInterceptor(invokerConfig));

            //添加到缓存
            proxyObjectCache.put(serviceUrl, enhancer.create());

            return (T) enhancer.create();

        }

    }

    /**
     * 执行方法调用并返回结果
     *
     * @param invocation 调用信息
     * @return
     * @throws Exception
     */
    public static Object doInvoker(Invocation invocation) throws Exception {

        long s = System.currentTimeMillis();
        //处理不同的服务
//		Object service = LeahContext.getExportServices().get(itf.getName());//实现类
        LeahServiceManager leahServiceManager = LeahServiceManager.getManager();
        String serviceUrl = getServiceUrl(invocation);
        Object service = leahServiceManager.getService(serviceUrl);

        String methodName = invocation.getMethodName();//方法名
        Class<?>[] paramType = invocation.getArgumentsType();//参数类型列表
        Object[] value = invocation.getArguments();//参数值列表

        StringBuilder serviceName = new StringBuilder();
        serviceName.append(invocation.getInterface().getName())
                .append("_")
                .append(invocation.getVersion())
                .append(":")
                .append(methodName)
                .append("(")
                .append(ClassHelper.getArgumentNamesStr(paramType))
                .append(")");
        Transaction t = Cat.newTransaction("leahService", serviceName.toString());
        //处理paramType中的基本类型
//		for(int i = 0 ; i < paramType.length ; i++){
//			if(isWrapClass(paramType[i])){
//				paramType[i] = paramType[i].getField("TYPE").;
//			}
//		}

        Object ret;
        try {

            if (wrapObjectCache.containsKey(serviceUrl)) {//如果存在
                JavassistWrapper wrap = wrapObjectCache.get(serviceUrl);
                ret = wrap.invoke(service, methodName, paramType, value);
            } else {
                JavassistWrapper wrap = JavassistWrapper.create(invocation.getInterface());//创建一个新的
                wrapObjectCache.put(serviceUrl, wrap);//添加到缓存
                ret = wrap.invoke(service, methodName, paramType, value);
            }
            t.setStatus(Transaction.SUCCESS);
            return ret;
        } catch (Exception e) {
            t.setStatus(e);
            throw e;
        } finally {
            t.complete();
            logger.info("leahService 耗时:{}ms,{}", System.currentTimeMillis() - s, System.currentTimeMillis());
        }

    }

    /**
     * 将invocation转化为url调用
     *
     * @param invocation
     * @return
     */
    private static String getServiceUrl(Invocation invocation) {
        StringBuilder url = new StringBuilder(DEFAULT_URL_PRE);
        url.append(invocation.getInterface().getName())
                .append("_")
                .append(invocation.getVersion());
        return url.toString();
    }
}
