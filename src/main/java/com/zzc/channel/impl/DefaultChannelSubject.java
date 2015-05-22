package com.zzc.channel.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.mina.core.session.IoSession;

import com.zzc.channel.ChannelSubject;
import com.zzc.result.Result;
import com.zzc.channel.Invoker;

/**
 * 默认的通道观察者
 * 将客户端与服务端通信的通道成为channel
 * @author ying
 *
 */
public class DefaultChannelSubject implements ChannelSubject {
	private AtomicLong incrementLong = new AtomicLong();//自增的id
	
	private IoSession session;
	/**
	 * 观察者缓存
	 */
	private Map<String, Invoker> observerCache = new ConcurrentHashMap<String, Invoker>();
	
	public DefaultChannelSubject(IoSession session) {
		this.session = session;
	}

	@Override
	public void register(Invoker resultObserver) {
		this.observerCache.put(resultObserver.getToken(), resultObserver);
	}

	@Override
	public void remove(Invoker resultObserver) {
        this.observerCache.remove(resultObserver.getToken());
	}

	@Override
	public void notifyOberver(Result result) {
		Invoker resultObserver = this.observerCache.get(result.getToken());//查询注册的观察者
		resultObserver.setResult(result);//调用观察者接口
	}

	@Override
	public void write(Object message) {
		session.write(message);
	}

	@Override
	public String genToken() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(this.session.getId())
						.append(this.incrementLong.incrementAndGet());
		return stringBuilder.toString();
	}

}
