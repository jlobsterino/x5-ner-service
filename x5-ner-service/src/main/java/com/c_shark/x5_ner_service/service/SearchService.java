package com.c_shark.x5_ner_service.service;

import com.c_shark.x5_ner_service.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final NerService nerService;
    private final ProductService productService;

    public SearchResponse search(String query) {
        log.info("========================================");
        log.info("Поиск по запросу: '{}'", query);
        log.info("========================================");

        List<EntityResponse> entities = new ArrayList<>();
        SearchCriteria criteria;
        String searchQuery = query;

        try {
            entities = nerService.getEntities(query);
            log.info("✓ Распознано сущностей: {}", entities.size());

            for (EntityResponse entity : entities) {
                log.info("  - {}: '{}'", entity.getEntity(), entity.getTextEntity());
            }

            criteria = extractSearchCriteriaFromEntities(entities);

            if (!entities.isEmpty()) {
                StringBuilder combinedText = new StringBuilder();
                for (EntityResponse entity : entities) {
                    if (combinedText.length() > 0) combinedText.append(" ");
                    combinedText.append(entity.getTextEntity());
                }
                searchQuery = combinedText.toString();
                log.info("✓ Используем объединенный текст сущностей: '{}'", searchQuery);
            }

        } catch (ResourceAccessException e) {
            log.warn("  ML-сервис недоступен, использую fallback-поиск");
            criteria = SearchCriteria.builder().build();
        }

        log.info("  Критерии поиска:");
        log.info("  - Бренд: {}", criteria.getBrandName());
        log.info("  - Тип: {}", criteria.getCategoryName());
        log.info("  - Объем: {}", criteria.getVolume());
        log.info("  - Процент: {}", criteria.getPercentage());

        List<ProductDTO> products = productService.searchProductsWithRanking(criteria, searchQuery);
        log.info("  Найдено товаров: {}", products.size());

        SearchResponse response = SearchResponse.builder()
                .originalQuery(query)
                .recognizedEntities(entities)
                .searchCriteria(criteria)
                .products(products)
                .totalCount(products.size())
                .build();

        log.info("========================================");
        return response;
    }

    private SearchCriteria extractSearchCriteriaFromEntities(List<EntityResponse> entities) {
        String brandName = null;
        String categoryName = null;
        String volume = null;
        String percentage = null;

        for (EntityResponse entity : entities) {
            String entityType = entity.getEntity().toUpperCase()
                    .replace("B-", "")
                    .replace("I-", "");

            String text = entity.getTextEntity();

            switch (entityType) {
                case "BRAND":
                    if (brandName == null) {
                        brandName = text;
                    }
                    break;

                case "TYPE":
                    if (categoryName == null) {
                        categoryName = text;
                    }
                    break;

                case "VOLUME":
                    if (volume == null) {
                        volume = text;
                    }
                    break;

                case "PERCENT":
                case "PERCENTAGE":
                    if (percentage == null) {
                        percentage = text;
                    }
                    break;
            }
        }

        return SearchCriteria.builder()
                .brandName(brandName)
                .categoryName(categoryName)
                .volume(volume)
                .percentage(percentage)
                .build();
    }
}
