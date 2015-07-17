package com.zzc.spring;

import com.zzc.UserBean;
import com.zzc.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by ying on 15/6/30.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-client.xml")
public class LeahClientTest {
    @Test
    public void testLeahClient(){
        UserService userService = SpringContext.getBean("userService");
        userService.add(new UserBean(1111111));
        try{
            Thread.sleep(1000*60*10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 模拟多线程请求
     */
    @Test
    public void test2(){

        for(int i = 0 ; i < 100 ; i++){
            com.zzc.spring.ThreadTest threadTest = new ThreadTest(i);
            threadTest.start();
        }

        try{
            Thread.sleep(1000*60*10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



}
