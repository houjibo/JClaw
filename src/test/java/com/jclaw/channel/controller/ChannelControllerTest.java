package com.jclaw.channel.controller;

import com.jclaw.channel.router.MessageRouter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 通道控制器集成测试
 */
@WebMvcTest(ChannelController.class)
class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageRouter messageRouter;

    @Test
    void testListChannels() throws Exception {
        mockMvc.perform(get("/api/channels"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testSendToChannel() throws Exception {
        mockMvc.perform(post("/api/channels/feishu/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"target\":\"user1\",\"content\":\"test\"}"))
            .andExpect(status().isOk());
    }

    @Test
    void testBroadcast() throws Exception {
        mockMvc.perform(post("/api/channels/broadcast")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"broadcast test\"}"))
            .andExpect(status().isOk());
    }
}
