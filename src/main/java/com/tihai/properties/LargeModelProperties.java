package com.tihai.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Copyright : DuanInnovator
 * @Description : 大模型配置
 * @Author : DuanInnovator
 * @CreateTime : 2025/3/3
 * @Link : <a href="https://github.com/DuanInnovator/SuperAutotudy">...</a>
 **/
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

