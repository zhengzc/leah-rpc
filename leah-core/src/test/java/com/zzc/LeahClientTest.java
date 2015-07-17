package com.zzc;

import com.alibaba.fastjson.JSONObject;
import com.zzc.main.LeahClient;
import com.zzc.main.LeahReferManager;
import com.zzc.main.config.CallTypeEnum;
import com.zzc.main.config.InvokerConfig;
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
public class LeahClientTest extends TestCase {

    /**
     * 异常测试
     */
    @Test
    public void testRpcClient() throws Exception{
        LeahReferManager leahReferManager = LeahReferManager.getManager();
        InvokerConfig invokerConfig = leahReferManager.addInvoker("http://www.zhengzhichao.com.cn/com.zzc.UserService_1.0",UserService.class);
        LeahClient leahClient = new LeahClient("127.0.0.1",8825);
        leahClient.start();

        //模拟调用
        int i = 0;
        while(i < 1) {
            UserService userService = (UserService)leahReferManager.getInvoker(invokerConfig.getServiceUrl()).getRef();

            System.out.println("****开始第" + i + "次调用****");
            userService.testException();
            System.out.println("****第" + i + "次调用结束****");
            i++;
        }
    }

    /**
     * 多线程测试
     */
    @Test
    public void testRpcClient2() throws Exception{
        LeahReferManager leahReferManager = LeahReferManager.getManager();
        InvokerConfig invokerConfig = leahReferManager.addInvoker("http://www.zhengzhichao.com.cn/com.zzc.UserService_1.0", UserService.class);
        LeahClient leahClient = new LeahClient("127.0.0.1",8825);
        leahClient.start();

        int i = 0;
        while(i < 1000){
            //模拟调用
            Thread t = new Thread(new ThreadTest(i,invokerConfig));
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
    public void testRpcClient3() throws Exception{
        LeahReferManager leahReferManager = LeahReferManager.getManager();
        InvokerConfig invokerConfig = leahReferManager.addInvoker("http://www.zhengzhichao.com.cn/com.zzc.UserService_1.0", UserService.class, 4000);
        LeahClient leahClient = new LeahClient("127.0.0.1",8825);
        leahClient.start();

        //模拟调用
        int i = 0;
        while(i < 1) {
            UserService userService = (UserService)leahReferManager.getInvoker(invokerConfig.getServiceUrl()).getRef();
//					userServcie.add(new UserBean(1111111));

            System.out.println("****开始第" + i + "次调用****");
            userService.add(new UserBean(5555));
            System.out.println("****第" + i + "次调用结束****");
            i++;
        }
    }

    /**
     * 测试异步调用
     */
    @Test
    public void testRpcClient4() throws Exception{
        LeahReferManager leahReferManager = LeahReferManager.getManager();
        InvokerConfig invokerConfig = leahReferManager.addInvoker("http://www.zhengzhichao.com.cn/com.zzc.UserService_1.0",UserService.class,6000,CallTypeEnum.future);
        LeahClient leahClient = new LeahClient("127.0.0.1",8825);
        leahClient.start();

        //模拟调用
        int i = 0;
        while(i < 10) {
            Thread t = new Thread(new FutureThread(i,invokerConfig));
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
        private InvokerConfig invokerConfig;
        public FutureThread(int i,InvokerConfig invokerConfig){
            this.i = i;
            this.invokerConfig = invokerConfig;
        }
        @Override
        public void run() {
            UserService userService = (UserService)LeahReferManager.getManager().getInvoker(invokerConfig.getServiceUrl()).getRef();
//					userServcie.add(new UserBean(1111111));

            System.out.println("****开始第" + i + "次调用****");
            userService.query(i, "第"+i+"次调用");
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
