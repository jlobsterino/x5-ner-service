package com.c_shark.x5_ner_service.service;

import com.c_shark.x5_ner_service.dto.ProductDTO;
import com.c_shark.x5_ner_service.dto.SearchCriteria;
import com.c_shark.x5_ner_service.entity.Product;
import com.c_shark.x5_ner_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private static final int MAX_RESULTS = 20;

    @Cacheable(value = "searchResults", key = "#criteria.toString() + '_' + #originalQuery")
    public List<ProductDTO> searchProductsWithRanking(SearchCriteria criteria, String originalQuery) {
        long startTime = System.currentTimeMillis();
        log.info("Комплексный поиск: критерии={}, запрос='{}'", criteria, originalQuery);

        List<Product> candidateProducts = getCandidateProducts(criteria, originalQuery);

        if (candidateProducts.isEmpty()) {
            log.info("Кандидаты не найдены");
            return Collections.emptyList();
        }

        List<ProductDTO> rankedProducts = candidateProducts.stream()
                .map(product -> {
                    ProductDTO dto = convertToDTO(product);
                    int score = calculateRelevance(product, criteria, originalQuery);
                    return new ScoredProduct(dto, score);
                })
                .filter(sp -> sp.getScore() > 0)
                .sorted(Comparator.comparingInt(ScoredProduct::getScore).reversed())
                .limit(MAX_RESULTS)
                .map(ScoredProduct::getProduct)
                .collect(Collectors.toList());

        long elapsed = System.currentTimeMillis() - startTime;
        log.info("Найдено {} товаров за {}ms", rankedProducts.size(), elapsed);

        return rankedProducts;
    }

    private List<Product> getCandidateProducts(SearchCriteria criteria, String originalQuery) {
        Set<Product> candidates = new HashSet<>();

        if (hasValidCriteria(criteria)) {
            if (criteria.getBrandName() != null && !criteria.getBrandName().isEmpty()) {
                candidates.addAll(productRepository.findByBrandNameContaining(criteria.getBrandName()));
            }
            if (criteria.getCategoryName() != null && !criteria.getCategoryName().isEmpty()) {
                candidates.addAll(productRepository.findByCategoryNameContaining(criteria.getCategoryName()));
                candidates.addAll(productRepository.findByNameContaining(criteria.getCategoryName()));
            }
        }

        if (originalQuery != null && !originalQuery.trim().isEmpty()) {
            candidates.addAll(productRepository.searchProducts(originalQuery.trim()));
        }

        if (candidates.isEmpty()) {
            log.info("📦 Fallback: загружаем все товары");
            return productRepository.findAllInStock();
        }

        return new ArrayList<>(candidates);
    }

    private boolean hasValidCriteria(SearchCriteria criteria) {
        return (criteria.getBrandName() != null && !criteria.getBrandName().isEmpty()) ||
                (criteria.getCategoryName() != null && !criteria.getCategoryName().isEmpty()) ||
                (criteria.getVolume() != null && !criteria.getVolume().isEmpty()) ||
                (criteria.getPercentage() != null && !criteria.getPercentage().isEmpty());
    }

    private int calculateRelevance(Product product, SearchCriteria criteria, String originalQuery) {
        int score = 0;

        if (criteria.getBrandName() != null && product.getBrand() != null) {
            String brandName = product.getBrand().getName().toLowerCase();
            String searchBrand = criteria.getBrandName().toLowerCase();
            if (brandName.equals(searchBrand)) score += 20;
            else if (brandName.contains(searchBrand)) score += 15;
        }

        if (criteria.getCategoryName() != null) {
            String searchType = criteria.getCategoryName().toLowerCase();

            if (product.getName() != null && product.getName().toLowerCase().contains(searchType)) {
                score += 18;
            }

            if (product.getCategory() != null && product.getCategory().getName() != null
                    && product.getCategory().getName().toLowerCase().contains(searchType)) {
                score += 12;
            }
        }

        if (criteria.getVolume() != null && product.getVolume() != null) {
            if (product.getVolume().contains(criteria.getVolume())) score += 10;
        }

        if (criteria.getPercentage() != null && product.getPercentage() != null) {
            if (product.getPercentage().contains(criteria.getPercentage())) score += 8;
        }

        if (originalQuery != null && !originalQuery.isEmpty()) {
            String queryLower = originalQuery.toLowerCase();

            if (product.getName() != null) {
                String nameLower = product.getName().toLowerCase();
                if (nameLower.equals(queryLower)) score += 25;
                else if (nameLower.startsWith(queryLower)) score += 15;
                else if (nameLower.contains(queryLower)) score += 10;
            }
        }

        if (Boolean.TRUE.equals(product.getInStock())) score += 5;

        return score;
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setBrandName(product.getBrand() != null ? product.getBrand().getName() : null);
        dto.setCategoryName(product.getCategory() != null ? product.getCategory().getName() : null);
        dto.setPrice(product.getPrice());
        dto.setVolume(product.getVolume());
        dto.setPercentage(product.getPercentage());
        dto.setDescription(product.getDescription());
        dto.setImageUrl(product.getImageUrl());
        dto.setInStock(product.getInStock());
        return dto;
    }

    @RequiredArgsConstructor
    @lombok.Data
    private static class ScoredProduct {
        private final ProductDTO product;
        private final int score;
    }

    @Cacheable("products")
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAllInStock().stream()
                .limit(50)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }
}
