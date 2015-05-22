package com.zzc;

import com.alibaba.fastjson.JSONObject;
import com.zzc.main.RpcClient;
import com.zzc.main.RpcContext;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * Created by ying on 15/5/21.
 */
public class RpcClientTest extends TestCase {

    @Test
    public void testRpcClient(){
        RpcContext.refer(UserServcie.class);
        RpcClient.start();

        //模拟调用
        int i = 0;
        while(i < 60) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            UserServcie userServcie = (UserServcie) RpcContext.referServicesMap.get(UserServcie.class);
//					userServcie.add(new UserBean(1111111));

            System.out.println("****开始第" + i + "次调用****");
            UserBean userBean = userServcie.query(i);
            System.out.println(i + "--------->" + JSONObject.toJSONString(userBean));

            userBean = userServcie.query(i, "第" + i + "调用");
            System.out.println(i + "--------->" + JSONObject.toJSONString(userBean));

            System.out.println("****第" + i + "次调用结束****");
            i++;
        }
    }

    @Test
    public void testRpcClient2(){
        RpcContext.refer(UserServcie.class);
        RpcClient.start();

        //模拟调用
        new Thread(){
            @Override
            public void run() {
                int i = 0;
                while(i < 60){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    UserServcie userServcie = (UserServcie) RpcContext.referServicesMap.get(UserServcie.class);
//					userServcie.add(new UserBean(1111111));

                    System.out.println("****开始第"+i+"次调用****");
                    UserBean userBean = userServcie.query(i*100);
                    System.out.println(i+"--------->"+JSONObject.toJSONString(userBean));

                    userBean = userServcie.query(i*100,"第"+i*100+"调用");
                    System.out.println(i+"--------->"+JSONObject.toJSONString(userBean));

                    System.out.println("****第"+i+"次调用结束****");
                    i++;
                }
            }
        }.start();

        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
