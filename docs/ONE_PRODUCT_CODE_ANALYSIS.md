# One Product 代码分析报告

> **任务**: 任务 4 - One Product 代码分析  
> **日期**: 2026-04-03  
> **作者**: 可乐 🥤  
> **分析范围**: 意图驱动 + 代码追溯 + 影响分析

---

## 📊 项目概况

### 基本信息

| 指标 | 数值 |
|------|------|
| **总代码量** | ~21,000 行（Phase 6-15） |
| **Java 文件** | 100+ 个 |
| **数据库表** | 15+ 张 |
| **API 接口** | 91 个 |
| **测试用例** | 109 个（100% 通过） |

### 模块结构

```
one-product-java/
├── one-product-common/        # 公共模块
│   └── entity/                # 基础实体
├── one-product-service/       # 服务模块
│   ├── trace/                 # 追溯服务 ✅
│   │   ├── entity/            # 实体类
│   │   ├── mapper/            # Mapper 接口
│   │   └── service/           # 服务层
│   ├── ai/                    # AI 服务
│   └── collaboration/         # 协作服务
├── one-product-api/           # API 模块
└── one-product-web/           # Web 启动模块
```

---

## 🎯 意图驱动模块分析

### 1. 数据模型

#### Intent 实体

```java
@Data
@TableName("intent")
public class Intent extends BaseEntity {
    private Long projectId;      // 项目 ID
    private String type;         // 意图类型
    private String name;         // 意图名称
    private String description;  // 意图描述
    private String context;      // 上下文数据 (JSON)
    private String status;       // 状态
    private Integer priority;    // 优先级
}
```

**数据库表**:
```sql
CREATE TABLE intent (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT,
    type VARCHAR(50),
    name VARCHAR(200),
    description TEXT,
    context JSON,
    status VARCHAR(50),
    priority INT,
    created_at DATETIME,
    updated_at DATETIME
);
```

**分析**:
- ✅ 结构简单清晰
- ✅ JSON 字段存储上下文（PostgreSQL 可用 JSONB 优化）
- ✅ 支持优先级和状态管理
- ❌ 缺少意图图谱关系表（需要补充）

---

### 2. 意图识别流程

**One Product 实现**:
```
用户输入 → AI 分析 → 意图提取 → 意图分类 → 意图存储
```

**关键代码**（AI 服务）:
```java
@Service
public class AiService {
    
    public Intent recognizeIntent(String userInput) {
        // 1. 调用大模型分析
        String analysis = callLLM(userInput);
        
        // 2. 提取意图
        Intent intent = parseIntent(analysis);
        
        // 3. 保存意图
        intentMapper.insert(intent);
        
        return intent;
    }
}
```

**JClaw 融合方案**:
```java
// 扩展现有 IntentRecognitionTool
@Component
public class IntentRecognitionTool extends BaseTool {
    
    @Override
    public ToolResult execute(ToolContext context) {
        String userInput = context.getParam("input");
        
        // 1. AI 意图识别
        Intent intent = intentService.recognizeIntent(userInput);
        
        // 2. 写入工作记忆（Redis）
        cacheUtils.setWorkingMemory(context.getSessionId(), 
            WorkingMemory.builder()
                .currentIntent(intent)
                .build()
        );
        
        return ToolResult.success(Map.of("intent", intent));
    }
}
```

---

## 🔗 代码追溯模块分析

### 1. 数据模型

#### TraceNode 实体

```java
@Data
@TableName("trace_node")
public class TraceNode extends BaseEntity {
    private String path;         // 文件路径
    private String type;         // 节点类型（REQUIREMENT/CODE/TEST）
    private String name;         // 节点名称
    private String signature;    // 签名（方法签名）
    private Map<String, Object> metrics; // 指标（圈复杂度等）
}
```

#### TraceRelation 实体

```java
@Data
public class TraceRelation {
    private String id;
    private String fromId;       // 源节点 ID
    private String toId;         // 目标节点 ID
    private TraceRelationType type; // 关系类型
    private double confidence;   // 置信度
    private Instant createdAt;
}
```

**关系类型枚举**:
```java
public enum TraceRelationType {
    REALIZES,      // 实现
    DEPENDS,       // 依赖
    REFERENCES,    // 引用
    CALLS,         // 调用
    TESTS,         // 测试
    DOCUMENTS      // 文档
}
```

---

### 2. 追溯服务

**核心接口**:
```java
public interface TraceService {
    
    // 创建追溯节点
    TraceNode createNode(TraceNode node);
    
    // 创建追溯关系
    TraceRelation createRelation(TraceRelation relation);
    
    // 查询实现代码
    List<TraceNode> findImplementations(String requirementId);
    
    // 查询追溯需求
    List<TraceNode> findRequirements(String codePath);
    
    // 影响分析
    ImpactAnalysis analyzeImpact(String nodeId);
}
```

**实现代码**:
```java
@Service
public class TraceServiceImpl implements TraceService {
    
    @Autowired
    private TraceMapper traceMapper;
    
    @Override
    public ImpactAnalysis analyzeImpact(String nodeId) {
        // 1. 查询节点
        TraceNode node = traceMapper.selectById(nodeId);
        
        // 2. 递归查询调用链
        List<TraceNode> affected = findAffectedNodes(nodeId);
        
        // 3. 查询关系
        List<TraceRelation> relations = traceMapper.selectRelations(nodeId);
        
        // 4. 构建分析结果
        return ImpactAnalysis.builder()
            .requirementId(nodeId)
            .affectedNodes(affected)
            .relations(relations)
            .statistics(Map.of(
                "totalNodes", affected.size(),
                "riskLevel", calculateRisk(affected)
            ))
            .build();
    }
}
```

---

### 3. AST 解析器

**功能**:
- 解析 Java/TS 代码
- 提取类/方法/函数
- 计算圈复杂度
- 生成代码单元

**JClaw 融合方案**:
```java
@Component
public class JavaAstParser {
    
    /**
     * 解析 Java 文件
     */
    public CodeUnit parseFile(String filePath) {
        // 使用 JavaParser 库
        CompilationUnit cu = JavaParser.parse(new File(filePath));
        
        // 提取类
        List<ClassOrInterfaceDeclaration> classes = 
            cu.findAll(ClassOrInterfaceDeclaration.class);
        
        // 提取方法
        List<MethodDeclaration> methods = 
            cu.findAll(MethodDeclaration.class);
        
        // 计算复杂度
        for (MethodDeclaration method : methods) {
            int complexity = calculateCyclomaticComplexity(method);
            // ...
        }
        
        return buildCodeUnit(cu, classes, methods);
    }
}
```

---

## 📊 影响分析模块分析

### 1. 影响分析流程

```
代码变更 → 检测变更 → 查询调用链 → 分析影响范围 → 风险评分 → 可视化
```

### 2. 影响分析结果

```java
@Data
public class ImpactAnalysis {
    private String requirementId;          // 需求 ID
    private List<TraceNode> affectedNodes; // 受影响节点
    private List<TraceRelation> relations; // 关系列表
    private Map<String, Object> statistics; // 统计信息
}
```

**统计信息**:
```json
{
  "totalNodes": 15,
  "riskLevel": "HIGH",
  "affectedFiles": 8,
  "affectedMethods": 12,
  "testCasesNeeded": 5
}
```

### 3. 风险评分算法

```java
@Service
public class RiskScorer {
    
    public double calculateRisk(List<TraceNode> affectedNodes) {
        double score = 0;
        
        for (TraceNode node : affectedNodes) {
            // 核心文件权重高
            if (isCoreFile(node.getPath())) {
                score += 2.0;
            }
            
            // 复杂度高的方法权重高
            if (node.getMetrics() != null) {
                int complexity = (int) node.getMetrics().get("complexity");
                score += complexity * 0.5;
            }
        }
        
        // 归一化到 0-100
        return Math.min(100, score);
    }
}
```

---

## 🔧 可复用代码清单

### 完全可复用（直接复制）

| 文件 | 说明 | 适配工作量 |
|------|------|------------|
| `TraceNode.java` | 追溯节点实体 | 低（改包名） |
| `TraceRelation.java` | 追溯关系实体 | 低 |
| `TraceRelationType.java` | 关系类型枚举 | 无 |
| `Intent.java` | 意图实体 | 低 |
| `ImpactAnalysis.java` | 影响分析结果 | 无 |

### 需要适配（修改后使用）

| 文件 | 说明 | 适配工作量 |
|------|------|------------|
| `TraceServiceImpl.java` | 追溯服务实现 | 中（改数据库） |
| `JavaAstParser.java` | AST 解析器 | 中（集成 JClaw） |
| `RiskScorer.java` | 风险评分器 | 低 |
| `TraceMapper.java` | Mapper 接口 | 中（改 MyBatis-Plus） |

### 需要重写（参考设计）

| 模块 | 说明 | 工作量 |
|------|------|--------|
| 意图识别 | 集成 JClaw 大模型 | 高 |
| 调用链可视化 | Three.js 3D 图谱 | 高 |
| 影响分析 API | REST API | 中 |

---

## 📋 JClaw 融合方案

### 1. 数据库 Schema 适配

**PostgreSQL 版本**:
```sql
-- 意图表
CREATE TABLE intent (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT,
    type VARCHAR(50),
    name VARCHAR(200),
    description TEXT,
    context JSONB,  -- JSONB 优化
    status VARCHAR(50),
    priority INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 追溯节点表
CREATE TABLE trace_nodes (
    id BIGSERIAL PRIMARY KEY,
    path VARCHAR(500),
    type VARCHAR(50),
    name VARCHAR(200),
    signature TEXT,
    metrics JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 追溯关系表
CREATE TABLE trace_relations (
    id BIGSERIAL PRIMARY KEY,
    from_id BIGINT REFERENCES trace_nodes(id),
    to_id BIGINT REFERENCES trace_nodes(id),
    type VARCHAR(50),
    confidence DOUBLE PRECISION,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 递归查询索引
CREATE INDEX idx_relations_from ON trace_relations(from_id);
CREATE INDEX idx_relations_to ON trace_relations(to_id);
```

### 2. 服务层集成

```java
@Service
public class JclawTraceService {
    
    @Autowired
    private TraceMapper traceMapper;
    
    @Autowired
    private JavaAstParser astParser;
    
    @Autowired
    private CacheUtils cacheUtils;
    
    /**
     * 分析代码变更影响
     */
    public ImpactAnalysis analyzeChange(String filePath) {
        // 1. AST 解析
        CodeUnit codeUnit = astParser.parseFile(filePath);
        
        // 2. 保存/更新代码单元
        traceMapper.upsertCodeUnit(codeUnit);
        
        // 3. 查询调用链
        List<TraceNode> callers = traceMapper.selectCallers(codeUnit.getId());
        List<TraceNode> callees = traceMapper.selectCallees(codeUnit.getId());
        
        // 4. 风险评分
        double risk = riskScorer.calculateRisk(callers);
        
        // 5. 缓存结果
        cacheUtils.setApiCache("impact_analysis", filePath, 
            Map.of("risk", risk, "affected", callers.size())
        );
        
        return buildImpactAnalysis(codeUnit, callers, callees, risk);
    }
}
```

### 3. REST API 设计

```java
@RestController
@RequestMapping("/api/trace")
public class TraceController {
    
    @Autowired
    private JclawTraceService traceService;
    
    /**
     * 分析影响
     */
    @PostMapping("/impact/{id}")
    public Result<ImpactAnalysis> analyzeImpact(@PathVariable String id) {
        ImpactAnalysis analysis = traceService.analyzeImpact(id);
        return Result.success(analysis);
    }
    
    /**
     * 查询调用链
     */
    @GetMapping("/callchain/{id}")
    public Result<List<TraceNode>> getCallChain(@PathVariable String id) {
        List<TraceNode> chain = traceService.getCallChain(id);
        return Result.success(chain);
    }
    
    /**
     * 创建追溯关系
     */
    @PostMapping("/relation")
    public Result<TraceRelation> createRelation(@RequestBody TraceRelation relation) {
        TraceRelation created = traceService.createRelation(relation);
        return Result.success(created);
    }
}
```

---

## ✅ 验收标准

### 代码分析完成标志

- [ ] One Product 核心代码分析完成
- [ ] 可复用代码清单明确
- [ ] 融合方案设计完成
- [ ] PostgreSQL Schema 设计完成
- [ ] REST API 设计完成
- [ ] 工作量评估准确

### 可交付成果

- [x] ✅ 代码分析报告（本文档）
- [ ] ⏳ PostgreSQL Schema 脚本
- [ ] ⏳ JClaw 融合实现代码
- [ ] ⏳ 单元测试用例

---

## 📚 参考资料

- [One Product GitHub](https://github.com/houjibo/one-product-java)
- [MyBatis-Plus 官方文档](https://baomidou.com/)
- [JavaParser 官方文档](https://javaparser.org/)

---

*报告完成时间：2026-04-03*  
*作者：可乐 🥤*  
*状态：✅ 完成*
