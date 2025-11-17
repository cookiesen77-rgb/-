# QWen API 迁移指南

## 📋 概述

本项目已将 QWen API 从 **阿里云 DashScope SDK** 迁移到 **OpenAI 兼容的 HTTP API**，提供更高的灵活性和更广泛的兼容性。

## 🔄 主要变更

### 1. 配置变更

#### 旧配置 (application-dev.yml)
```yaml
large:
  model:
    qwen-api-key: your-api-key-here
```

#### 新配置 (application-dev.yml)
```yaml
large:
  model:
    api-key: 123456  # OpenAI兼容API Key
    base-url: http://127.0.0.1:3001/openai-qwen-oauth  # OpenAI兼容API Base URL
    model: qwen3-coder-plus  # 模型名称（可选）
```

### 2. 配置类变更

#### LargeModelProperties.java

**旧代码:**
```java
@Data
public class LargeModelProperties {
    private String QWenApiKey;
}
```

**新代码:**
```java
@Data
public class LargeModelProperties {
    private String apiKey;
    private String baseUrl;
    private String model = "qwen3-coder-plus";
}
```

### 3. API 调用变更

#### QWen.java

**旧代码:**
```java
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.InputRequiredException;

public GenerationResult callWithMessage(String content) 
    throws ApiException, NoApiKeyException, InputRequiredException {
    // 使用 DashScope SDK
    Generation gen = new Generation();
    // ...
    return gen.call(param);
}
```

**新代码:**
```java
import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.*;
import java.io.IOException;

public QWenResponse callWithMessage(String content) throws IOException {
    // 使用 OkHttp 调用 OpenAI 兼容 API
    Request request = new Request.Builder()
        .url(url)
        .header("Authorization", "Bearer " + apiKey)
        .post(body)
        .build();
    // ...
    return new QWenResponse(jsonResponse);
}
```

### 4. 返回类型变更

#### 旧返回类型
```java
GenerationResult result = qWen.callWithMessage(content);
String answer = result.getOutput().getChoices().get(0).getMessage().getContent();
```

#### 新返回类型
```java
QWen.QWenResponse response = qWen.callWithMessage(content);
String answer = response.getContent();
int tokens = response.getTotalTokens();
```

### 5. 异常处理变更

#### 旧异常
```java
try {
    GenerationResult result = qWen.callWithMessage(content);
} catch (ApiException | NoApiKeyException | InputRequiredException e) {
    // 处理异常
}
```

#### 新异常
```java
try {
    QWen.QWenResponse response = qWen.callWithMessage(content);
} catch (IOException e) {
    // 处理异常
}
```

## 🎯 迁移步骤

### 步骤 1: 更新配置文件

在 `application-dev.yml` 中添加新的配置：

```yaml
large:
  model:
    api-key: your-api-key  # 替换为实际的 API Key
    base-url: http://your-api-endpoint  # 替换为实际的 API 地址
    model: qwen3-coder-plus  # 可选，使用默认值
```

### 步骤 2: 更新代码调用

如果你的代码中有使用 QWen API 的地方，需要更新：

**旧代码示例:**
```java
@Autowired
private QWen qWen;

public void processQuestion(String question) {
    try {
        GenerationResult result = qWen.callWithMessage(question);
        String answer = result.getOutput()
            .getChoices().get(0)
            .getMessage()
            .getContent();
        // 处理答案
    } catch (ApiException | NoApiKeyException | InputRequiredException e) {
        log.error("API调用失败", e);
    }
}
```

**新代码示例:**
```java
@Autowired
private QWen qWen;

public void processQuestion(String question) {
    try {
        QWen.QWenResponse response = qWen.callWithMessage(question);
        String answer = response.getContent();
        
        // 可以获取更多信息
        log.info("使用模型: {}", response.getModel());
        log.info("Token使用: {}", response.getTotalTokens());
        
        // 处理答案
    } catch (IOException e) {
        log.error("API调用失败", e);
    }
}
```

### 步骤 3: 移除旧依赖（可选）

如果不再使用阿里云 DashScope SDK，可以在 `pom.xml` 中移除相关依赖：

```xml
<!-- 可以移除 -->
<!--
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>dashscope-sdk-java</artifactId>
    <version>2.18.3</version>
</dependency>
-->
```

## ✅ 优势

### 1. 更高的灵活性
- 不再绑定到阿里云平台
- 可以使用任何 OpenAI 兼容的 API 服务
- 支持自建 API 代理服务

### 2. 标准化协议
- 使用业界标准的 OpenAI API 协议
- 更容易与其他服务集成
- 学习曲线更平缓

### 3. 简化依赖
- 只需要 OkHttp 和 Jackson（项目已有）
- 不需要额外的 SDK 依赖
- 减小项目体积

### 4. 更好的控制
- 完全控制 HTTP 请求过程
- 更容易调试和监控
- 可以自定义超时、重试等行为

## 📚 相关文档

- [QWen API 调用规则](QWEN_API_RULES.md) - 详细的 API 调用文档
- [README.md](README.md) - 项目主文档

## 🔧 测试命令

使用 cURL 测试 API 是否正常工作：

```bash
curl http://127.0.0.1:3001/openai-qwen-oauth/v1/chat/completions \
  -H "Authorization: Bearer 123456" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "qwen3-coder-plus",
    "messages": [
      {
        "role": "user",
        "content": "你好"
      }
    ]
  }'
```

## ❓ 常见问题

### Q1: 旧的 API Key 还能用吗？
A: 不能。新的 API 使用 OpenAI 兼容协议，需要使用新的 API Key 和 Base URL。

### Q2: 如何获取新的 API Key？
A: 根据你使用的服务提供商获取。如果使用自建服务，请联系管理员。

### Q3: API 响应格式有变化吗？
A: 是的。新 API 返回 `QWenResponse` 对象，提供了便捷的方法来获取内容和统计信息。

### Q4: 性能有影响吗？
A: 使用 HTTP 直接调用，性能与原 SDK 相当或更好。可以通过配置连接池进一步优化。

### Q5: 是否支持流式响应？
A: 当前版本不支持流式响应，如需支持可以扩展实现。

## 🚀 后续计划

- [ ] 添加流式响应支持
- [ ] 实现请求重试机制
- [ ] 添加请求限流功能
- [ ] 支持多种大模型切换
- [ ] 添加响应缓存机制

---

**迁移日期**: 2025-03-03  
**版本**: v2.0.0  
**作者**: DuanInnovator
