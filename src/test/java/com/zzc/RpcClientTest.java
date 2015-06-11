package com.zzc;

import com.alibaba.fastjson.JSONObject;
import com.zzc.main.RpcClient;
import com.zzc.main.config.CallTypeEnum;
import com.zzc.main.config.InterfaceConfig;
import com.zzc.main.config.RpcContext;
import com.zzc.proxy.ServiceFutureFactory;
import com.zzc.proxy.result.Result;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by ying on 15/5/21.
 */
public class RpcClientTest extends TestCase {

    /**
     * 异常测试
     */
    @Test
    public void testRpcClient(){
        RpcContext.refer(UserServcie.class);
        RpcClient.start();

        //模拟调用
        int i = 0;
        while(i < 1) {
            UserServcie userServcie = (UserServcie) (RpcContext.getReferServices().get(UserServcie.class).getRef());

            System.out.println("****开始第" + i + "次调用****");
            userServcie.testException();
            System.out.println("****第" + i + "次调用结束****");
            i++;
        }
    }

    /**
     * 多线程测试
     */
    @Test
    public void testRpcClient2(){
        RpcContext.refer(UserServcie.class);
        RpcClient.start();

        int i = 0;
        while(i < 1000){
            //模拟调用
            Thread t = new Thread(new ThreadTest(i));
            t.start();
            i++;
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试超时
     */
    @Test
    public void testRpcClient3(){
        InterfaceConfig interfaceConfig = RpcContext.refer(UserServcie.class);
        interfaceConfig.setTimeout(4000);
        RpcClient.start();

        //模拟调用
        int i = 0;
        while(i < 1) {
            UserServcie userServcie = (UserServcie)(RpcContext.getReferServices().get(UserServcie.class).getRef());
//					userServcie.add(new UserBean(1111111));

            System.out.println("****开始第" + i + "次调用****");
            userServcie.add(new UserBean(5555));
            System.out.println("****第" + i + "次调用结束****");
            i++;
        }
    }

    /**
     * 测试异步调用
     */
    @Test
    public void testRpcClient4(){
        InterfaceConfig interfaceConfig = RpcContext.refer(UserServcie.class);
        interfaceConfig.setTimeout(6000);
        interfaceConfig.setCallType(CallTypeEnum.future);
        RpcClient.start();

        //模拟调用
        int i = 0;
        while(i < 10) {
            Thread t = new Thread(new FutureThread(i));
            t.start();
            i++;
        }

        try {
            Thread.sleep(1000*100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * future多线程调用测试
     */
    class FutureThread implements Runnable{
        private int i;
        public FutureThread(int i){
            this.i = i;
        }
        @Override
        public void run() {
            UserServcie userServcie = (UserServcie)(RpcContext.getReferServices().get(UserServcie.class).getRef());
//					userServcie.add(new UserBean(1111111));

            System.out.println("****开始第" + i + "次调用****");
            userServcie.query(i, "第"+i+"次调用");
            System.out.println("****第" + i + "次调用结束****");

            Future<Result> future = ServiceFutureFactory.getFuture();
            try {
                Result result = future.get();
                UserBean userBean = (UserBean)result.getResult();
                System.out.println("第"+i+"次调用:"+JSONObject.toJSONString(userBean));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

}
