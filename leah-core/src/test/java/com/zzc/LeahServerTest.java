package com.zzc;

import com.zzc.main.LeahServer;
import com.zzc.spring.LeahServiceRegister;
import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ying on 15/5/21.
 */
public class LeahServerTest extends TestCase{
    private final Logger logger = Logger.getLogger(LeahServerTest.class);

    @Test
    public void testRpcServer(){
        logger.info("test is starting!");
        try {
//            LeahContext.export(UserServcie.class, new UserServcieImpl());
            Map<String,Object> services = new HashMap<String, Object>();
            services.put("http://www.zhengzhichao.com.cn/com.zzc.UserService_1.0",new UserServiceImpl());

            LeahServiceRegister leahServiceRegister = new LeahServiceRegister();
            leahServiceRegister.setServices(services);
            leahServiceRegister.init();

            LeahServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        CountDownLatch countDownLatch = new CountDownLatch(1);
//        try {
//            countDownLatch.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    @Test
    public void test(){
        System.out.println(111);
    }
}
