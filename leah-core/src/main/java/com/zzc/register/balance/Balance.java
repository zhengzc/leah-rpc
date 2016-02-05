package com.zzc.register.balance;

import com.zzc.channel.ChannelSubject;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ying on 16/1/25.
 * 负载均衡接口
 */
public abstract class Balance {
    /**
     * 缓存所有conn对应的channel信息
     */
    private static final Map<String, ChannelSubject> connChannelSubject = new ConcurrentHashMap<String, ChannelSubject>();

    public abstract ChannelSubject getChannel(List<ChannelSubject> channelSubjectList);
}
