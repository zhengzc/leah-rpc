package com.zzc.proxy;

import com.zzc.proxy.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by ying on 15/6/9.
 * 异步执行获取返回值的一个工具类
 */
public class ServiceFutureFactory {
    private static final Logger logger = LoggerFactory.getLogger(ServiceFutureFactory.class);
    /**
     * 任务缓存
     */
    private static ThreadLocal<Future<Result>> threadFuture = new ThreadLocal<Future<Result>>();

    /**
     * 线程池
     */
    private static ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * 提交一个Invoker
     * 此方法的这个限制
     * @param callable
     */
    protected static void submit(Callable<Result> callable){
        if(threadFuture.get() != null){
            threadFuture.remove();
            String err = "if you set CallType is \"future\",you must call \"ServiceFutureFactory.getFuture\" or \"ServiceFutureFactory.remove\" first before next remote call";
            logger.error(err);
            throw new IllegalArgumentException(err);
        }
        Future<Result> resultFuture = executorService.submit(callable);
        threadFuture.set(resultFuture);
    }

    /**
     * 获取最近的一次调用future
     * @return
     */
    public static Future<Result> getFuture(){
        Future<Result> future = threadFuture.get();
        threadFuture.remove();
        return future;
    }

}
