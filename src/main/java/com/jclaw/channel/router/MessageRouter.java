package com.jclaw.channel.router;

import com.jclaw.channel.adapter.ChannelAdapter;
import com.jclaw.channel.adapter.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息路由器
 */
@Component
public class MessageRouter implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(MessageRouter.class);
    @Autowired
    private List<ChannelAdapter> channelAdapters;

    private final Map<String, ChannelAdapter> adapterMap = new ConcurrentHashMap<>();

    /**
     * 初始化通道
     */
    public void init() {
        for (ChannelAdapter adapter : channelAdapters) {
            adapterMap.put(adapter.getName(), adapter);
            adapter.receiveMessage(this);
            log.info("注册通道：{}", adapter.getName());
        }
    }

    /**
     * 发送消息到指定通道
     */
    public void sendToChannel(String channelName, String target, String content) {
        ChannelAdapter adapter = adapterMap.get(channelName);
        if (adapter == null) {
            log.error("通道不存在：{}", channelName);
            return;
        }
        if (!adapter.isConnected()) {
            log.error("通道未连接：{}", channelName);
            return;
        }
        adapter.sendMessage(target, content);
        log.info("消息已发送到 {}/{}", channelName, target);
    }

    /**
     * 广播消息到所有通道
     */
    public void broadcast(String content) {
        for (ChannelAdapter adapter : channelAdapters) {
            if (adapter.isConnected()) {
                adapter.sendMessage("all", content);
            }
        }
        log.info("消息已广播到所有通道");
    }

    @Override
    public void onMessage(String from, String content) {
        log.info("收到消息 from {}: {}", from, content);
        // TODO: 消息处理逻辑
    }

    /**
     * 获取所有已注册通道
     */
    public List<String> getChannels() {
        return adapterMap.keySet().stream().toList();
    }
}
