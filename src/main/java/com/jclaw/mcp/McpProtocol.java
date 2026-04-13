package com.jclaw.mcp;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * MCP 协议基础类
 */
public class McpProtocol {
    
    /**
     * MCP 请求
     */
    @Data
    @NoArgsConstructor
    public static class Request {
        private String jsonrpc = "2.0";
        private String id;
        private String method;
        private Map<String, Object> params;
    }
    
    /**
     * MCP 响应
     */
    @Data
    @NoArgsConstructor
    public static class Response {
        private String jsonrpc = "2.0";
        private String id;
        private Map<String, Object> result;
        private Error error;
        
        @Data
        public static class Error {
            private Integer code;
            private String message;
        }
    }
    
    /**
     * MCP 资源
     */
    @Data
    public static class Resource {
        private String uri;
        private String name;
        private String description;
        private String mimeType;
    }
    
    /**
     * MCP 工具
     */
    @Data
    public static class Tool {
        private String name;
        private String description;
        private InputSchema inputSchema;
        
        @Data
        public static class InputSchema {
            private String type = "object";
            private Map<String, Property> properties;
            private List<String> required;
            
            @Data
            public static class Property {
                private String type;
                private String description;
            }
        }
    }
    
    /**
     * MCP 提示词
     */
    @Data
    public static class Prompt {
        private String name;
        private String description;
        private List<Argument> arguments;
        
        @Data
        public static class Argument {
            private String name;
            private String description;
            private Boolean required;
        }
    }
}
