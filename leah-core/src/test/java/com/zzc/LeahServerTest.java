package com.zzc;

import com.zzc.main.LeahServer;
import com.zzc.main.LeahServiceManager;
import com.zzc.main.config.ServerConfig;
import com.zzc.spring.LeahServiceRegister;
import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by ying on 15/5/21.
 */
public class LeahServerTest extends TestCase {
    private final Logger logger = Logger.getLogger(LeahServerTest.class);

    private int readBufferSize = 2048;
    private int idleTime = 10;
    private int port = 8825;
    private boolean autoSelectPort = true;
    private int coreServicePoolSize = 50;
    private int maxServicePoolSize = 300;
    private int workQueueSize = 300;

    @Test
    public void testRpcServer() {
        logger.info("test is starting!");
        try {
//            LeahContext.export(UserServcie.class, new UserServcieImpl());
//            Map<String,Object> services = new HashMap<String, Object>();
//            services.put();
//
//            LeahServiceRegister leahServiceRegister = new LeahServiceRegister();
//            leahServiceRegister.setServices(services);
//            leahServiceRegister.init();
            ServerConfig serverConfig = new ServerConfig();
            serverConfig.setReadBufferSize(this.readBufferSize);
            serverConfig.setIdleTime(this.idleTime);
            serverConfig.setPort(this.port);
            serverConfig.setAutoSelectPort(this.autoSelectPort);
            serverConfig.setCoreServicePoolSize(this.coreServicePoolSize);
            serverConfig.setMaxServicePoolSize(this.maxServicePoolSize);
            serverConfig.setWorkQueueSize(this.workQueueSize);

            LeahServiceManager leahServiceManager = LeahServiceManager.getManager();
            leahServiceManager.setServerConfig(serverConfig);
            leahServiceManager.addService("http://www.zhengzhichao.com.cn/com.zzc.UserService_1.0", new UserServiceImpl());

            LeahServer.start();
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
    public void test() {
        System.out.println(111);
    }
}
