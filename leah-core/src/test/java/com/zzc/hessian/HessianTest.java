package com.zzc.hessian;

import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;
import com.zzc.UserBean;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ying on 16/2/3.
 */
public class HessianTest {
    @Test
    public void testHessian() {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        SerializerFactory serializerFactory = new SerializerFactory();
//        SerializerFactory serializerFactory = null;

        ExecutorService executorService = Executors.newFixedThreadPool(100);
//        ExecutorService executorService = Executors.newCachedThreadPool(1);
        for (int i = 0; i < 100; i++) {
            TestThread t = new TestThread(new UserBean(i), serializerFactory, countDownLatch);
//            executorService.submit(t);
            t.start();
        }


        try {
            Thread.sleep(1000 * 2);
            countDownLatch.countDown();
            Thread.sleep(1000 * 60 * 10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testHessianOneThread() {
        for (int i = 0; i < 2; i++) {
            testMethod(new UserBean(i));
        }
    }


    class TestThread extends Thread {
        private UserBean userBean;
        private SerializerFactory serializerFactory;
        private CountDownLatch countDownLatch;

        public TestThread(UserBean userBean, SerializerFactory serializerFactory, CountDownLatch countDownLatch) {
            this.userBean = userBean;
            this.serializerFactory = serializerFactory;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            ByteArrayOutputStream byteOutputStream = null;
            Hessian2Output hessian2Output = null;
            try {
//                Thread.sleep(userBean.getUserId() * 1000);
                //声明二进制数组
                byteOutputStream = new ByteArrayOutputStream();
                hessian2Output = new Hessian2Output(byteOutputStream);
//                hessian2Output.setSerializerFactory(serializerFactory);
                countDownLatch.await();
                long s = System.currentTimeMillis();
                //写入序列化信息
                hessian2Output.writeObject(userBean);
                System.out.println("耗时" + (System.currentTimeMillis() - s));
                hessian2Output.flush();//将序列化信息发送出去

                //准备写入数据
                byte[] object = byteOutputStream.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void testMethod(UserBean userBean) {
        ByteArrayOutputStream byteOutputStream = null;
        Hessian2Output hessian2Output = null;
        try {
//                Thread.sleep(userBean.getUserId() * 1000);
            //声明二进制数组
            byteOutputStream = new ByteArrayOutputStream();
            hessian2Output = new Hessian2Output(byteOutputStream);
            long s = System.currentTimeMillis();
            //写入序列化信息
            hessian2Output.writeObject(userBean);
            System.out.println("耗时" + (System.currentTimeMillis() - s));
            hessian2Output.flush();//将序列化信息发送出去

            //准备写入数据
            byte[] object = byteOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
