package com.tihai.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tihai.properties.LargeModelProperties;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * @Copyright : DuanInnovator
 * @Description : QWen接口 - OpenAI兼容API
 * @Author : DuanInnovator
 * @CreateTime : 2025/3/3
 * @Link : <a href="https://github.com/DuanInnovator/SuperAutotudy">...</a>
 **/
@Component
public class QWen {

    private static QWen instance;
    
    @Autowired
    private LargeModelProperties largeModelProperties;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        instance = this;
    }

    public QWenResponse callWithMessage(String content) throws IOException {
        if (largeModelProperties.getApiKey() == null || largeModelProperties.getApiKey().isEmpty()) {
            throw new IllegalStateException("API Key未配置");
        }
        if (largeModelProperties.getBaseUrl() == null || largeModelProperties.getBaseUrl().isEmpty()) {
            throw new IllegalStateException("Base URL未配置");
        }

        String userContent = "你帮我返回问题的答案，使用json格式返回,并且只需返回json数据，其他不用任务信息，谢谢你题目列表:" + content;
        System.out.println(userContent);

        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", largeModelProperties.getModel());
        
        ArrayNode messages = objectMapper.createArrayNode();
        ObjectNode systemMessage = objectMapper.createObjectNode();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a helpful assistant.");
        messages.add(systemMessage);
        
        ObjectNode userMessage = objectMapper.createObjectNode();
        userMessage.put("role", "user");
        userMessage.put("content", userContent);
        messages.add(userMessage);
        
        requestBody.set("messages", messages);

        String url = largeModelProperties.getBaseUrl();
        if (!url.endsWith("/")) {
            url += "/";
        }
        url += "v1/chat/completions";

        RequestBody body = RequestBody.create(
            requestBody.toString(),
            MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + largeModelProperties.getApiKey())
                .header("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("API调用失败: " + response.code() + " " + response.message());
            }

            String responseBody = response.body().string();
            JsonNode jsonResponse = objectMapper.readTree(responseBody);
            
            return new QWenResponse(jsonResponse);
        }
    }

    public static QWenResponse staticCallWithMessage(String content) throws IOException {
        if (instance == null) {
            throw new IllegalStateException("QWen 实例尚未初始化");
        }
        return instance.callWithMessage(content);
    }

    public static class QWenResponse {
        private final JsonNode responseJson;

        public QWenResponse(JsonNode responseJson) {
            this.responseJson = responseJson;
        }

        public String getContent() {
            try {
                return responseJson.path("choices")
                        .path(0)
                        .path("message")
                        .path("content")
                        .asText();
            } catch (Exception e) {
                return "";
            }
        }

        public JsonNode getJson() {
            return responseJson;
        }

        public String getModel() {
            return responseJson.path("model").asText();
        }

        public int getPromptTokens() {
            return responseJson.path("usage").path("prompt_tokens").asInt(0);
        }

        public int getCompletionTokens() {
            return responseJson.path("usage").path("completion_tokens").asInt(0);
        }

        public int getTotalTokens() {
            return responseJson.path("usage").path("total_tokens").asInt(0);
        }
    }

}

