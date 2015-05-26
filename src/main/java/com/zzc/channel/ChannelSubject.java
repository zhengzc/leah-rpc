package com.zzc.channel;

import com.zzc.result.Result;

/**
 * 通道，抽象为客户端和服务端通信的媒介，比如一个tcp链接
 * 并且这是一个主题，可以向其订阅
 * 此类的主要功能是完成通道上面的监听，将客户端收到的数据分发到指定的订阅者，并且能够发送请求给server
 * @author ying
 *
 */
public interface ChannelSubject{
	/**
	 * 生成一个与通道有关的唯一token
	 * @return
	 */
	public String genToken();
	/**
	 * 向通道写入一个对象
	 * @param obj
	 */
	public void write(Object obj);
	
	/**
	 * 注册一个订阅者
	 * @param resultObserver 订阅者
	 */
	public void register(Invoker resultObserver);
	
	/**
	 * 删除一个订阅者
	 * @param resultObserver
	 */
	public void remove(Invoker resultObserver);
	
	/**
	 * 调用此方法来通知一个订阅者
	 * @param result 
	 */
	public void notifyObserver(Result result);
}
