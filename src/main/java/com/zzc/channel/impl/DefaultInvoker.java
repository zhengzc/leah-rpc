package com.zzc.channel.impl;

import com.zzc.channel.ChannelSubject;
import com.zzc.channel.Invoker;
import com.zzc.proxy.Invocation;
import com.zzc.result.Result;

import java.nio.channels.Channel;
import java.util.concurrent.CountDownLatch;

/**
 * Created by ying on 15/5/22.
 */
public class DefaultInvoker implements Invoker {
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

    public DefaultInvoker(ChannelSubject channel,Invocation invocation){
        this.channel = channel;
        this.token = invocation.getToken();
        this.invocation = invocation;
    }

    @Override
    public Result doInvoke() throws InterruptedException {
        //发送请求
        this.channel.write(invocation);
        //监听调用，等待返回
        this.channel.register(this);
        //等待返回
        this.gate.await();

        return this.result;
    }

    @Override
    public void setResult(Result result) {
        this.gate.countDown();
        this.result = result;
    }

    @Override
    public String getToken() {
        return this.token;
    }
}
