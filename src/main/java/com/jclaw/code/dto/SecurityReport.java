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
public class SecurityReport {
    private String filePath;
    private int riskScore;
    private List<Vulnerability> vulnerabilities;
    private List<String> recommendations;
}
