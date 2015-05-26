package com.zzc;

import com.zzc.main.RpcContext;
import com.zzc.main.RpcServer;
import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by ying on 15/5/21.
 */
public class RpcServerTest extends TestCase{
    private final Logger logger = Logger.getLogger(RpcServerTest.class);

    @Test
    public void testRpcServer(){
        logger.info("test is starting!");
        try {
            RpcContext.export(UserServcie.class,new UserServcieImpl());

            RpcServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test(){
        System.out.println(111);
    }
}
