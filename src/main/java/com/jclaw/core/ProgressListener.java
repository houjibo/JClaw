package com.jclaw.core;

/**
 * 进度监听器接口
 * 用于接收工具执行进度更新
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@FunctionalInterface
public interface ProgressListener {
    
    /**
     * 进度更新回调
     * 
     * @param progress 进度数据
     */
    void onProgressUpdate(ToolProgressData progress);
}
