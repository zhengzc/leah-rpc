import com.zzc.spring.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import service.UserService;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ying on 16/2/5.
 */
public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
//        args = new String[]{"startServer"};

        if(args.length == 0){
            System.out.println("参数错误");
            return;
        }

        if("startServer".equals(args[0])) {
            ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext(new String[]{"classpath:spring/test-service.xml"});
            classPathXmlApplicationContext.start();

            try {
                Thread.sleep(1000*1000000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                System.out.println("任意键退出");
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }

            classPathXmlApplicationContext.stop();
            System.out.println("服务退出");
        }else if("stringTest".equals(args[0])){
            ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext(new String[]{"classpath:spring/test-client.xml"});
            classPathXmlApplicationContext.start();

            final UserService userService = SpringContext.getBean("userService");

            ExecutorService executorService = Executors.newFixedThreadPool(10);

            for (int i = 0; i < 10; i++) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            long s = System.currentTimeMillis();
                            userService.testStr(genString(1000));
                            logger.info("-->{}ms", System.currentTimeMillis() - s);
                        }
                    }

                    /**
                     * 随机生成一定b大小的string
                     */
                    private String genString(int num) {
                        StringBuffer buffer = new StringBuffer("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
                        StringBuffer sb = new StringBuffer();
                        Random random = new Random();
                        int range = buffer.length();
                        for (int i = 0; i < (1000 * num) / 2; i++) {
                            sb.append(buffer.charAt(random.nextInt(range)));
                        }
                        return sb.toString();
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

                if (count > 60 * 10) {
                    break;
                }
            }
        }else{
            System.out.println("错误的命令");
        }

    }
}
