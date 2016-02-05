package com.zzc.proxy;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.zzc.channel.ChannelSubject;
import com.zzc.channel.Invoker;
import com.zzc.channel.impl.DefaultInvoker;
import com.zzc.exception.LeahException;
import com.zzc.main.config.CallTypeEnum;
import com.zzc.main.config.InvokerConfig;
import com.zzc.proxy.result.Result;
import com.zzc.register.ConnManager;
import com.zzc.util.ClassHelper;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * cglib 生成动态代理的时候，统一的拦截方法
 * 此方法就是我们拦截方法调用，执行rpc调用的地方
 *
 * @author ying
 */
public class ServiceInterceptor implements MethodInterceptor {
    private final Logger logger = LoggerFactory.getLogger(ServiceInterceptor.class);
    /**
     * 调用信息
     */
    private InvokerConfig invokerConfig;

    private Random random = new Random();

    /**
     * @param invokerConfig 代理的目标接口类
     */
    public ServiceInterceptor(InvokerConfig invokerConfig) {
        this.invokerConfig = invokerConfig;
    }

    @Override
    public Object intercept(Object arg0, Method arg1, Object[] arg2,
                            MethodProxy arg3) throws Throwable {

        long s = System.currentTimeMillis();

        StringBuffer callName = new StringBuffer();
        callName.append(invokerConfig.getItfCls().getName())
                .append("_")
                .append(invokerConfig.getVersion())
                .append(":")
                .append(arg1.getName())
                .append("(")
                .append(ClassHelper.getArgumentNamesStr(arg1))
                .append(")");
        Transaction t = Cat.newTransaction("leahCall", callName.toString());

        logger.debug("interceptor1 耗时:{}ms", System.currentTimeMillis() - s);

        Object ret = null;
        try {

            /**
             * 如何增加负载均衡
             * 获取channel列表
             * invoker对象传入channel列表
             * invoker中增加负载调用策略
             */
            ConnManager connManager = ConnManager.getManager();
            ChannelSubject channel;
            try {
                channel = this.getChannel(connManager.getChannelSubjects(invokerConfig.getServiceUrl()));
            } catch (IllegalArgumentException e) {
                logger.error("找不到服务:{}", invokerConfig.getServiceUrl(), e);
                throw new LeahException("找不到服务:" + invokerConfig.getServiceUrl());
            }


            //参数类型列表
            List<Class<?>> argumentsType = new ArrayList<Class<?>>();
            for (Object tmp : arg2) {
                argumentsType.add(tmp.getClass());
            }

            Class[] a = {};

            //构建请求对象
            Invocation invocation = new RpcInvocation(channel.genToken(), arg1.getName(), argumentsType.toArray(a), arg2, this.invokerConfig.getItfCls(), this.invokerConfig.getVersion());

            //构建调用者
            Invoker invoker = new DefaultInvoker(channel, invocation, invokerConfig);

            logger.debug("interceptor2 耗时:{}ms,{}", System.currentTimeMillis() - s, System.currentTimeMillis());

            Result result = null;
            if (this.invokerConfig.getCallType() == CallTypeEnum.future) {//异步调用
                ServiceFutureFactory.submit(invoker);
            } else if (this.invokerConfig.getCallType() == CallTypeEnum.syn) {//同步调用
                //执行调用
                result = invoker.doInvoke();

                logger.debug("interceptor3 耗时:{}ms,{}", System.currentTimeMillis() - s, System.currentTimeMillis());

                if (result.getException() != null) {//调用如果发生异常
                    throw result.getException();//服务端的异常被封装传送过来
                }
                //正常返回
                ret = result.getResult();

                logger.debug("interceptor4 耗时:{}ms", System.currentTimeMillis() - s);
            } else {
                logger.error("错误的调用类型callType");
                throw new IllegalArgumentException("错误的调用类型callType");
            }

            t.setStatus(Transaction.SUCCESS);
            return ret;
        } catch (Exception e) {
            t.setStatus(e);
            throw e;
        } finally {
            t.complete();

            logger.debug("interceptor 耗时:{}ms", System.currentTimeMillis() - s);
        }
    }

    /**
     * 选出一个channel
     *
     * @param channelSubjects
     * @return
     */
    private ChannelSubject getChannel(List<ChannelSubject> channelSubjects) {
        long s = System.currentTimeMillis();
        try {
            if (channelSubjects.size() == 0) {
//            logger.error("channel列表为空，请确定服务已启动");
                throw new IllegalArgumentException("channel列表为空，请确认对应服务是否已经启动");
            }
            int index = random.nextInt(channelSubjects.size());
            ChannelSubject channelSubject = channelSubjects.get(index);

            if (channelSubject != null && !channelSubject.isConnected()) {//确定得到的channel是能用的
                channelSubjects.remove(index);
                return getChannel(channelSubjects);
            }
            return channelSubject;
        } catch (RuntimeException e) {
            throw e;
        } finally {
            logger.debug("getChannel 耗时:{}ms", System.currentTimeMillis() - s);
        }
    }

}
