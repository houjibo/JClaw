package com.jclaw.code.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebugInfo {
    private String filePath;
    private int lineNumber;
    private String context;
    private List<String> variables;
    private List<String> suggestions;
}
