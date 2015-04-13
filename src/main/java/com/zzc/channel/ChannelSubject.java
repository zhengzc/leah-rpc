package com.zzc.channel;

import com.zzc.result.Result;
import com.zzc.result.ResultObserver;

/**
 * 通道，抽象为客户端和服务端通信的媒介，比如一个tcp链接
 * 并且这是一个主题，可以向其订阅
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
	 * 写入一个对象
	 * @param obj
	 */
	public void write(Object obj);
	
	/**
	 * 注册一个订阅者
	 * @param resultObserver 订阅者
	 */
	public void register(ResultObserver resultObserver);
	
	/**
	 * 删除一个订阅者
	 * @param resultObserver
	 */
	public void remove(ResultObserver resultObserver);
	
	/**
	 * 调用此方法来通知一个订阅者
	 * @param result 
	 */
	public void notifyOberver(Result result);
}
