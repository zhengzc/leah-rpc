package com.zzc.handler;

import com.alibaba.fastjson.JSONObject;
import com.zzc.proxy.Invocation;
import com.zzc.proxy.ProxyFactory;
import com.zzc.result.Result;
import com.zzc.result.impl.DefaultResult;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 * Created by ying on 15/5/18.
 * 服务端handler,服务端收到消息将在这里开始处理
 */
public class ServiceHandler extends IoHandlerAdapter {
    @Override
    public void sessionCreated(IoSession session) throws Exception {
        System.out.println("创建");
        super.sessionCreated(session);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        System.out.println("打开");
        super.sessionOpened(session);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        System.out.println("关闭");
        super.sessionClosed(session);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        System.out.println("空闲");
        super.sessionIdle(session, status);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        cause.printStackTrace();
        super.exceptionCaught(session, cause);
    }

    /**
     * 收到消息
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        System.out.println("serverMsg--------->start");
        System.out.println("serverMsg--------->" + message.toString());
        //客户端收到的消息统一转换为Invocation对象
        Invocation invocation = (Invocation)message;

        //执行调用
        Object obj = ProxyFactory.doInvoker(invocation);

        //装配返回结果 返回结果统一装配为Result
        Result result = new DefaultResult(invocation.getToken(),obj);

        //写入返回数据
        session.write(result);

        System.out.println("serverMsg--------->end");
//					System.out.println("--->"+JSONObject.toJSONString(obj));
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        System.out.println("发送");
        super.messageSent(session, message);
    }
}
