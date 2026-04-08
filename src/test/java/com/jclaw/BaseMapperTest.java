package com.jclaw;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Mapper 层集成测试基类
 * 使用 H2 内存数据库，自动回滚事务
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@Transactional
public abstract class BaseMapperTest {

    @BeforeEach
    public void setUp() {
        // 每个测试前清理数据
        cleanupData();
    }

    /**
     * 清理测试数据
     * 子类可以重写此方法
     */
    public void cleanupData() {
        // 默认实现为空
    }
}
