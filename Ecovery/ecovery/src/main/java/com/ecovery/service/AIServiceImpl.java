package com.ecovery.service;

import com.ecovery.dto.AIPredictionResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIServiceImpl implements AIService {

    @Value("${ai.server.url}")
    private String aiServerUrl;

    private final RestTemplate restTemplate;

    @Override
    public AIPredictionResultDto predictDisposal(MultipartFile imageFile, String regionGu) throws IOException, Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        log.info("Original file name: {}", imageFile.getOriginalFilename());
        log.info("File size: {} bytes", imageFile.getSize());
        log.info("Content type: {}", imageFile.getContentType());

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(imageFile.getBytes()) {
            @Override
            public String getFilename() {
                return imageFile.getOriginalFilename();
            }
        });
        body.add("regionGu", regionGu);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        String apiUrl = aiServerUrl + "/predict";
        log.info("Calling AI server: {}", apiUrl);

        try {
            ResponseEntity<AIPredictionResultDto> response = restTemplate.postForEntity(apiUrl, requestEntity, AIPredictionResultDto.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Prediction result: {}", response.getBody());
                log.info("AI prediction successful. Predicted Class: {}", response.getBody().getPredictedClass());
                return response.getBody();
            } else {
                log.error("AI server returned non-success status or empty body: status={}, body={}",
                        response.getStatusCode(), response.getBody());
                throw new Exception("AI 서버 응답이 유효하지 않습니다. 상태 코드: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            log.error("AI server client error (4xx): status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new Exception("AI 서버 요청 오류: " + e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException e) {
            log.error("AI server internal error (5xx): status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new Exception("AI 서버 내부 오류: " + e.getResponseBodyAsString(), e);
        } catch (ResourceAccessException e) {
            log.error("Failed to connect to AI server: {}", e.getMessage(), e);
            throw new Exception("AI 서버에 연결할 수 없습니다. 서버 상태를 확인하세요.", e);
        } catch (Exception e) { // 그 외 예상치 못한 모든 예외
            log.error("An unexpected error occurred while communicating with AI server: {}", e.getMessage(), e);
            throw new Exception("AI 서버 통신 중 예상치 못한 오류가 발생했습니다.", e);
        }
        // 이 지점에 도달할 경우 (위의 모든 return/throw 문에 도달하지 못했을 때)
        // 예를 들어, IOException은 이 try-catch 블록 밖으로 던져질 수 있지만,
        // 컴파일러는 try-catch 블록이 모든 가능한 RuntimeException을 잡지는 못할 것이라고 판단할 수 있습니다.
        // 따라서 명시적으로 마지막에 예외를 던지는 것이 가장 확실한 해결책입니다.
        // throw new IllegalStateException("AI 서비스 처리 중 알 수 없는 상태 발생");
    }
}