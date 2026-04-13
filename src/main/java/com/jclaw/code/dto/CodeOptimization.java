package com.jclaw.code.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeOptimization {
    private String type;
    private String description;
    private String suggestion;
    private String beforeCode;
    private String afterCode;
    private int priority;
}
