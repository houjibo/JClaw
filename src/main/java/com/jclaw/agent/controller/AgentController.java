package com.jclaw.agent.controller;

import com.jclaw.agent.dto.AgentDTO;
import com.jclaw.agent.service.AgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/agents")
@RequiredArgsConstructor
public class AgentController {
    
    private final AgentService agentService;
    
    @PostMapping
    public ResponseEntity<AgentDTO> createAgent(@RequestBody AgentDTO agent) {
        return ResponseEntity.ok(agentService.createAgent(agent));
    }
    
    @GetMapping
    public ResponseEntity<List<AgentDTO>> listAgents() {
        return ResponseEntity.ok(agentService.listAgents());
    }
    
    @PostMapping("/{agentId}/message")
    public ResponseEntity<Void> sendMessage(
            @PathVariable String agentId,
            @RequestParam String content) {
        agentService.sendMessage(agentId, content);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{agentId}/stop")
    public ResponseEntity<Void> stopAgent(@PathVariable String agentId) {
        agentService.stopAgent(agentId);
        return ResponseEntity.ok().build();
    }
}
