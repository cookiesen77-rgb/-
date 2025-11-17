# 🌟 项目名称（Super-Auto）

[//]: # (![GitHub stars]&#40;https://github.com/DuanInnovator/SuperAutoStudy/stars.svg&#41;)

[//]: # ()
[//]: # (![GitHub forks]&#40;https://img.shields.io/github/forks/your-repo.svg&#41;)

[//]: # ()
[//]: # (![GitHub issues]&#40;https://img.shields.io/github/issues/your-repo.svg&#41;)

[//]: # ()
[//]: # (![GitHub license]&#40;https://img.shields.io/github/license/your-repo.svg&#41;)

> ✨ “本项目是基于Springboot+RabbitMQ+docker+Dubbo的超星学习通自动刷课平台”
## 📚 文档入口
- [<span style="font-size: 1.5em">📖 使用文档</span>](https://doc.xxtmooc.com)
- [🤖 QWen API 调用规则](QWEN_API_RULES.md)
- [🔄 API 迁移指南](MIGRATION_GUIDE.md)

## 🎯 功能特性（Features）

[//]: # (✅ **现代化 UI**：使用 Vue3 + Element Plus 构建，界面简洁美观  )

[//]: # (✅ **权限管理**：支持基于角色的权限控制  )

[//]: # (✅ **数据可视化**：集成 ECharts，支持多种图表展示  )

[//]: # (✅ **响应式布局**：兼容 PC 和移动端  )

[//]: # (✅ **多语言支持**：支持 i18n 国际化  )

[//]: # (✅ **用户友好**：简洁的代码结构，易于维护)

(✅ **稳定高效**：使用学习通官方API配合时间欺骗+模拟用户观看,高效完成学习通任务 )


(✅ **多任务**：  使用自定义ThreadPoolExecutor实现多任务)


## 🔥 当前版本功能（Current Features）

📌 **版本号**：v1.0.1 
📌 **发布日期**：2025-04-26

支持多任务同时运行,目前只支持学习通,支持自动完成章节测验！！！！！




## 题库配置

| 参数             | 描述                      | 是否必须       | 示例值                              | Token获取方式                |
|----------------|-------------------------|------------|----------------------------------|--------------------------|
| use            | 你想要使用哪些题库,不填写默认使用所有免费题库 | 否          | local,icodef,buguake,wanneng             |      |
| wannengToken   | 万能付费题库的Token值(10位)      | 否          | E196FD8B49                       | https://lyck6.cn/pay     |
| icodefToken    | Icodef 题库Token值         | 否          | UafYcHViJMGzSVNh                 | 关注微信公众号"一之哥哥"发送"token"获取 |
| enncyToken     | enncy 题库Token值          | 否          | a21ae2403b414b94b512736c30c69940 | https://tk.enncy.cn      |
| aidianYToken   | 爱点题库(亿级题库API)Token值     | 否          | cvor7f3HxZ7nF2M3ljmA             | https://www.51aidian.com |
| lemonToken     | 柠檬题库 Token值             | 否          | 8a3debe92e2ba83d6786e186bef2a424 | https://www.lemtk.xyz    |


````
##application-dev.yml

tiku:
  settings:
    endpoints:
        - name: "icodef"  #示例local,icodef,buguake,wanneng，不填写默认使用所有免费题库
          token: xQtsFM16W6KpXCBt
        - name: "wanneng"
          token:

large:
  model:
    api-key: 123456  # OpenAI兼容API Key
    base-url: http://127.0.0.1:3001/openai-qwen-oauth  # OpenAI兼容API Base URL
    model: qwen3-coder-plus  # 模型名称（可选，默认为qwen3-coder-plus）

````