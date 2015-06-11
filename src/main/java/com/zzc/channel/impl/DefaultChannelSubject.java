package com.zzc.channel.impl;

import com.zzc.channel.ChannelSubject;
import com.zzc.channel.Invoker;
import com.zzc.proxy.result.Result;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 默认的通道观察者
 * 将客户端与服务端通信的通道成为channel
 * @author ying
 *
 */
public class DefaultChannelSubject implements ChannelSubject {
    private final Logger logger = LoggerFactory.getLogger(DefaultChannelSubject.class);

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
        logger.debug("register Observer token is : {}",resultObserver.getToken());
		this.observerCache.put(resultObserver.getToken(), resultObserver);
	}

	@Override
	public void remove(Invoker resultObserver) {
        logger.debug("remove observer token is : {}",resultObserver.getToken());
        this.observerCache.remove(resultObserver.getToken());
	}

	@Override
	public void notifyObserver(Result result) {
        logger.debug("notify observer token is : {}",result.getToken());
		Invoker invoker = this.observerCache.get(result.getToken());//查询注册的观察者
        if(invoker != null){
            invoker.setResult(result);//调用观察者接口
        }else{
            logger.info("invoker is not exist:{}",result.getToken());
        }
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
