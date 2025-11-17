# QWen API 调用规则文档

## 📋 概述

本项目集成了阿里巴巴通义千问（QWen）大语言模型，用于辅助回答学习通题目。本文档详细说明了QWen API的调用规则、配置方式和使用场景。

## 🔧 技术架构

### 依赖库
- **SDK**: `com.alibaba:dashscope-sdk-java:2.18.3`
- **模型**: `qwen-plus`
- **API提供商**: 阿里云百炼平台 (DashScope)

### 核心类
- **位置**: `com.tihai.api.QWen`
- **配置类**: `com.tihai.properties.LargeModelProperties`

## 📝 配置说明

### 1. Maven依赖配置 (pom.xml)

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>dashscope-sdk-java</artifactId>
    <version>2.18.3</version>
    <exclusions>
        <!-- 排除冲突的 SLF4J 简单绑定 -->
        <exclusion>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

### 2. 应用配置 (application-dev.yml)

```yaml
large:
  model:
    qwen-api-key: your-api-key-here  # 通义千问 API Key
```

**获取 API Key 方式**:
- 访问阿里云百炼平台: https://help.aliyun.com/zh/model-studio/getting-started/models
- 注册并创建应用后获取 API Key

### 3. 配置属性类

```java
@ConfigurationProperties(prefix = "large.model")
@Configuration
@Data
public class LargeModelProperties {
    /**
     * QWen Api Key
     * TODO 将引入OpenAI等
     */
    private String QWenApiKey;
}
```

## 🚀 调用规则

### 调用方式

#### 1. 实例方法调用
```java
@Autowired
private QWen qWen;

public void example() throws ApiException, NoApiKeyException, InputRequiredException {
    GenerationResult result = qWen.callWithMessage(content);
}
```

#### 2. 静态方法调用
```java
public void example() throws ApiException, NoApiKeyException, InputRequiredException {
    GenerationResult result = QWen.staticCallWithMessage(content);
}
```

### API 调用参数规则

#### 请求构建规则

1. **System Message (系统消息)**
   - **角色**: `Role.SYSTEM`
   - **内容**: `"You are a helpful assistant."`
   - **作用**: 定义AI助手的基本角色和行为

2. **User Message (用户消息)**
   - **角色**: `Role.USER`
   - **内容格式**: `"你帮我返回问题的答案，使用json格式返回,并且只需返回json数据，其他不用任务信息，谢谢你" + "题目列表:" + content`
   - **作用**: 传递具体的题目内容和回答要求

3. **API参数配置**
   ```java
   GenerationParam param = GenerationParam.builder()
       .apiKey(largeModelProperties.getQWenApiKey())  // API密钥
       .model("qwen-plus")                            // 使用qwen-plus模型
       .messages(Arrays.asList(systemMsg, userMsg))   // 对话消息列表
       .resultFormat(GenerationParam.ResultFormat.MESSAGE)  // 返回格式为消息格式
       .build();
   ```

### 调用流程

```
┌─────────────────┐
│  调用入口       │
│ callWithMessage │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ 创建Generation  │
│ 实例            │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ 构建System消息  │
│ (助手角色定义)  │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ 构建User消息    │
│ (题目+要求)     │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ 构建API参数     │
│ - API Key       │
│ - Model         │
│ - Messages      │
│ - Result Format │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ 调用 gen.call() │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ 返回结果        │
│ GenerationResult│
└─────────────────┘
```

## 🎯 使用场景

根据代码分析，QWen API主要用于以下场景：

1. **题目答案生成**: 当题库查询失败或无法找到答案时，使用大模型生成答案
2. **JSON格式返回**: 明确要求模型以JSON格式返回答案，便于程序解析
3. **批量题目处理**: 支持传入题目列表进行批量处理

## ⚠️ 注意事项

### 1. 初始化检查
```java
public static GenerationResult staticCallWithMessage(String content) {
    if (instance == null) {
        throw new IllegalStateException("QWen 实例尚未初始化");
    }
    return instance.callWithMessage(content);
}
```
- 使用静态方法前，必须确保Spring容器已完成`@PostConstruct`初始化
- 避免在容器启动早期调用

### 2. 异常处理
调用时需要处理以下异常：
- `ApiException`: API调用异常
- `NoApiKeyException`: API Key未配置或无效
- `InputRequiredException`: 必需参数缺失

### 3. 日志输出
```java
System.out.println(userMsg.getContent());
```
- 当前会打印发送给模型的完整消息内容
- 用于调试和审计

## 🔗 相关链接

- **阿里云模型列表**: https://help.aliyun.com/zh/model-studio/getting-started/models
- **DashScope SDK文档**: https://help.aliyun.com/zh/dashscope/
- **项目文档**: https://doc.xxtmooc.com

## 🔄 扩展计划

根据代码注释：
```java
/**
 * QWen Api Key
 * TODO 将引入OpenAI等
 */
```

未来计划支持更多大模型：
- OpenAI GPT系列
- 其他主流LLM服务

## 📊 调用示例

### 完整调用示例
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
            GenerationResult result = qWen.callWithMessage(content);
            
            // 解析结果
            String answer = result.getOutput().getChoices().get(0).getMessage().getContent();
            
            return answer;
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            log.error("QWen API调用失败", e);
            return null;
        }
    }
}
```

## 📈 性能考虑

1. **调用限制**: 
   - 遵循阿里云API调用频率限制
   - 建议实现请求限流机制

2. **超时配置**:
   - 大模型响应可能较慢
   - 建议设置合理的超时时间

3. **成本优化**:
   - 优先使用题库查询
   - 仅在题库无结果时调用大模型
   - 建议缓存常见题目答案

## 🛡️ 安全建议

1. **API Key保护**:
   - 不要将API Key提交到版本控制
   - 使用环境变量或配置中心管理
   - 定期轮换API Key

2. **输入验证**:
   - 对传入内容进行长度限制
   - 过滤敏感信息
   - 防止注入攻击

3. **输出检查**:
   - 验证返回的JSON格式
   - 检查答案合理性
   - 记录异常结果用于审计
