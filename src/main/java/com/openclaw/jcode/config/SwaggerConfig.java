package com.openclaw.jcode.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI 配置
 * 
 * 访问：http://localhost:8080/swagger-ui.html
 */
@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI jcodeOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("JClaw API")
                .description("JClaw - Java 编码智能体 REST API")
                .version("1.0.0")
                .contact(new Contact()
                    .name("JClaw Team")
                    .email("contact@jcode.ai")
                    .url("https://github.com/openclaw/jcode")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("本地开发环境")
            ));
    }
}
