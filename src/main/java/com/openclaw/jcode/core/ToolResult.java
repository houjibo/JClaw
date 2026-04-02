package com.openclaw.jcode.core;

/**
 * 工具执行结果
 */
public class ToolResult {
    
    private boolean success;
    private String message;
    private String output;
    private String error;
    private Object data;
    private long durationMs;
    
    private ToolResult() {}
    
    public static ToolResult success(String message) {
        ToolResult r = new ToolResult();
        r.success = true;
        r.message = message;
        return r;
    }
    
    public static ToolResult success(String message, String output) {
        ToolResult r = new ToolResult();
        r.success = true;
        r.message = message;
        r.output = output;
        return r;
    }
    
    public static ToolResult error(String error) {
        ToolResult r = new ToolResult();
        r.success = false;
        r.error = error;
        return r;
    }
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getOutput() { return output; }
    public void setOutput(String output) { this.output = output; }
    
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
    
    public long getDurationMs() { return durationMs; }
    public void setDurationMs(long durationMs) { this.durationMs = durationMs; }
}
