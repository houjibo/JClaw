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
public class CodeDocumentation {
    private String filePath;
    private String overview;
    private List<ApiDoc> apis;
    private List<String> examples;
}
