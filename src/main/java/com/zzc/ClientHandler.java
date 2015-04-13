package com.zzc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.zzc.result.Result;
import com.zzc.result.ResultObserver;
import com.zzc.result.ResultSubject;

public class ClientHandler extends IoHandlerAdapter implements ResultSubject{
	/**
	 * 观察者缓存
	 */
	private Map<String, ResultObserver> observerCache = new ConcurrentHashMap<String, ResultObserver>();
	
	private IoSession ioSession ;

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		super.sessionOpened(session);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		super.exceptionCaught(session, cause);
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		Result result = (Result)message;//获取结果
		this.notifyOberver(result);//调用通知
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		super.messageSent(session, message);
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
		this.ioSession = session;
	}

	@Override
	public void register(ResultObserver resultObserver) {
		this.observerCache.put(resultObserver.getToken(), resultObserver);
	}

	@Override
	public void remove(ResultObserver resultObserver) {
		this.observerCache.remove(resultObserver.getToken());
	}

	@Override
	public void notifyOberver(Result result) {
		ResultObserver resultObserver = this.observerCache.get(result.getToken());//查询注册的观察者
		resultObserver.setResult(result);//调用观察者接口
	}
}
