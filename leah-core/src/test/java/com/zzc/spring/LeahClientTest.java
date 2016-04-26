package com.zzc.spring;

import com.alibaba.fastjson.JSONObject;
import com.zzc.UserBean;
import com.zzc.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ying on 15/6/30.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-client.xml")
public class LeahClientTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private UserService userService;

    @Test
    public void testLeahClient() {
        long sTime = System.currentTimeMillis();
        userService.add(new UserBean(1111111));
        logger.info("耗时:{}ms", (System.currentTimeMillis() - sTime));
//        try{
//            Thread.sleep(1000*60*10);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 模拟多线程请求
     */
    @Test
    public void test2() {
        userService.add(new UserBean(33333333));
        try {
            ExecutorService executorService = Executors.newFixedThreadPool(1000);
            for (int i = 0; i < 10000; i++) {
                executorService.submit(new ThreadTest(i));
            }

            Thread.sleep(1000 * 60 * 10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 循环调用100次
     */
    @Test
    public void testStr() {
        for (int i = 0; i < 100; i++) {
            long sTime = System.currentTimeMillis();
            String ret = userService.testStr("asfjasjdf;lakjsdfjpoiwejfqpowiejrqjwerpqiower");
            logger.info("耗时:{}ms", System.currentTimeMillis() - sTime);
            logger.info("ret:{}", ret);
        }
    }

    /**
     * 循环调用100次
     */
    @Test
    public void testObj() {
        for (int i = 0; i < 1000; i++) {
            long sTime = System.currentTimeMillis();
            UserBean ret = userService.testObj(new UserBean(i));
            logger.info("---test---------耗时:{}ms", System.currentTimeMillis() - sTime);
            logger.info("ret:{}", JSONObject.toJSONString(ret));
        }

//        try{
//            Thread.sleep(1000*10);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
