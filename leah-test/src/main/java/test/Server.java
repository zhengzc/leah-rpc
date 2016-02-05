package test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by ying on 16/2/5.
 */
public class Server {
    public static void main(String[] args){
        ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext(new String[]{"classpath:spring/applicationContext-service.xml"});
        classPathXmlApplicationContext.start();

        try {
            Thread.sleep(1000*60*60);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
