package com.jclaw.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CronServiceTest {
    
    @Autowired
    private CronService cronService;
    
    @Test
    void testRegisterTask() {
        cronService.register("test-task", () -> {}, "0 * * * * ?");
        assertTrue(cronService.listTasks().contains("test-task"));
    }
    
    @Test
    void testUnregisterTask() {
        cronService.register("temp-task", () -> {}, "0 * * * * ?");
        cronService.unregister("temp-task");
        assertFalse(cronService.listTasks().contains("temp-task"));
    }
}
