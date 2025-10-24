package com.c_shark.x5_ner_service.service;

import com.c_shark.x5_ner_service.dto.EntityResponse;
import com.c_shark.x5_ner_service.dto.PredictionRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
@Profile("prod")
@Slf4j
public class RemoteNerServiceProxy implements NerService {

    private final RestTemplate restTemplate;

    @Value("${ml.service.url:http://localhost:8000/api/predict}")
    private String mlServiceUrl;

    public RemoteNerServiceProxy() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    @Cacheable(value = "nerResults", key = "#text.toLowerCase()", unless = "#result == null || #result.isEmpty()")
    public List<EntityResponse> getEntities(String text) {
        try {
            log.info("Запрос к ML: '{}'", text);

            PredictionRequest request = new PredictionRequest();
            request.setInput(text);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<PredictionRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<List<EntityResponse>> response = restTemplate.exchange(
                    mlServiceUrl,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<List<EntityResponse>>() {}
            );

            List<EntityResponse> entities = response.getBody();

            if (entities != null && !entities.isEmpty()) {
                log.info("Получено сущностей: {}", entities.size());
                return entities;
            }

            log.warn("ML-сервис вернул пустой список");
            return Collections.emptyList();

        } catch (Exception e) {
            log.error("Ошибка ML-сервиса: {}", e.getMessage());
            throw new RuntimeException("Ошибка при вызове ML-сервиса", e);
        }
    }
}
