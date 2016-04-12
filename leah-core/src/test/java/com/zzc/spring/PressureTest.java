package com.zzc.spring;

import com.zzc.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ying on 16/2/4.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-client.xml")
public class PressureTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private UserService userService;

    /**
     * 随机生成一定kb大小的string 英文字符
     */
    private String genString(int kb) {
        StringBuffer buffer = new StringBuffer("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        int range = buffer.length();
        for (int i = 0; i < kb*1024; i++) {
            sb.append(buffer.charAt(random.nextInt(range)));
        }

        String tmp = sb.toString();
        logger.info("生成字符串大小为{}byte {}kb", tmp.getBytes().length, tmp.getBytes().length / 1024);
        return tmp;
    }

    @Test
    public void stringTest() {

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 1; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    String tmp = genString(1);
                    while (true) {
                        long s = System.currentTimeMillis();
                        userService.testStr(tmp);
                        logger.info("-->{}ms", System.currentTimeMillis() - s);
                    }
                }
            });
        }
        int count = 0;
        while (true) {
            try {
                count++;
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (count > 60 * 30) {
                break;
            }
        }
    }
}
