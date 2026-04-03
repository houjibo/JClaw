package com.jclaw.channel.controller;

import com.jclaw.common.entity.Result;
import com.jclaw.channel.router.MessageRouter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 通道管理 REST API 控制器
 */
@RestController
@RequestMapping("/api/channels")
@Slf4j
public class ChannelController {

    @Autowired
    private MessageRouter messageRouter;

    /**
     * 获取所有通道
     */
    @GetMapping
    public Result<List<String>> listChannels() {
        List<String> channels = messageRouter.getChannels();
        return Result.success(channels);
    }

    /**
     * 发送消息到指定通道
     */
    @PostMapping("/{channel}/send")
    public Result<Void> sendToChannel(
        @PathVariable String channel,
        @RequestBody Map<String, String> request
    ) {
        String target = request.get("target");
        String content = request.get("content");
        messageRouter.sendToChannel(channel, target, content);
        return Result.success();
    }

    /**
     * 广播消息
     */
    @PostMapping("/broadcast")
    public Result<Void> broadcast(@RequestBody Map<String, String> request) {
        String content = request.get("content");
        messageRouter.broadcast(content);
        return Result.success();
    }

    /**
     * 获取通道状态
     */
    @GetMapping("/{channel}/status")
    public Result<Map<String, Object>> getChannelStatus(@PathVariable String channel) {
        // TODO: 实现通道状态查询
        return Result.success(Map.of(
            "name", channel,
            "connected", true
        ));
    }
}
