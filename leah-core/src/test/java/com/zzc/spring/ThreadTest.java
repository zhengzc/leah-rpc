package com.zzc.spring;

import com.alibaba.fastjson.JSONObject;
import com.zzc.UserBean;
import com.zzc.UserService;

/**
 * Created by ying on 15/7/14.
 */
public class ThreadTest extends Thread {
    private int index;
    private UserService userService;
    public ThreadTest(int index){
        this.index = index;
        this.userService = SpringContext.getBean("userService");
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("****开始第"+index+"次调用****");
        UserBean userBean = userService.query(index);
        System.out.println(index+"--------->"+ JSONObject.toJSONString(userBean));

        userBean = userService.query(index,"第"+index+"调用");
        System.out.println(index+"--------->"+JSONObject.toJSONString(userBean));

        System.out.println("****第"+index+"次调用结束****");

    }
}
