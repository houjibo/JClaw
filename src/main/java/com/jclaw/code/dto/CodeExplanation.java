package com.jclaw.code.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeExplanation {
    private String filePath;
    private String language;
    private String summary;
    private String detailedExplanation;
    private java.util.List<String> keyFunctions;
    private java.util.List<String> dependencies;
}
