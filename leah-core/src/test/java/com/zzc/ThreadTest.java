package com.zzc;

import com.alibaba.fastjson.JSONObject;
import com.zzc.main.LeahReferManager;
import com.zzc.main.config.InvokerConfig;

/**
 * Created by ying on 15/5/25.
 */
public class ThreadTest implements Runnable {
    private int index;
    private InvokerConfig invokerConfig;
    public ThreadTest(int index,InvokerConfig invokerConfig){
        this.index = index;
        this.invokerConfig = invokerConfig;
    }
    @Override
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        UserService userService = (UserService) LeahReferManager.getManager().getInvoker(invokerConfig.getServiceUrl()).getRef();
        //					userServcie.add(new UserBean(1111111));

        System.out.println("****开始第"+index+"次调用****");
        UserBean userBean = userService.query(index);
        System.out.println(index+"--------->"+ JSONObject.toJSONString(userBean));

        userBean = userService.query(index,"第"+index+"调用");
        System.out.println(index+"--------->"+JSONObject.toJSONString(userBean));

        System.out.println("****第"+index+"次调用结束****");

    }
}
