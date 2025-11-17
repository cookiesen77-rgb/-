# QWen API 测试示例

## 📋 测试前准备

1. 确保 API 服务已启动
2. 确认 API Key 和 Base URL 正确配置
3. 准备测试数据

## 🧪 cURL 测试命令

### 1. 基础测试

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

### 2. 完整对话测试

```bash
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

### 3. 题目问答测试

```bash
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
        "content": "你帮我返回问题的答案，使用json格式返回,并且只需返回json数据，其他不用任务信息，谢谢你题目列表:[{\"question\":\"中国的首都是哪里？\",\"options\":[\"北京\",\"上海\",\"广州\",\"深圳\"]}]"
      }
    ]
  }'
```

### 4. 多题目批量测试

```bash
curl http://127.0.0.1:3001/openai-qwen-oauth/v1/chat/completions \
  -H "Authorization: Bearer 123456" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "qwen3-coder-plus",
    "messages": [
      {
        "role": "user",
        "content": "你帮我返回问题的答案，使用json格式返回题目列表:[{\"question\":\"1+1=?\",\"options\":[\"1\",\"2\",\"3\",\"4\"]},{\"question\":\"地球是什么形状？\",\"options\":[\"平的\",\"圆的\",\"方的\"]}]"
      }
    ]
  }'
```

## 💻 Java 代码测试

### 1. 简单测试

```java
@SpringBootTest
public class QWenApiTest {
    
    @Autowired
    private QWen qWen;
    
    @Test
    public void testSimpleCall() throws IOException {
        String content = "测试问题";
        QWen.QWenResponse response = qWen.callWithMessage(content);
        
        System.out.println("回答: " + response.getContent());
        System.out.println("模型: " + response.getModel());
        System.out.println("总Token: " + response.getTotalTokens());
        
        assertNotNull(response.getContent());
    }
}
```

### 2. 完整测试

```java
@SpringBootTest
public class QWenApiIntegrationTest {
    
    @Autowired
    private QWen qWen;
    
    @Test
    public void testQuestionAnswer() throws IOException {
        // 构建题目JSON
        String questions = "[{" +
            "\"question\":\"Java中的String是可变的吗？\"," +
            "\"options\":[\"是\",\"否\"]" +
        "}]";
        
        // 调用API
        QWen.QWenResponse response = qWen.callWithMessage(questions);
        
        // 验证响应
        assertNotNull(response);
        assertNotNull(response.getContent());
        assertTrue(response.getTotalTokens() > 0);
        
        // 打印详细信息
        System.out.println("=== API响应详情 ===");
        System.out.println("内容: " + response.getContent());
        System.out.println("模型: " + response.getModel());
        System.out.println("Prompt Tokens: " + response.getPromptTokens());
        System.out.println("Completion Tokens: " + response.getCompletionTokens());
        System.out.println("总 Tokens: " + response.getTotalTokens());
        System.out.println("原始JSON: " + response.getJson().toString());
    }
    
    @Test
    public void testMultipleQuestions() throws IOException {
        // 多个问题
        String questions = "[" +
            "{\"question\":\"1+1=?\",\"options\":[\"1\",\"2\",\"3\"]}," +
            "{\"question\":\"2+2=?\",\"options\":[\"2\",\"3\",\"4\"]}," +
            "{\"question\":\"3+3=?\",\"options\":[\"5\",\"6\",\"7\"]}" +
        "]";
        
        QWen.QWenResponse response = qWen.callWithMessage(questions);
        
        assertNotNull(response.getContent());
        System.out.println("批量问题答案: " + response.getContent());
    }
    
    @Test
    public void testErrorHandling() {
        // 测试空内容
        assertThrows(IOException.class, () -> {
            qWen.callWithMessage("");
        });
    }
    
    @Test
    public void testStaticCall() throws IOException {
        String content = "静态方法测试";
        QWen.QWenResponse response = QWen.staticCallWithMessage(content);
        
        assertNotNull(response);
        assertNotNull(response.getContent());
    }
}
```

### 3. 性能测试

```java
@SpringBootTest
public class QWenPerformanceTest {
    
    @Autowired
    private QWen qWen;
    
    @Test
    public void testPerformance() throws IOException {
        int requestCount = 10;
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < requestCount; i++) {
            String question = "测试问题 " + (i + 1);
            QWen.QWenResponse response = qWen.callWithMessage(question);
            System.out.println("请求 " + (i + 1) + " 完成，Token使用: " + response.getTotalTokens());
        }
        
        long endTime = System.currentTimeMillis();
        long avgTime = (endTime - startTime) / requestCount;
        
        System.out.println("=== 性能测试结果 ===");
        System.out.println("总请求数: " + requestCount);
        System.out.println("总耗时: " + (endTime - startTime) + "ms");
        System.out.println("平均耗时: " + avgTime + "ms");
    }
}
```

## 🔍 预期响应格式

### 成功响应

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
        "content": "这里是AI的回答内容"
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

### 错误响应

```json
{
  "error": {
    "message": "Invalid API key",
    "type": "invalid_request_error",
    "code": "invalid_api_key"
  }
}
```

## 🛠️ 使用 Postman 测试

### 1. 创建新请求

- **方法**: POST
- **URL**: `http://127.0.0.1:3001/openai-qwen-oauth/v1/chat/completions`

### 2. 设置请求头

```
Authorization: Bearer 123456
Content-Type: application/json
```

### 3. 设置请求体（Body - raw JSON）

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
      "content": "你好"
    }
  ]
}
```

## 📊 测试检查清单

- [ ] API 服务是否运行正常
- [ ] API Key 是否正确配置
- [ ] Base URL 是否可访问
- [ ] 网络连接是否正常
- [ ] 请求格式是否符合 OpenAI API 规范
- [ ] 响应状态码是否为 200
- [ ] 响应内容是否包含 `choices` 数组
- [ ] Token 统计信息是否正确返回
- [ ] 错误处理是否正常工作

## ⚠️ 常见问题排查

### 1. 连接超时

**原因**: 
- API 服务未启动
- Base URL 配置错误
- 网络问题

**解决方案**:
```bash
# 检查服务是否运行
curl http://127.0.0.1:3001/openai-qwen-oauth/

# 检查端口是否开放
netstat -an | grep 3001
```

### 2. 认证失败

**原因**:
- API Key 错误
- Authorization 头格式错误

**解决方案**:
```bash
# 验证 API Key
echo "检查配置文件中的 api-key 是否正确"

# 确保 Authorization 头格式为: Bearer YOUR_API_KEY
```

### 3. 响应格式错误

**原因**:
- API 服务返回格式不符合 OpenAI 标准
- 模型不支持

**解决方案**:
```bash
# 查看原始响应
curl -v http://127.0.0.1:3001/openai-qwen-oauth/v1/chat/completions \
  -H "Authorization: Bearer 123456" \
  -H "Content-Type: application/json" \
  -d '{"model":"qwen3-coder-plus","messages":[{"role":"user","content":"test"}]}'
```

## 📈 监控和日志

### 查看应用日志

```bash
# 查看 Spring Boot 日志
tail -f logs/application.log

# 查看控制台输出
# 会打印发送给模型的完整消息内容
```

### 统计 Token 使用

```java
@Service
public class TokenUsageMonitor {
    
    private int totalPromptTokens = 0;
    private int totalCompletionTokens = 0;
    
    public void recordUsage(QWen.QWenResponse response) {
        totalPromptTokens += response.getPromptTokens();
        totalCompletionTokens += response.getCompletionTokens();
        
        log.info("Token使用统计 - Prompt: {}, Completion: {}, Total: {}", 
            totalPromptTokens, 
            totalCompletionTokens, 
            totalPromptTokens + totalCompletionTokens);
    }
}
```

## 🎯 测试场景示例

### 场景1: 选择题

```bash
curl http://127.0.0.1:3001/openai-qwen-oauth/v1/chat/completions \
  -H "Authorization: Bearer 123456" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "qwen3-coder-plus",
    "messages": [{
      "role": "user",
      "content": "你帮我返回问题的答案，使用json格式返回题目列表:[{\"type\":\"single\",\"question\":\"Python是什么类型的语言？\",\"options\":[\"编译型\",\"解释型\",\"汇编语言\",\"机器语言\"]}]"
    }]
  }'
```

### 场景2: 判断题

```bash
curl http://127.0.0.1:3001/openai-qwen-oauth/v1/chat/completions \
  -H "Authorization: Bearer 123456" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "qwen3-coder-plus",
    "messages": [{
      "role": "user",
      "content": "你帮我返回问题的答案，使用json格式返回题目列表:[{\"type\":\"judge\",\"question\":\"Java是一种面向对象的编程语言\",\"options\":[\"正确\",\"错误\"]}]"
    }]
  }'
```

### 场景3: 多选题

```bash
curl http://127.0.0.1:3001/openai-qwen-oauth/v1/chat/completions \
  -H "Authorization: Bearer 123456" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "qwen3-coder-plus",
    "messages": [{
      "role": "user",
      "content": "你帮我返回问题的答案，使用json格式返回题目列表:[{\"type\":\"multiple\",\"question\":\"以下哪些是编程语言？\",\"options\":[\"Java\",\"Python\",\"HTML\",\"JavaScript\",\"CSS\"]}]"
    }]
  }'
```

---

**更新时间**: 2025-03-03  
**文档版本**: v1.0  
**作者**: DuanInnovator
