package com.jclaw;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

/**
 * 集成测试配置（H2 内存数据库版本）
 * 当 Docker 不可用时，使用 H2 内存数据库代替 PostgreSQL
 */
@TestConfiguration
public class IntegrationTestConfiguration {

    /**
     * 创建 H2 内存数据源
     */
    @Bean
    @Primary
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("classpath:schema.sql")  // 可选：初始化 schema
            .build();
    }
}
