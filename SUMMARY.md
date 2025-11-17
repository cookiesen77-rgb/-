# QWen API 修改摘要

## 📋 任务完成情况

✅ **已完成**: 将 QWen API 调用从阿里云 DashScope SDK 迁移到 OpenAI 兼容的 HTTP API

## 🔄 主要变更

### 1. 代码变更

#### ✅ `LargeModelProperties.java`
- **位置**: `src/main/java/com/tihai/properties/LargeModelProperties.java`
- **变更**:
  - 将 `QWenApiKey` 更名为 `apiKey`
  - 新增 `baseUrl` 属性
  - 新增 `model` 属性（默认：`qwen3-coder-plus`）

#### ✅ `QWen.java`
- **位置**: `src/main/java/com/tihai/api/QWen.java`
- **变更**:
  - 移除阿里云 DashScope SDK 依赖
  - 使用 OkHttp + Jackson 实现 HTTP 调用
  - 实现 OpenAI Chat Completions API 协议
  - 新增 `QWenResponse` 内部类封装响应
  - 方法签名变更: `GenerationResult` → `QWenResponse`
  - 异常变更: `ApiException, NoApiKeyException, InputRequiredException` → `IOException`

### 2. 配置变更

#### ✅ `application-dev.yml`
- **位置**: `src/main/resources/application-dev.yml`
- **变更**:
```yaml
# 旧配置
large:
  model:
    qwen-api-key: your-api-key-here

# 新配置
large:
  model:
    api-key: 123456
    base-url: http://127.0.0.1:3001/openai-qwen-oauth
    model: qwen3-coder-plus
```

#### ✅ `README.md`
- 更新配置示例
- 添加文档链接

#### ✅ `.gitignore`
- 新增项目 .gitignore 文件
- 保护敏感配置文件

### 3. 新增文档

| 文档 | 大小 | 描述 |
|------|------|------|
| `QWEN_API_RULES.md` | 13KB | QWen API 调用规则详细文档（505行） |
| `MIGRATION_GUIDE.md` | 6.3KB | 从旧版本迁移指南 |
| `CHANGELOG.md` | 5.0KB | 变更日志 |
| `API_TEST_EXAMPLE.md` | 11KB | API 测试示例和方法 |
| `SUMMARY.md` | 当前文档 | 变更摘要 |

## 🎯 API 调用方式对比

### 旧方式（DashScope SDK）

```java
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.exception.*;

try {
    GenerationResult result = qWen.callWithMessage(content);
    String answer = result.getOutput()
        .getChoices().get(0)
        .getMessage()
        .getContent();
} catch (ApiException | NoApiKeyException | InputRequiredException e) {
    // 处理异常
}
```

### 新方式（OpenAI 兼容 API）

```java
import java.io.IOException;

try {
    QWen.QWenResponse response = qWen.callWithMessage(content);
    String answer = response.getContent();
    int tokens = response.getTotalTokens();
} catch (IOException e) {
    // 处理异常
}
```

## 📊 技术细节

### HTTP 请求示例

```http
POST http://127.0.0.1:3001/openai-qwen-oauth/v1/chat/completions
Authorization: Bearer 123456
Content-Type: application/json

{
  "model": "qwen3-coder-plus",
  "messages": [
    {
      "role": "system",
      "content": "You are a helpful assistant."
    },
    {
      "role": "user",
      "content": "你帮我返回问题的答案，使用json格式返回..."
    }
  ]
}
```

### 响应格式

```json
{
  "choices": [{
    "message": {
      "content": "答案内容"
    }
  }],
  "usage": {
    "prompt_tokens": 100,
    "completion_tokens": 50,
    "total_tokens": 150
  },
  "model": "qwen3-coder-plus"
}
```

## ✨ 新功能

### QWenResponse 类方法

```java
// 获取回答内容
String content = response.getContent();

// 获取模型名称
String model = response.getModel();

// 获取 token 统计
int promptTokens = response.getPromptTokens();
int completionTokens = response.getCompletionTokens();
int totalTokens = response.getTotalTokens();

// 获取原始 JSON
JsonNode json = response.getJson();
```

## 🔐 配置说明

### 必需配置

| 配置项 | 说明 | 示例值 |
|--------|------|--------|
| `large.model.api-key` | API Key | `123456` |
| `large.model.base-url` | API Base URL | `http://127.0.0.1:3001/openai-qwen-oauth` |

### 可选配置

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `large.model.model` | 模型名称 | `qwen3-coder-plus` |

## 🧪 测试方法

### cURL 测试

```bash
export OPENAI_API_KEY=123456
export OPENAI_BASE_URL=http://127.0.0.1:3001/openai-qwen-oauth

curl $OPENAI_BASE_URL/v1/chat/completions \
  -H "Authorization: Bearer $OPENAI_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "qwen3-coder-plus",
    "messages": [{"role": "user", "content": "你好"}]
  }'
```

### Java 测试

```java
@Autowired
private QWen qWen;

@Test
public void testQWenApi() throws IOException {
    QWen.QWenResponse response = qWen.callWithMessage("测试问题");
    assertNotNull(response.getContent());
    System.out.println("答案: " + response.getContent());
    System.out.println("Token: " + response.getTotalTokens());
}
```

## 📦 依赖说明

### 使用的依赖（项目已有）

- `com.squareup.okhttp3:okhttp:4.12.0`
- `com.fasterxml.jackson.core:jackson-databind:2.15.4`

### 可移除的依赖（可选）

```xml
<!-- 不再需要 -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>dashscope-sdk-java</artifactId>
    <version>2.18.3</version>
</dependency>
```

## ✅ 优势

1. **标准化**: 使用 OpenAI 标准协议，兼容性更好
2. **灵活性**: 可切换任何 OpenAI 兼容的服务提供商
3. **简化**: 减少外部依赖，使用项目已有的 OkHttp 和 Jackson
4. **透明**: 完全控制 HTTP 请求，易于调试和监控
5. **扩展性**: 易于添加重试、超时、限流等功能

## 📚 相关文档

| 文档 | 描述 |
|------|------|
| [QWEN_API_RULES.md](QWEN_API_RULES.md) | 详细的 API 调用规则和示例 |
| [MIGRATION_GUIDE.md](MIGRATION_GUIDE.md) | 完整的迁移指南 |
| [CHANGELOG.md](CHANGELOG.md) | 详细的变更日志 |
| [API_TEST_EXAMPLE.md](API_TEST_EXAMPLE.md) | API 测试示例和方法 |
| [README.md](README.md) | 项目主文档 |

## 🔍 文件清单

### 修改的文件

- ✏️ `src/main/java/com/tihai/api/QWen.java`
- ✏️ `src/main/java/com/tihai/properties/LargeModelProperties.java`
- ✏️ `src/main/resources/application-dev.yml`
- ✏️ `README.md`

### 新增的文件

- ✨ `.gitignore`
- ✨ `QWEN_API_RULES.md`
- ✨ `MIGRATION_GUIDE.md`
- ✨ `CHANGELOG.md`
- ✨ `API_TEST_EXAMPLE.md`
- ✨ `SUMMARY.md`

## 🎓 快速开始

### 1. 更新配置

编辑 `src/main/resources/application-dev.yml`:

```yaml
large:
  model:
    api-key: your-api-key-here
    base-url: http://your-api-endpoint
    model: qwen3-coder-plus
```

### 2. 测试连接

```bash
curl http://your-api-endpoint/v1/chat/completions \
  -H "Authorization: Bearer your-api-key-here" \
  -H "Content-Type: application/json" \
  -d '{"model":"qwen3-coder-plus","messages":[{"role":"user","content":"test"}]}'
```

### 3. 运行应用

```bash
# 编译
mvn clean compile

# 运行
mvn spring-boot:run
```

### 4. 验证功能

```java
// 在代码中调用
@Autowired
private QWen qWen;

QWen.QWenResponse response = qWen.callWithMessage("测试问题");
System.out.println(response.getContent());
```

## ⚠️ 注意事项

1. **API Key 安全**: 不要将 API Key 提交到版本控制系统
2. **Base URL**: 确保 Base URL 可访问且正确配置
3. **网络**: 确保网络连接正常，防火墙允许访问
4. **兼容性**: 确保 API 服务遵循 OpenAI Chat Completions API 规范
5. **错误处理**: 捕获并妥善处理 IOException

## 🐛 故障排查

### 问题 1: API Key未配置

**错误**: `IllegalStateException: API Key未配置`

**解决**: 检查 `application-dev.yml` 中的 `large.model.api-key` 配置

### 问题 2: Base URL未配置

**错误**: `IllegalStateException: Base URL未配置`

**解决**: 检查 `application-dev.yml` 中的 `large.model.base-url` 配置

### 问题 3: 连接超时

**错误**: `IOException: timeout`

**解决**: 
- 检查 Base URL 是否正确
- 检查网络连接
- 增加超时时间配置

### 问题 4: 认证失败

**错误**: `IOException: API调用失败: 401 Unauthorized`

**解决**: 验证 API Key 是否正确

## 📞 联系方式

- **作者**: DuanInnovator
- **项目地址**: https://github.com/DuanInnovator/SuperAutotudy
- **文档**: https://doc.xxtmooc.com

---

**完成日期**: 2025-03-03  
**版本**: v2.0.0  
**状态**: ✅ 已完成
