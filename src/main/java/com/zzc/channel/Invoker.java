package com.zzc.channel;

import com.zzc.result.Result;

/**
 * 调用者，每当客户端发起调用的时候，将由此接口实例化出一个调用者，发起一次调用
 * 另外此接口也是是一个订阅者
 * @author ying
 *
 */
public interface Invoker {
    /**
     * 发起一次调用
     * @return
     */
    public Result doInvoke() throws InterruptedException;
	/**
	 * 设置result
	 * 此方法是通道收到消息时候通知调用者的接口
	 * @param result 设置结果
	 * @return
	 */
	public void setResult(Result result);
	
	/**
	 * 获取token值
	 * token标示了每一个观察者唯一性
	 * @return
	 */
	public String getToken();
}
