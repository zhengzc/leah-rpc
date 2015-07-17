package com.zzc.spring;

import com.zzc.main.LeahReferManager;
import com.zzc.main.LeahServiceManager;
import com.zzc.register.ConnManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStoppedEvent;

import java.io.IOException;

/**
 * Created by ying on 15/7/8.
 * 保存applictionContext的类
 */
public class SpringContext implements ApplicationContextAware,ApplicationListener {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static ApplicationContext appContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appContext = applicationContext;
    }

    /**
     * 获取bean
     * @param cls
     * @param <T>
     * @return
     */
    public static <T>T getBean(Class<T> cls){
        return appContext.getBean(cls);
    }

    /**
     * 获取bean
     * @param id
     * @param <T>
     * @return
     */
    public static <T> T getBean(String id){
        return (T)appContext.getBean(id);
    }
    @Override
    public void onApplicationEvent(ApplicationEvent event){
        LeahServiceManager leahServiceManager = LeahServiceManager.getManager();
        LeahReferManager leahReferManager = LeahReferManager.getManager();
        ConnManager connManager = ConnManager.getManager();

        if(event instanceof ContextRefreshedEvent){//容器刷新事件
            ContextRefreshedEvent refreshEvent = (ContextRefreshedEvent)event;
            if(refreshEvent.getApplicationContext().getParent() == null){//防止事件重复执行
                try {
                    if(leahServiceManager.getServiceSize() != 0){//存在服务上线
                        leahServiceManager.online();
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage(),e);
                }
            }
        }else if(event instanceof ContextClosedEvent || event instanceof ContextStoppedEvent){
            try {
                if(leahServiceManager.getServiceSize() != 0) {//存在服务上线
                    leahServiceManager.offline();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
        }
    }
}
