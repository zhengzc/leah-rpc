package com.zzc.channel.impl;

import com.zzc.channel.ChannelSubject;
import com.zzc.channel.Invoker;
import com.zzc.proxy.Invocation;
import com.zzc.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.Channel;
import java.util.concurrent.CountDownLatch;

/**
 * Created by ying on 15/5/22.
 */
public class DefaultInvoker implements Invoker {
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
    public Result doInvoke() throws InterruptedException {
        logger.debug("doInvoke");
        //发送请求
        this.channel.write(invocation);
        //监听调用，等待返回
        this.channel.register(this);
        logger.debug("register success,wait return");
        //等待返回
        this.gate.await();

        logger.debug("call success,token is {}",result.getToken());
        return this.result;
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
