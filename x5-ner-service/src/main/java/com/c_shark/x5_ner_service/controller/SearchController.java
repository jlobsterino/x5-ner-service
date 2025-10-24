package com.c_shark.x5_ner_service.controller;

import com.c_shark.x5_ner_service.dto.SearchRequest;
import com.c_shark.x5_ner_service.dto.SearchResponse;
import com.c_shark.x5_ner_service.dto.ProductDTO;
import com.c_shark.x5_ner_service.service.SearchService;
import com.c_shark.x5_ner_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class SearchController {

    private final SearchService searchService;
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<SearchResponse> search(@RequestBody SearchRequest request) {
        log.info("Получен поисковый запрос: {}", request.getQuery());

        if (request.getQuery() == null || request.getQuery().trim().isEmpty()) {
            log.warn("Пустой поисковый запрос");
            return ResponseEntity.badRequest().build();
        }

        try {
            SearchResponse response = searchService.search(request.getQuery());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Ошибка при обработке поискового запроса: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        log.info("Запрос всех товаров");
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        log.info("Запрос товара с ID: {}", id);
        ProductDTO product = productService.getProductById(id);

        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(product);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Search service is running");
    }
}
