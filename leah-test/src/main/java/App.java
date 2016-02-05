import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * Created by ying on 16/2/5.
 */
public class App {
    public static void main(String[] args){
        if(args.length == 0){
            System.out.println("参数错误");
            return;
        }

        if("startServer".equals(args[0])){
            ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext(new String[]{"classpath:spring/applicationContext-service.xml"});
            classPathXmlApplicationContext.start();

            try {
                System.out.println("任意键退出");
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }

            classPathXmlApplicationContext.stop();
            System.out.println("服务退出");
        }else{
            System.out.println("错误的命令");
        }
    }
}
