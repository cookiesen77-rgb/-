# 变更日志 (Changelog)

## [2.0.0] - 2025-03-03

### 🔄 重大变更 (Breaking Changes)

#### QWen API 迁移到 OpenAI 兼容协议

**变更说明:**
- 从阿里云 DashScope SDK 迁移到 OpenAI 兼容的 HTTP API
- 提供更高的灵活性和更广泛的服务提供商兼容性

**影响范围:**
- 配置文件格式变更
- API 调用方式变更
- 返回类型变更
- 异常处理变更

### ✨ 新增功能 (Added)

1. **OpenAI 兼容 API 支持**
   - 支持任何 OpenAI 兼容的 API 服务
   - 支持自定义 API Base URL
   - 支持自定义模型名称

2. **新的响应封装类 `QWenResponse`**
   - 提供 `getContent()` 获取回答内容
   - 提供 `getModel()` 获取模型名称
   - 提供 `getPromptTokens()` / `getCompletionTokens()` / `getTotalTokens()` 获取 token 使用统计
   - 提供 `getJson()` 获取原始 JSON 响应

3. **新增文档**
   - `QWEN_API_RULES.md` - QWen API 调用规则详细文档
   - `MIGRATION_GUIDE.md` - 从旧版本迁移指南
   - `CHANGELOG.md` - 变更日志

### 🔧 配置变更 (Configuration Changes)

#### application-dev.yml

**旧配置:**
```yaml
large:
  model:
    qwen-api-key: your-api-key-here
```

**新配置:**
```yaml
large:
  model:
    api-key: 123456
    base-url: http://127.0.0.1:3001/openai-qwen-oauth
    model: qwen3-coder-plus
```

### 📝 代码变更 (Code Changes)

#### LargeModelProperties.java

**变更内容:**
- 将 `QWenApiKey` 更名为 `apiKey`
- 新增 `baseUrl` 属性
- 新增 `model` 属性（默认值：qwen3-coder-plus）

#### QWen.java

**主要变更:**
- 移除阿里云 DashScope SDK 依赖
- 使用 OkHttp 进行 HTTP 请求
- 使用 Jackson 进行 JSON 处理
- 实现 OpenAI Chat Completions API 协议
- 新增 `QWenResponse` 内部类封装响应

**方法签名变更:**
```java
// 旧版本
public GenerationResult callWithMessage(String content) 
    throws ApiException, NoApiKeyException, InputRequiredException

// 新版本
public QWenResponse callWithMessage(String content) 
    throws IOException
```

### 🗑️ 移除 (Removed)

1. **移除的导入:**
   - `com.alibaba.dashscope.aigc.generation.GenerationResult`
   - `com.alibaba.dashscope.aigc.generation.Generation`
   - `com.alibaba.dashscope.aigc.generation.GenerationParam`
   - `com.alibaba.dashscope.common.Message`
   - `com.alibaba.dashscope.common.Role`
   - `com.alibaba.dashscope.exception.ApiException`
   - `com.alibaba.dashscope.exception.InputRequiredException`
   - `com.alibaba.dashscope.exception.NoApiKeyException`

2. **可选移除的依赖:**
   - `com.alibaba:dashscope-sdk-java:2.18.3` (可保留用于其他功能)

### 🐛 修复 (Fixed)

- 改进 API Key 和 Base URL 的配置校验
- 增强错误处理和异常信息

### 📚 文档更新 (Documentation)

1. **README.md**
   - 更新配置示例
   - 添加 QWen API 调用规则文档链接
   - 添加迁移指南链接

2. **新增文档**
   - 详细的 API 调用规则文档（272行）
   - 完整的迁移指南
   - cURL 测试示例
   - 完整的代码示例

### 🔐 安全改进 (Security)

- API Key 通过 Authorization Bearer Token 传递
- 支持 HTTPS 连接
- 建议不将 API Key 提交到版本控制

### ⚡ 性能优化 (Performance)

- 使用 OkHttp 客户端，支持连接池复用
- 减少依赖体积
- 更直接的 HTTP 调用

### 🎯 兼容性说明 (Compatibility)

#### 支持的服务

新版本支持所有 OpenAI 兼容的 API 服务，包括但不限于：
- 自建 OpenAI 代理服务
- 通义千问 OpenAI 兼容接口
- 其他实现 OpenAI Chat Completions API 的服务

#### 支持的模型

可通过配置文件指定任何支持的模型名称：
- `qwen3-coder-plus` (默认)
- `qwen-plus`
- `qwen-turbo`
- 其他兼容模型

### 📋 迁移清单 (Migration Checklist)

如果从旧版本升级，请完成以下步骤：

- [ ] 更新 `application-dev.yml` 配置
  - [ ] 将 `qwen-api-key` 改为 `api-key`
  - [ ] 添加 `base-url` 配置
  - [ ] 添加 `model` 配置（可选）
- [ ] 更新代码中的 API 调用
  - [ ] 将 `GenerationResult` 改为 `QWen.QWenResponse`
  - [ ] 更新异常处理（`IOException`）
  - [ ] 更新结果获取方式（使用 `getContent()`）
- [ ] 测试 API 连接
  - [ ] 使用 cURL 测试端点
  - [ ] 验证 API Key 有效性
  - [ ] 运行应用测试

### 🔗 相关链接 (Links)

- [QWen API 调用规则](QWEN_API_RULES.md)
- [迁移指南](MIGRATION_GUIDE.md)
- [使用文档](https://doc.xxtmooc.com)
- [OpenAI API 文档](https://platform.openai.com/docs/api-reference/chat/create)

### 👥 贡献者 (Contributors)

- DuanInnovator

### 📅 发布信息 (Release Info)

- **版本**: v2.0.0
- **发布日期**: 2025-03-03
- **类型**: 重大版本更新
- **兼容性**: 不向后兼容（需要迁移）

---

## [1.0.1] - 2025-04-26

### 功能
- 支持多任务同时运行
- 支持自动完成章节测验
- 基于 Springboot + RabbitMQ + docker + Dubbo 的超星学习通自动刷课平台

---

**注意**: 更多历史版本信息请查看项目 Git 提交记录。
