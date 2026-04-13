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
public class ApiDoc {
    private String name;
    private String description;
    private List<String> parameters;
    private String returnType;
}
