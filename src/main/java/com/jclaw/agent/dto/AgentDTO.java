package com.jclaw.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentDTO {
    private String id;
    private String name;
    private String role;
    private String status; // idle, busy, stopped
    private String model;
    private LocalDateTime createdAt;
}
