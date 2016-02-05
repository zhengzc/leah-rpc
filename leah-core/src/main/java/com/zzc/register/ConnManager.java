package com.zzc.register;

import com.alibaba.fastjson.JSONObject;
import com.zzc.channel.ChannelSubject;
import com.zzc.main.LeahClient;
import com.zzc.main.LeahReferManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ying on 15/7/1.
 * 链接管理器
 */
public class ConnManager {
    private static final Logger logger = LoggerFactory.getLogger(ConnManager.class);

    private final int SYNTIME = 10 * 1000;//多少秒同步一次
    private final int CHECK_CHANNEL_TIMES = 10 * 1000;//间隔多久check一次

    private static volatile ConnManager channelManager;
    private Register register = null;

    /**
     * 缓存url->[192.168.8.10:8825,192.168.8.11:8825]的映射
     */
//    private Map<String,Set<String>> cacheUrl2Conns = new ConcurrentHashMap<String, Set<String>>();
    /**
     * 缓存 192.168.8.10:8825->[com.zzc.userService_1.0,com.zzc.userService_2.0]的映射
     */
//    private Map<String,Set<String>> cacheConn2Urls = new ConcurrentHashMap<String, Set<String>>();

    /**
     * 缓存mina服务器地址和端口
     * 例如：192.168.8.10:8825->LeahClient
     */
    private Map<String, LeahClient> allClient = new ConcurrentHashMap<String, LeahClient>();

    /**
     * 缓存url->channel映射
     */
//    private Map<String,List<ChannelSubject>> cacheChannel = new ConcurrentHashMap<String, List<ChannelSubject>>();

    /**
     * 缓存url->conns映射
     */
    private Map<String, Set<String>> cacheUrlConns = new HashMap<String, Set<String>>();
    /**
     * 缓存conn->channel映射
     */
//    private Map<String,ChannelSubject>  cacheConnChannel = new ConcurrentHashMap<String, ChannelSubject>();

    /**
     * 缓存之前获取到的url ->channelSubjects信息
     */
    private Map<String, List<ChannelSubject>> cacheUrlChannels = new HashMap<String, List<ChannelSubject>>();
    /**
     * 当前客户端所调用的服务列表
     */
    private Set<String> urls;

    private ConnManager(Register register) {
        this.register = register;

        //获取所有服务列表
        LeahReferManager leahReferManager = LeahReferManager.getManager();
        urls = leahReferManager.getAllInvokerUrl();

        logger.debug("所需服务列表:{}", JSONObject.toJSONString(urls));

        //初始化
        /*for(String url : urls){
//            cacheChannel.put(url,new ArrayList<ChannelSubject>());
        }*/

        if (urls.size() == 0) {
            logger.debug("无需引入远程服务");
        } else {
            refreshService();

            //启动定时同步任务
            Thread thread = new Thread() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(SYNTIME);
                        } catch (InterruptedException e) {
                            logger.error(e.getMessage(), e);
                        }
                        refreshService();
                    }
                }
            };

            thread.setDaemon(true);
            thread.start();


            //启动定时refresh
            Thread thread1 = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(CHECK_CHANNEL_TIMES);
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage(), e);
                    }

                    refreshChannel();
                }
            };

            thread1.setDaemon(true);
            thread1.start();
        }
    }

    /**
     * 初始化
     *
     * @param register
     */
    public static void init(Register register) {
        logger.debug("channelManager is initing");
        if (channelManager == null) {
            synchronized (ConnManager.class) {
                if (channelManager == null) {
                    channelManager = new ConnManager(register);
                }
            }
        } else {
            logger.warn("ConnManager has already init,repeat call is invalid");
        }
    }

    /**
     * 获取manager
     *
     * @return
     */
    public static ConnManager getManager() {
        if (channelManager == null) {
            logger.error("channelManager is null,please call init() first");
            throw new NullPointerException("channelManager is null,please call init() first");
        }
        return channelManager;
    }

    /**
     * 移除链接
     * @param conn
     */
    /*public void removeConn(String conn){

        Set<String> urls = this.cacheConn2Urls.get(conn);
        for(String url : urls){

            //删除没用channel
            Iterator<ChannelSubject> it = this.cacheChannel.get(url).iterator();
            while(it.hasNext()){
                ChannelSubject channelSubject = it.next();
                if(!channelSubject.isConnected()){
                    it.remove();
                }
            }

            //删除cacheUrl2Conns中的无用链接
            Set<String> conns = this.cacheUrl2Conns.get(url);
            conns.remove(conn);
        }

        //删除掉cacheConn2Urls链接
        this.cacheConn2Urls.remove(conn);

        //释放掉服务
        LeahClient leahClient = this.allClient.remove(conn);
        leahClient.stop();
    }*/

    /**
     * 根据url信息获取channel列表
     *
     * @param url
     * @return
     */
    public List<ChannelSubject> getChannelSubjects(String url) {
        long s = System.currentTimeMillis();

        List<ChannelSubject> channelSubjects;
        //先去缓存取
        if (this.cacheUrlChannels.containsKey(url) && this.cacheUrlChannels.get(url).size() > 0) {
            channelSubjects = this.cacheUrlChannels.get(url);
        } else {
            Set<String> conns = this.cacheUrlConns.get(url);
            channelSubjects = ChannelManager.getChannelManager().getChannel(conns);

            this.cacheUrlChannels.put(url, channelSubjects);
        }

//        logger.debug("channel列表，url:{},channels:{}",url,JSONObject.toJSONString(channelSubjects));
        logger.debug("getChannelSubjects 耗时:{}", System.currentTimeMillis() - s);
        return channelSubjects;
    }


    /**
     * 直接检查所有channel，刷新cacheUrlChannel缓存
     */
    private void refreshChannel() {
        Iterator<String> it = this.cacheUrlChannels.keySet().iterator();
        while (it.hasNext()) {
            String conn = it.next();
            this.cacheUrlChannels.put(conn, ChannelManager.getChannelManager().getChannel(this.cacheUrlConns.get(conn)));
        }
    }

    /**
     * 更新服务列表
     */
    private void refreshService() {
        logger.debug("定时同步任务开始");
//        RegisterServiceBean serviceBean = register.syn(urls);//获取新的服务列表信息

        /**
         * 遍历所有服务，我们这里采用每次拉下的新的服务列表都增量更新到上次的服务列表中
         * 删除服务由客户端client探测链接的存活来动态删除服务
         * 这样的好处是当注册中心出现问题的时候，也不会影响到正常服务
         */
        //检测是否有新增服务
        Set<String> newConns = this.register.getAllConn(urls);
        Set<String> oldConns = allClient.keySet();
        if (!oldConns.containsAll(newConns)) {//存在新服务
            newConns.removeAll(oldConns);//获取新服务
            logger.debug("检测到新增加服务:{}", JSONObject.toJSONString(newConns));
            for (String conn : newConns) {//遍历增加新的服务链接
                String[] strs = conn.split(":");
                String host = strs[0];
                int port = Integer.valueOf(strs[1]);
                LeahClient leahClient = new LeahClient(host, port);
                leahClient.start();

                allClient.put(conn, leahClient);
            }
        }

        //更新url->conns映射
        for (String url : urls) {
            Set<String> conns = this.register.getConns(url);
            this.cacheUrlConns.put(url, conns);

            if (!this.cacheUrlChannels.containsKey(url)) {
                List<ChannelSubject> channelSubjects = ChannelManager.getChannelManager().getChannel(conns);
                this.cacheUrlChannels.put(url, channelSubjects);
            }
        }

        //更新conn->urls映射
        /*for(Map.Entry<String,Set<String>> entry : serviceBean.getConnUrls().entrySet()){
            if(cacheConn2Urls.containsKey(entry.getKey())){//已经存在
                //更新列表
                cacheConn2Urls.get(entry.getKey()).addAll(entry.getValue());
            }else{//不存在添加进去
                cacheConn2Urls.put(entry.getKey(),entry.getValue());
            }
        }*/

        //更新url->conns映射 更新cacheChannel
                    /*for(Map.Entry<String,Set<String>> entry : serviceBean.getUrlConns().entrySet()){
                        String url = entry.getKey();
                        Set<String> conns = entry.getValue();

                        if(cacheUrl2Conns.containsKey(url)){
                            Set<String> newUrlConns = conns;
                            Set<String> oldUrlConns = cacheUrl2Conns.get(url);
                            if(oldConns.containsAll(newUrlConns)){//没有新增服务
                                //do nothing
                            }else{
                                //更新cacheChannel
                                newUrlConns.removeAll(oldConns);//获取新增conn
                                List<ChannelSubject> channelSubjects = cacheChannel.get(url);
                                for(String conn : newUrlConns){
                                    channelSubjects.add(allClient.get(conn).getChannelSubject());
                                }

                                //更新url->conns映射
                                oldUrlConns.addAll(newUrlConns);

                                        给观察者发送变更通知

                            }
                        }else{
                            List<ChannelSubject> channelSubjects = cacheChannel.get(url);
                            for(String conn : conns){
                                channelSubjects.add(allClient.get(conn).getChannelSubject());
                            }

                            cacheUrl2Conns.put(url,conns);
                            给观察者发送变更通知
                        }
                    }*/


        logger.debug("定时同步任务结束");
    }
}
