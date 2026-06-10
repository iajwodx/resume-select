package com.resume.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume.config.DeepSeekConfig;
import com.resume.dto.DeepSeekResultDTO;
import com.resume.dto.KeywordMatchResultDTO;
import com.resume.entity.Resume;
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

    private static final String MATCH_SYSTEM_PROMPT = """
            你是一个简历关键词匹配助手。请评估以下简历与给定关键词的匹配程度。

            需要返回的JSON格式：
            - matchScore: 匹配度评分(0-100的整数，100表示完全匹配)
            - matchedTexts: 简历中与关键词匹配的文本片段列表(从简历原文中提取的关键短语，每个片段不超过20个字)

            评分标准：
            - 关键词直接出现在技能或项目经验中：80-100分
            - 关键词语义相关（如同义词、上下位概念、相关技术栈）：50-79分
            - 关键词部分相关：20-49分
            - 关键词完全不相关：0-19分

            注意：matchedTexts中的文本必须来自简历原文，不要编造。

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

    @Override
    public KeywordMatchResultDTO matchKeyword(String keyword, Resume resume) throws Exception {
        // Build resume summary for matching
        StringBuilder resumeInfo = new StringBuilder();
        if (resume.getSkills() != null && !resume.getSkills().isBlank()) {
            resumeInfo.append("技能：").append(resume.getSkills()).append("\n");
        }
        if (resume.getProjectExperience() != null && !resume.getProjectExperience().isBlank()) {
            resumeInfo.append("项目经验：").append(resume.getProjectExperience()).append("\n");
        }
        if (resume.getName() != null) {
            resumeInfo.append("姓名：").append(resume.getName()).append("\n");
        }
        if (resume.getEducation() != null) {
            resumeInfo.append("学历：").append(resume.getEducation()).append("\n");
        }
        if (resume.getWorkYears() != null) {
            resumeInfo.append("工作年限：").append(resume.getWorkYears()).append("年\n");
        }

        String userMessage = "关键词：" + keyword + "\n\n简历信息：\n---\n" + resumeInfo + "\n---\n\n请评估该简历与关键词的匹配程度。";

        Map<String, Object> requestBody = Map.of(
                "model", deepSeekConfig.getModel(),
                "messages", List.of(
                        Map.of("role", "system", "content", MATCH_SYSTEM_PROMPT),
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

        log.info("Calling DeepSeek API to match keyword '{}' for resume id={}", keyword, resume.getId());

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            log.error("DeepSeek API error: status={}, body={}", response.statusCode(), response.body());
            throw new RuntimeException("DeepSeek API call failed with status: " + response.statusCode());
        }

        return parseMatchResponse(response.body());
    }

    @SuppressWarnings("unchecked")
    private KeywordMatchResultDTO parseMatchResponse(String responseBody) throws Exception {
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

        log.info("DeepSeek match result: {}", content);

        return objectMapper.readValue(content, KeywordMatchResultDTO.class);
    }
}
