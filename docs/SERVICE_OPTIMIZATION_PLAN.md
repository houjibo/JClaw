# JClaw Service 层深度优化计划

**分析时间**: 2026-04-06 11:57  
**分析者**: 可乐 🥤

---

## 📊 待优化项清单

### 1️⃣ AI 任务分解集成（高优先级）

**问题**: `TaskDecompositionServiceImpl` 使用硬编码示例，未调用 AI

**现状**:
- `TaskDecompositionService.decompose()` - 返回固定 3 个任务
- `AiTaskDecompositionService` 已实现但未集成

**改进方案**:
- ✅ 在 `TaskDecompositionServiceImpl` 中注入 `AiTaskDecompositionService`
- ✅ 优先使用 AI 分解，降级到规则分解
- ✅ 补充测试用例

---

### 2️⃣ AI 意图识别集成（高优先级）

**问题**: `IntentRecognitionServiceImpl.recognize()` 使用示例逻辑

**现状**:
- `IntentRecognitionServiceImpl` - TODO 占位代码
- `AiIntentRecognitionService` 已实现但未集成

**改进方案**:
- ✅ 在 `IntentRecognitionServiceImpl` 中注入 `AiIntentRecognitionService`
- ✅ 优先使用 AI 识别，降级到规则识别
- ✅ 补充测试用例

---

### 3️⃣ 知识萃取服务完善（中优先级）

**问题**: `KnowledgeServiceImpl` 多个 TODO 未实现

**待实现**:
- ❌ AI 知识萃取（`extractKnowledgeWithAI`）
- ❌ 从日志萃取知识（`extractFromDailyLog`）
- ❌ PostgreSQL 全文搜索
- ❌ 分页查询

**改进方案**:
- ✅ 集成 AI 知识萃取
- ✅ 实现从 Memory 萃取知识
- ✅ 实现全文搜索（参考 MemoryService）
- ✅ 实现分页查询

---

### 4️⃣ 梦境时间任务实现（中优先级）

**问题**: `CronServiceImpl.startDreamTimeTask()` TODO 未实现

**待实现**:
- ❌ 记忆整理逻辑
- ❌ 知识萃取流程
- ❌ 生成改进建议

**改进方案**:
- ✅ 调用 MemoryService 整理记忆
- ✅ 调用 KnowledgeService 萃取知识
- ✅ 生成梦境报告

---

### 5️⃣ Subagent 实际启动（中优先级）

**问题**: `SubagentServiceImpl.createSubagent()` TODO 未实现

**现状**:
- 只保存状态到数据库
- 未实际调用 OpenClaw sessions_spawn API

**改进方案**:
- ✅ 集成 OpenClaw API 客户端
- ✅ 实际启动 Subagent 进程
- ✅ 实现状态同步

---

### 6️⃣ 影响分析 Git 集成（低优先级）

**问题**: `ImpactAnalysisServiceImpl.analyzeChange()` TODO 未实现

**改进方案**:
- ✅ 集成 JGit
- ✅ 检测代码变更
- ✅ 分析影响范围

---

### 7️⃣ 测试覆盖率补充（高优先级）

**缺失测试**:
- ❌ `KnowledgeServiceTest`
- ❌ `CronServiceTest`
- ❌ `SubagentServiceTest`
- ❌ `ImpactAnalysisServiceTest`
- ❌ `AiTaskDecompositionServiceTest`

**改进方案**:
- ✅ 补充 5 个测试类
- ✅ 目标：80% 覆盖率

---

## 🎯 执行顺序

### Phase 1 - AI 集成（立即执行）
1. ✅ AI 意图识别集成
2. ✅ AI 任务分解集成
3. ✅ 补充测试

### Phase 2 - 知识服务（接下来执行）
1. ✅ 知识萃取 AI 集成
2. ✅ 全文搜索实现
3. ✅ 分页查询实现

### Phase 3 - 定时任务（后续执行）
1. ✅ 梦境时间任务实现
2. ✅ 记忆整理逻辑
3. ✅ 知识萃取流程

### Phase 4 - Subagent 集成（后续执行）
1. ✅ OpenClaw API 客户端
2. ✅ Subagent 实际启动
3. ✅ 状态同步

---

## 📈 预期收益

| 改进项 | 当前状态 | 改进后 | 收益 |
|-------|---------|--------|------|
| AI 意图识别 | ❌ 占位代码 | ✅ 真实 AI 调用 | 功能完整 |
| AI 任务分解 | ❌ 硬编码 | ✅ AI 智能分解 | 实用性提升 |
| 知识萃取 | ❌ 未实现 | ✅ AI 萃取 | 新功能 |
| 梦境时间 | ❌ 空任务 | ✅ 自动整理 | 自动化 |
| 测试覆盖 | ~65% | ~80% | +15% |

---

*计划制定者：可乐 🥤*  
*开始执行时间：2026-04-06 12:00*
