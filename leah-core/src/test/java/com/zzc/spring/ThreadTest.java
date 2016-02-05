package com.zzc.spring;

import com.alibaba.fastjson.JSONObject;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.zzc.UserBean;
import com.zzc.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ying on 15/7/14.
 */
public class ThreadTest extends Thread {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private int index;
    private UserService userService;

    public ThreadTest(int index) {
        this.index = index;
        this.userService = SpringContext.getBean("userService");
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000 * 1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.info("开始调用{}", index);

        Transaction t = Cat.newTransaction("test", "ThreadTest.testObj");
        long startTime = System.currentTimeMillis();
        UserBean userBean = userService.testObj(new UserBean(index));
        logger.info("调用结束{},耗时:{}ms", index, (System.currentTimeMillis() - startTime));
        t.setStatus(Transaction.SUCCESS);
        t.complete();

        logger.info("{}--------->{}", index, JSONObject.toJSONString(userBean));
    }
}
