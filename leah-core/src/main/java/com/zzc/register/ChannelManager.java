package com.zzc.register;

import com.zzc.channel.ChannelSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by ying on 16/1/26.
 * 管理所有的channel
 */
public class ChannelManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static ChannelManager channelManager = new ChannelManager();
    private Map<String, ChannelSubject> connChannelSubject = new HashMap<String, ChannelSubject>();

    private ChannelManager() {
        Thread t = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5 * 1000);
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage(), e);
                    }

                    Iterator<String> it = connChannelSubject.keySet().iterator();
                    while (it.hasNext()) {
                        String conn = it.next();
                        if (!connChannelSubject.get(conn).isConnected()) {
                            connChannelSubject.remove(conn);
                        }
                    }
                }
            }
        };
        t.setDaemon(true);
        t.start();
    }

    public static ChannelManager getChannelManager() {
        return channelManager;
    }

    /**
     * 新初始化的conn 和 channel
     *
     * @param conn
     * @param channelSubject
     */
    public void addConnChannel(String conn, ChannelSubject channelSubject) {
        this.connChannelSubject.put(conn, channelSubject);
    }

    /**
     * 获取channel
     *
     * @param conns
     * @return
     */
    public List<ChannelSubject> getChannel(Set<String> conns) {
        List<ChannelSubject> channelSubjects = new ArrayList<ChannelSubject>();
        Iterator<String> it = conns.iterator();
        while (it.hasNext()) {
            String tmp = it.next();
            if (this.connChannelSubject.containsKey(tmp) && this.connChannelSubject.get(tmp) != null) {
                channelSubjects.add(this.connChannelSubject.get(tmp));
            }
        }
        return channelSubjects;
    }
}
