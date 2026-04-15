package com.jclaw.monitoring.prometheus;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Prometheus 监控配置
 * 
 * 指标：
 * - JVM 内存/线程
 * - CPU 使用
 * - API 请求延迟
 * - 工具调用次数
 * - AI 响应时间
 * 
 * @author JClaw
 * @since 2026-04-15
 */
@Slf4j
@Configuration
public class PrometheusConfig {
    
    @Bean
    public MeterRegistry meterRegistry() {
        PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        
        // JVM 指标
        new JvmMemoryMetrics().bindTo(registry);
        new JvmThreadMetrics().bindTo(registry);
        new ProcessorMetrics().bindTo(registry);
        
        log.info("Prometheus 指标注册完成");
        return registry;
    }
}
