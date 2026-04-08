package com.jclaw.trace.service.impl;

import com.jclaw.trace.entity.CodeUnit;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AST 解析服务测试（JavaParser 集成）
 */
@SpringBootTest
@DisplayName("AST 解析服务测试")
class AstParserServiceTest {

    @Autowired
    private AstParserServiceImpl astParserService;

    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("ast-test");
        System.out.println("创建临时目录：" + tempDir);
    }

    @AfterEach
    void tearDown() throws IOException {
        if (tempDir != null && Files.exists(tempDir)) {
            // 递归删除临时目录
            Files.walk(tempDir)
                .sorted(java.util.Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        // 忽略删除失败
                    }
                });
        }
    }

    @Test
    @DisplayName("测试解析简单 Java 类")
    void testParseSimpleClass() throws IOException {
        // Arrange
        String javaCode = """
            package com.example;
            
            public class SimpleClass {
                private String name;
                
                public String getName() {
                    return name;
                }
                
                public void setName(String name) {
                    this.name = name;
                }
            }
            """;
        
        File javaFile = createTempJavaFile("SimpleClass.java", javaCode);

        // Act
        CodeUnit result = astParserService.parseJavaFile(javaFile.getAbsolutePath());

        // Assert
        assertNotNull(result);
        assertEquals("SimpleClass", result.getUnitName());
        assertEquals("CLASS", result.getUnitType());
        assertNotNull(result.getSignature());
    }

    @Test
    @DisplayName("测试解析包含方法的类")
    void testParseClassWithMethods() throws IOException {
        // Arrange
        String javaCode = """
            package com.example;
            
            import java.util.List;
            import java.util.ArrayList;
            
            public class UserService {
                private List<String> users = new ArrayList<>();
                
                public void addUser(String user) {
                    if (user != null && !user.isEmpty()) {
                        users.add(user);
                    }
                }
                
                public List<String> getUsers() {
                    return users;
                }
                
                public int getUserCount() {
                    return users.size();
                }
            }
            """;
        
        File javaFile = createTempJavaFile("UserService.java", javaCode);

        // Act
        CodeUnit result = astParserService.parseJavaFile(javaFile.getAbsolutePath());

        // Assert
        assertNotNull(result);
        assertEquals("UserService", result.getUnitName());
        assertNotNull(result.getMetrics());
        assertTrue((Integer) result.getMetrics().get("methodCount") >= 3);
    }

    @Test
    @DisplayName("测试解析复杂类（控制流语句）")
    void testParseComplexClass() throws IOException {
        // Arrange
        String javaCode = """
            package com.example;
            
            public class ComplexClass {
                public int calculate(int a, int b) {
                    if (a > 0) {
                        for (int i = 0; i < b; i++) {
                            if (i % 2 == 0) {
                                a += i;
                            } else {
                                a -= i;
                            }
                        }
                    } else {
                        while (b > 0) {
                            switch (b % 3) {
                                case 0:
                                    a++;
                                    break;
                                case 1:
                                    a--;
                                    break;
                                default:
                                    break;
                            }
                            b--;
                        }
                    }
                    return a;
                }
            }
            """;
        
        File javaFile = createTempJavaFile("ComplexClass.java", javaCode);

        // Act
        CodeUnit result = astParserService.parseJavaFile(javaFile.getAbsolutePath());

        // Assert
        assertNotNull(result);
        assertNotNull(result.getMetrics());
        assertTrue((Integer) result.getMetrics().get("complexity") > 1);
    }

    @Test
    @DisplayName("测试批量解析目录")
    void testParseDirectory() throws IOException {
        // Arrange
        createTempJavaFile("Class1.java", "public class Class1 {}");
        createTempJavaFile("Class2.java", "public class Class2 {}");
        createTempJavaFile("Class3.java", "public class Class3 {}");

        // Act
        int count = astParserService.parseDirectory(tempDir.toAbsolutePath().toString());

        // Assert
        assertEquals(3, count);
    }

    @Test
    @DisplayName("测试解析不存在的文件")
    void testParseNonExistentFile() {
        // Act
        CodeUnit result = astParserService.parseJavaFile("/non/existent/File.java");

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("测试解析无效 Java 文件")
    void testParseInvalidJavaFile() throws IOException {
        // Arrange
        String invalidCode = "this is not valid java code";
        File javaFile = createTempJavaFile("Invalid.java", invalidCode);

        // Act
        CodeUnit result = astParserService.parseJavaFile(javaFile.getAbsolutePath());

        // Assert
        assertNull(result);  // 解析失败应返回 null
    }

    /**
     * 辅助方法：创建临时 Java 文件
     */
    private File createTempJavaFile(String fileName, String content) throws IOException {
        if (tempDir == null) {
            throw new IllegalStateException("tempDir not initialized");
        }
        File file = new File(tempDir.toFile(), fileName);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
            writer.flush();
        }
        System.out.println("创建临时文件：" + file.getAbsolutePath() + " (" + file.length() + " bytes)");
        return file;
    }
}
