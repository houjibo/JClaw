package com.jclaw.trace.mapper;

import com.jclaw.BaseMapperTest;
import com.jclaw.trace.entity.CodeUnit;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CodeUnitMapper 集成测试
 * 测试代码单元数据访问层的 CRUD 操作
 */
@DisplayName("CodeUnitMapper 集成测试")
class CodeUnitMapperTest extends BaseMapperTest {

    @Autowired
    private CodeUnitMapper codeUnitMapper;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        // 清理 code_unit 表
        codeUnitMapper.delete(null);
    }

    @Test
    @DisplayName("测试插入代码单元")
    void testInsert() {
        // Arrange
        CodeUnit codeUnit = createCodeUnit("code_001", "TestClass", "/src/Test.java", "CLASS");

        // Act
        int result = codeUnitMapper.insert(codeUnit);

        // Assert
        assertEquals(1, result);
        assertNotNull(codeUnit.getId());
    }

    @Test
    @DisplayName("测试根据 ID 查询代码单元")
    void testSelectById() {
        // Arrange
        CodeUnit codeUnit = createCodeUnit("code_001", "TestClass", "/src/Test.java", "CLASS");
        codeUnitMapper.insert(codeUnit);

        // Act
        CodeUnit result = codeUnitMapper.selectById("code_001");

        // Assert
        assertNotNull(result);
        assertEquals("TestClass", result.getUnitName());
        assertEquals("/src/Test.java", result.getFilePath());
        assertEquals("CLASS", result.getUnitType());
    }

    @Test
    @DisplayName("测试根据文件路径查询代码单元")
    void testSelectByFilePath() {
        // Arrange
        CodeUnit codeUnit = createCodeUnit("code_001", "TestClass", "/src/Test.java", "CLASS");
        codeUnitMapper.insert(codeUnit);

        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CodeUnit> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.eq("file_path", "/src/Test.java");

        // Act
        CodeUnit result = codeUnitMapper.selectOne(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals("/src/Test.java", result.getFilePath());
    }

    @Test
    @DisplayName("测试更新代码单元")
    void testUpdate() {
        // Arrange
        CodeUnit codeUnit = createCodeUnit("code_001", "TestClass", "/src/Test.java", "CLASS");
        codeUnitMapper.insert(codeUnit);

        // Act
        codeUnit.setUnitName("UpdatedClass");
        codeUnit.setSignature("public class UpdatedClass {}");
        int result = codeUnitMapper.updateById(codeUnit);

        // Assert
        assertEquals(1, result);
        
        CodeUnit updated = codeUnitMapper.selectById("code_001");
        assertNotNull(updated);
        assertEquals("UpdatedClass", updated.getUnitName());
        assertEquals("public class UpdatedClass {}", updated.getSignature());
    }

    @Test
    @DisplayName("测试删除代码单元")
    void testDelete() {
        // Arrange
        CodeUnit codeUnit = createCodeUnit("code_001", "TestClass", "/src/Test.java", "CLASS");
        codeUnitMapper.insert(codeUnit);

        // Act
        int result = codeUnitMapper.deleteById("code_001");

        // Assert
        assertEquals(1, result);
        
        CodeUnit deleted = codeUnitMapper.selectById("code_001");
        assertNull(deleted);
    }

    @Test
    @DisplayName("测试查询代码单元列表")
    void testSelectList() {
        // Arrange
        codeUnitMapper.insert(createCodeUnit("code_001", "Class1", "/src/Class1.java", "CLASS"));
        codeUnitMapper.insert(createCodeUnit("code_002", "Class2", "/src/Class2.java", "CLASS"));
        codeUnitMapper.insert(createCodeUnit("code_003", "method1", "/src/Class1.java", "METHOD"));

        // Act
        List<CodeUnit> codeUnits = codeUnitMapper.selectList(null);

        // Assert
        assertEquals(3, codeUnits.size());
    }

    @Test
    @DisplayName("测试按类型查询代码单元")
    void testSelectByType() {
        // Arrange
        codeUnitMapper.insert(createCodeUnit("code_001", "Class1", "/src/Class1.java", "CLASS"));
        codeUnitMapper.insert(createCodeUnit("code_002", "Class2", "/src/Class2.java", "CLASS"));
        codeUnitMapper.insert(createCodeUnit("code_003", "method1", "/src/Class1.java", "METHOD"));
        codeUnitMapper.insert(createCodeUnit("code_004", "method2", "/src/Class2.java", "METHOD"));

        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CodeUnit> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.eq("unit_type", "METHOD");

        // Act
        List<CodeUnit> codeUnits = codeUnitMapper.selectList(wrapper);

        // Assert
        assertEquals(2, codeUnits.size());
        assertTrue(codeUnits.stream().allMatch(c -> "METHOD".equals(c.getUnitType())));
    }

    @Test
    @DisplayName("测试按文件路径分组查询")
    void testSelectByFilePathGroup() {
        // Arrange
        codeUnitMapper.insert(createCodeUnit("code_001", "Class1", "/src/Class1.java", "CLASS"));
        codeUnitMapper.insert(createCodeUnit("code_002", "method1", "/src/Class1.java", "METHOD"));
        codeUnitMapper.insert(createCodeUnit("code_003", "method2", "/src/Class1.java", "METHOD"));
        codeUnitMapper.insert(createCodeUnit("code_004", "Class2", "/src/Class2.java", "CLASS"));

        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CodeUnit> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.eq("file_path", "/src/Class1.java");

        // Act
        List<CodeUnit> codeUnits = codeUnitMapper.selectList(wrapper);

        // Assert
        assertEquals(3, codeUnits.size());
        assertTrue(codeUnits.stream().allMatch(c -> "/src/Class1.java".equals(c.getFilePath())));
    }

    @Test
    @DisplayName("测试模糊查询代码单元名称")
    void testSelectLike() {
        // Arrange
        codeUnitMapper.insert(createCodeUnit("code_001", "UserService", "/src/UserService.java", "CLASS"));
        codeUnitMapper.insert(createCodeUnit("code_002", "UserServiceImpl", "/src/UserServiceImpl.java", "CLASS"));
        codeUnitMapper.insert(createCodeUnit("code_003", "OrderService", "/src/OrderService.java", "CLASS"));

        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CodeUnit> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.like("unit_name", "User");

        // Act
        List<CodeUnit> codeUnits = codeUnitMapper.selectList(wrapper);

        // Assert
        assertEquals(2, codeUnits.size());
        assertTrue(codeUnits.stream().allMatch(c -> c.getUnitName().contains("User")));
    }

    @Test
    @DisplayName("测试查询不存在的代码单元")
    void testSelectNonExistent() {
        // Act
        CodeUnit result = codeUnitMapper.selectById("non_existent");

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("测试限制结果数量")
    void testSelectWithLimit() {
        // Arrange
        for (int i = 1; i <= 25; i++) {
            codeUnitMapper.insert(createCodeUnit("code_" + String.format("%03d", i), 
                "Class" + i, "/src/Class" + i + ".java", "CLASS"));
        }

        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CodeUnit> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.last("LIMIT 10");

        // Act
        List<CodeUnit> codeUnits = codeUnitMapper.selectList(wrapper);

        // Assert
        assertEquals(10, codeUnits.size());
    }

    /**
     * 辅助方法：创建测试代码单元
     */
    private CodeUnit createCodeUnit(String id, String unitName, String filePath, String unitType) {
        return CodeUnit.builder()
            .id(id)
            .unitName(unitName)
            .filePath(filePath)
            .unitType(unitType)
            .signature("public class " + unitName + " {}")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
    }
}
