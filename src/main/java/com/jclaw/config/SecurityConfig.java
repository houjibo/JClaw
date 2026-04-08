package com.jclaw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Web 安全配置
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 允许匿名访问认证 API
                .requestMatchers("/api/auth/**").permitAll()
                // 允许访问其他 API
                .requestMatchers("/api/**").permitAll()
                // 允许访问文档和静态资源
                .requestMatchers("/", "/index.html", "/assets/**").permitAll()
                .requestMatchers("/doc.html", "/webjars/**", "/v3/api-docs/**").permitAll()
                // 允许 H2 控制台
                .requestMatchers("/h2-console/**").permitAll()
                // 允许 Actuator
                .requestMatchers("/actuator/**").permitAll()
                // 允许 WebSocket
                .requestMatchers("/ws/**").permitAll()
                // 其他请求全部允许（开发环境）
                .anyRequest().permitAll()
            )
            .headers(headers -> headers.frameOptions(frame -> frame.disable())); // 允许 H2 控制台 iframe
        
        return http.build();
    }
}
