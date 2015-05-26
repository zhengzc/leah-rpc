package com.zzc;

import com.alibaba.fastjson.JSONObject;
import com.zzc.main.RpcContext;

/**
 * Created by ying on 15/5/25.
 */
public class ThreadTest implements Runnable {
    private int index;
    public ThreadTest(int index){
        this.index = index;
    }
    @Override
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        UserServcie userServcie = (UserServcie) RpcContext.referServicesMap.get(UserServcie.class);
        //					userServcie.add(new UserBean(1111111));

        System.out.println("****开始第"+index+"次调用****");
        UserBean userBean = userServcie.query(index);
        System.out.println(index+"--------->"+ JSONObject.toJSONString(userBean));

        userBean = userServcie.query(index,"第"+index+"调用");
        System.out.println(index+"--------->"+JSONObject.toJSONString(userBean));

        System.out.println("****第"+index+"次调用结束****");

    }
}
