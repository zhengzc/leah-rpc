package com.zzc.channel.impl;

import com.zzc.channel.ChannelSubject;
import com.zzc.channel.Invoker;
import com.zzc.main.config.InterfaceConfig;
import com.zzc.main.config.RpcContext;
import com.zzc.proxy.Invocation;
import com.zzc.proxy.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by ying on 15/5/22.
 */
public class DefaultInvoker implements Invoker{
    private final Logger logger = LoggerFactory.getLogger(DefaultInvoker.class);

    /**
     * 通道
     */
    private ChannelSubject channel;
    /**
     * 声明一个闭锁，用来同步异步线程
     */
    private CountDownLatch gate = new CountDownLatch(1);
    /**
     * 调用参数
     */
    private Invocation invocation;
    /**
     * 返回值
     */
    private Result result;
    /**
     * 每个Invoker的唯一标示，token
     */
    private String token;

    /**
     * @param channel
     * @param invocation
     */
    public DefaultInvoker(ChannelSubject channel,Invocation invocation){
        this.channel = channel;
        this.token = invocation.getToken();
        this.invocation = invocation;
    }

    @Override
    public Result doInvoke() throws InterruptedException,TimeoutException {
        logger.debug("doInvoke");

        //获取当前接口的配置信息
        InterfaceConfig itfCfg = RpcContext.getReferServices().get(invocation.getInterface());

        //发送请求
        this.channel.write(invocation);
        //监听调用，等待返回
        this.channel.register(this);
        logger.debug("register success,wait return");
        //等待返回
        Boolean isSuccess = this.gate.await(itfCfg.getTimeout(), TimeUnit.MILLISECONDS);

        if(!isSuccess){
            throw new TimeoutException("request timeout");
        }else{//返回成功的话直接删除
            this.channel.remove(this);
        }
        logger.debug("call success,token is {}",result.getToken());
        return this.result;
    }

    @Override
    public Result call() throws Exception {
        return this.doInvoke();
    }

    @Override
    public void setResult(Result result) {
        logger.debug("set result,token is {}",result.getToken());
        this.result = result;//这个要放在gate.countDown的前面，要不然坑死你
        this.gate.countDown();
    }

    @Override
    public String getToken() {
        return this.token;
    }
}
