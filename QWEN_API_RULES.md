# QWen API 调用规则文档

## 📋 概述

本项目使用 **OpenAI 兼容的 API 接口** 来调用通义千问（QWen）大语言模型，用于辅助回答学习通题目。本文档详细说明了 OpenAI 兼容 API 的调用规则、配置方式和使用场景。

## 🔧 技术架构

### 依赖库
- **HTTP 客户端**: `OkHttp 4.12.0`
- **JSON 处理**: `Jackson 2.15.4`
- **模型**: `qwen3-coder-plus` (可配置)
- **API 协议**: OpenAI Chat Completions API

### 核心类
- **位置**: `com.tihai.api.QWen`
- **配置类**: `com.tihai.properties.LargeModelProperties`
- **响应类**: `com.tihai.api.QWen.QWenResponse`

## 📝 配置说明

### 1. 应用配置 (application-dev.yml)

```yaml
large:
  model:
    api-key: 123456  # OpenAI兼容API Key
    base-url: http://127.0.0.1:3001/openai-qwen-oauth  # OpenAI兼容API Base URL
    model: qwen3-coder-plus  # 模型名称（可选，默认为qwen3-coder-plus）
```

### 2. 环境变量配置（可选）

```bash
export OPENAI_API_KEY=123456
export OPENAI_BASE_URL=http://127.0.0.1:3001/openai-qwen-oauth
```

### 3. 配置属性类

```java
@ConfigurationProperties(prefix = "large.model")
@Configuration
@Data
public class LargeModelProperties {
    /**
     * OpenAI兼容的API Key
     */
    private String apiKey;

    /**
     * OpenAI兼容的API Base URL
     */
    private String baseUrl;

    /**
     * 模型名称
     */
    private String model = "qwen3-coder-plus";
}
```

## 🚀 API 调用规则

### API 端点

完整 URL 格式：`{baseUrl}/v1/chat/completions`

示例：`http://127.0.0.1:3001/openai-qwen-oauth/v1/chat/completions`

### 请求格式

#### HTTP 请求头

```http
POST /v1/chat/completions HTTP/1.1
Host: 127.0.0.1:3001
Authorization: Bearer 123456
Content-Type: application/json
```

#### 请求体（JSON）

```json
{
  "model": "qwen3-coder-plus",
  "messages": [
    {
      "role": "system",
      "content": "You are a helpful assistant."
    },
    {
      "role": "user",
      "content": "你帮我返回问题的答案，使用json格式返回,并且只需返回json数据，其他不用任务信息，谢谢你题目列表:[题目内容]"
    }
  ]
}
```

#### 请求参数说明

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `model` | String | 是 | 模型名称，如 `qwen3-coder-plus` |
| `messages` | Array | 是 | 对话消息列表 |
| `messages[].role` | String | 是 | 角色：`system`、`user` 或 `assistant` |
| `messages[].content` | String | 是 | 消息内容 |

### 响应格式

#### 成功响应（JSON）

```json
{
  "id": "chatcmpl-xxxxx",
  "object": "chat.completion",
  "created": 1234567890,
  "model": "qwen3-coder-plus",
  "choices": [
    {
      "index": 0,
      "message": {
        "role": "assistant",
        "content": "答案内容"
      },
      "finish_reason": "stop"
    }
  ],
  "usage": {
    "prompt_tokens": 100,
    "completion_tokens": 50,
    "total_tokens": 150
  }
}
```

#### 响应参数说明

| 参数 | 类型 | 说明 |
|------|------|------|
| `choices[0].message.content` | String | AI 生成的回答内容 |
| `model` | String | 实际使用的模型名称 |
| `usage.prompt_tokens` | Integer | 输入 tokens 数量 |
| `usage.completion_tokens` | Integer | 输出 tokens 数量 |
| `usage.total_tokens` | Integer | 总 tokens 数量 |

## 💻 代码实现

### 调用方式

#### 1. 实例方法调用（推荐）

```java
@Autowired
private QWen qWen;

public void example() {
    try {
        QWen.QWenResponse response = qWen.callWithMessage(content);
        String answer = response.getContent();
        System.out.println("答案: " + answer);
    } catch (IOException e) {
        log.error("API调用失败", e);
    }
}
```

#### 2. 静态方法调用

```java
public void example() {
    try {
        QWen.QWenResponse response = QWen.staticCallWithMessage(content);
        String answer = response.getContent();
    } catch (IOException e) {
        log.error("API调用失败", e);
    }
}
```

### QWenResponse 响应对象方法

```java
public class QWenResponse {
    // 获取回答内容
    public String getContent();
    
    // 获取原始 JSON 响应
    public JsonNode getJson();
    
    // 获取模型名称
    public String getModel();
    
    // 获取 prompt tokens 数量
    public int getPromptTokens();
    
    // 获取 completion tokens 数量
    public int getCompletionTokens();
    
    // 获取总 tokens 数量
    public int getTotalTokens();
}
```

### 调用流程

```
┌─────────────────────┐
│  调用入口            │
│  callWithMessage    │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│  配置校验            │
│  - API Key          │
│  - Base URL         │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│  构建请求体          │
│  - System Message   │
│  - User Message     │
│  - Model            │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│  构建 HTTP 请求      │
│  - URL              │
│  - Authorization    │
│  - Content-Type     │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│  发送 HTTP POST      │
│  (OkHttp Client)    │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│  解析响应            │
│  (Jackson)          │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│  返回 QWenResponse   │
└─────────────────────┘
```

## 🎯 使用场景

1. **题目答案生成**: 当题库查询失败或无法找到答案时，使用大模型生成答案
2. **JSON格式返回**: 明确要求模型以JSON格式返回答案，便于程序解析
3. **批量题目处理**: 支持传入题目列表进行批量处理

## ⚠️ 注意事项

### 1. 配置检查

```java
if (largeModelProperties.getApiKey() == null || largeModelProperties.getApiKey().isEmpty()) {
    throw new IllegalStateException("API Key未配置");
}
if (largeModelProperties.getBaseUrl() == null || largeModelProperties.getBaseUrl().isEmpty()) {
    throw new IllegalStateException("Base URL未配置");
}
```

- 使用前必须配置 `api-key` 和 `base-url`
- 配置缺失会抛出 `IllegalStateException`

### 2. 异常处理

调用时需要处理以下异常：
- `IOException`: HTTP 请求异常
- `IllegalStateException`: 配置缺失或实例未初始化

### 3. URL 自动拼接

代码会自动拼接 `/v1/chat/completions` 路径：

```java
String url = largeModelProperties.getBaseUrl();
if (!url.endsWith("/")) {
    url += "/";
}
url += "v1/chat/completions";
```

配置 `base-url` 时可以不包含尾部的 `/v1/chat/completions`

### 4. 日志输出

```java
String userContent = "你帮我返回问题的答案，使用json格式返回,并且只需返回json数据，其他不用任务信息，谢谢你题目列表:" + content;
System.out.println(userContent);
```

- 会打印发送给模型的完整用户消息
- 用于调试和审计

## 🔗 相关链接

- **OpenAI API 文档**: https://platform.openai.com/docs/api-reference/chat/create
- **项目文档**: https://doc.xxtmooc.com

## 📊 完整调用示例

### 基础示例

```java
@Component
public class QuestionAnswerService {
    
    @Autowired
    private QWen qWen;
    
    public String getAnswerFromAI(String questionContent) {
        try {
            // 构建题目内容
            String content = "[{\"question\":\"题目内容\",\"options\":[\"A\",\"B\",\"C\",\"D\"]}]";
            
            // 调用QWen API
            QWen.QWenResponse response = qWen.callWithMessage(content);
            
            // 获取答案
            String answer = response.getContent();
            
            // 打印token使用情况
            System.out.println("Prompt tokens: " + response.getPromptTokens());
            System.out.println("Completion tokens: " + response.getCompletionTokens());
            System.out.println("Total tokens: " + response.getTotalTokens());
            
            return answer;
        } catch (IOException e) {
            log.error("QWen API调用失败", e);
            return null;
        }
    }
}
```

### cURL 测试命令

```bash
export OPENAI_API_KEY=123456
export OPENAI_BASE_URL=http://127.0.0.1:3001/openai-qwen-oauth

curl http://127.0.0.1:3001/openai-qwen-oauth/v1/chat/completions \
  -H "Authorization: Bearer 123456" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "qwen3-coder-plus",
    "messages": [
      {
        "role": "system",
        "content": "You are a helpful assistant."
      },
      {
        "role": "user",
        "content": "帮我写一个快速排序（Python）"
      }
    ]
  }'
```

### 高级示例 - 带重试机制

```java
public String getAnswerWithRetry(String content, int maxRetries) {
    int retries = 0;
    Exception lastException = null;
    
    while (retries < maxRetries) {
        try {
            QWen.QWenResponse response = qWen.callWithMessage(content);
            return response.getContent();
        } catch (IOException e) {
            lastException = e;
            retries++;
            log.warn("API调用失败，第{}次重试", retries, e);
            
            if (retries < maxRetries) {
                try {
                    Thread.sleep(1000 * retries); // 指数退避
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
    
    log.error("API调用失败，已达到最大重试次数", lastException);
    return null;
}
```

## 📈 性能考虑

1. **连接池配置**: 
   - 当前使用默认的 OkHttpClient
   - 建议配置连接池和超时时间

2. **超时配置示例**:
   ```java
   private final OkHttpClient httpClient = new OkHttpClient.Builder()
       .connectTimeout(30, TimeUnit.SECONDS)
       .readTimeout(60, TimeUnit.SECONDS)
       .writeTimeout(30, TimeUnit.SECONDS)
       .build();
   ```

3. **成本优化**:
   - 优先使用题库查询
   - 仅在题库无结果时调用大模型
   - 建议缓存常见题目答案

4. **并发控制**:
   - 建议实现请求限流机制
   - 控制并发请求数量
   - 遵循 API 提供商的速率限制

## 🛡️ 安全建议

1. **API Key 保护**:
   - 不要将 API Key 提交到版本控制
   - 使用环境变量或配置中心管理
   - 定期轮换 API Key

2. **输入验证**:
   - 对传入内容进行长度限制
   - 过滤敏感信息
   - 防止注入攻击

3. **输出检查**:
   - 验证返回的 JSON 格式
   - 检查答案合理性
   - 记录异常结果用于审计

4. **网络安全**:
   - 生产环境使用 HTTPS
   - 验证 SSL 证书
   - 配置代理时注意安全性

## 🔄 与阿里云 DashScope SDK 的区别

| 特性 | DashScope SDK | OpenAI 兼容 API |
|------|---------------|-----------------|
| 依赖 | `dashscope-sdk-java` | `OkHttp + Jackson` |
| 协议 | 阿里云专有协议 | OpenAI 标准协议 |
| 灵活性 | 固定于阿里云 | 可切换多个提供商 |
| 学习成本 | 需学习专有 API | 通用 OpenAI API |
| 异常类型 | `ApiException`, `NoApiKeyException` | `IOException` |
| 返回类型 | `GenerationResult` | `QWenResponse` (自定义) |

## 📦 依赖说明

本实现不需要阿里云 DashScope SDK，仅使用项目已有的依赖：

```xml
<!-- 已有依赖 -->
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.12.0</version>
</dependency>

<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.15.4</version>
</dependency>
```

可以移除 DashScope SDK 依赖（可选）：

```xml
<!-- 不再需要 -->
<!-- <dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>dashscope-sdk-java</artifactId>
    <version>2.18.3</version>
</dependency> -->
```

## 🎓 总结

- ✅ 使用标准的 OpenAI Chat Completions API 协议
- ✅ 支持任何 OpenAI 兼容的 API 服务
- ✅ 简化依赖，只使用 OkHttp 和 Jackson
- ✅ 灵活配置 API Key、Base URL 和模型名称
- ✅ 提供便捷的响应封装类
- ✅ 支持 token 使用统计

---

**最后更新**: 2025-03-03  
**作者**: DuanInnovator  
**项目**: SuperAutoStudy
