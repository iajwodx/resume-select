package com.resume.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume.config.DeepSeekConfig;
import com.resume.dto.DeepSeekResultDTO;
import com.resume.service.DeepSeekService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Implementation of DeepSeekService using Java HttpClient.
 */
@Service
public class DeepSeekServiceImpl implements DeepSeekService {

    private static final Logger log = LoggerFactory.getLogger(DeepSeekServiceImpl.class);

    private static final String SYSTEM_PROMPT = """
            你是一个简历分析助手。请从以下简历文本中提取结构化信息，严格以JSON格式返回。
            如果某个字段无法从简历中提取，则设为null。

            需要提取的字段：
            - name: 姓名
            - contact: 联系方式(手机号/邮箱，多个用逗号分隔)
            - expectedLocations: 期望工作地点(多个用逗号分隔)
            - workYears: 工作年限(整数，如3表示3年)
            - education: 学历(博士/硕士/本科/大专/高中及以下)
            - salaryMin: 期望月薪下限(单位：千元，如15表示15K)
            - salaryMax: 期望月薪上限(单位：千元)
            - skills: 技能(保留原文关键信息)
            - projectExperience: 项目经验(精简总结，保留核心内容)
            - jobStatus: 求职状态(在职/离职/随时到岗，无法判断则为null)

            请仅返回JSON，不要添加markdown代码块标记或其他内容。
            """;

    private final DeepSeekConfig deepSeekConfig;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public DeepSeekServiceImpl(DeepSeekConfig deepSeekConfig, ObjectMapper objectMapper) {
        this.deepSeekConfig = deepSeekConfig;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    @Override
    public DeepSeekResultDTO analyzeResume(String resumeText) throws Exception {
        String userMessage = "简历文本：\n---\n" + resumeText + "\n---\n\n请提取上述简历的结构化信息。";

        Map<String, Object> requestBody = Map.of(
                "model", deepSeekConfig.getModel(),
                "messages", List.of(
                        Map.of("role", "system", "content", SYSTEM_PROMPT),
                        Map.of("role", "user", "content", userMessage)
                ),
                "temperature", 0.1
        );

        String requestJson = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(deepSeekConfig.getApiUrl()))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + deepSeekConfig.getApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .timeout(Duration.ofSeconds(60))
                .build();

        log.info("Calling DeepSeek API to analyze resume");

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            log.error("DeepSeek API error: status={}, body={}", response.statusCode(), response.body());
            throw new RuntimeException("DeepSeek API call failed with status: " + response.statusCode());
        }

        return parseDeepSeekResponse(response.body());
    }

    @SuppressWarnings("unchecked")
    private DeepSeekResultDTO parseDeepSeekResponse(String responseBody) throws Exception {
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);

        List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
        if (choices == null || choices.isEmpty()) {
            throw new RuntimeException("No choices returned from DeepSeek API");
        }

        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        String content = (String) message.get("content");

        // Clean potential markdown code block markers
        content = content.trim();
        if (content.startsWith("```json")) {
            content = content.substring(7);
        } else if (content.startsWith("```")) {
            content = content.substring(3);
        }
        if (content.endsWith("```")) {
            content = content.substring(0, content.length() - 3);
        }
        content = content.trim();

        log.info("DeepSeek analysis result: {}", content);

        return objectMapper.readValue(content, DeepSeekResultDTO.class);
    }
}
