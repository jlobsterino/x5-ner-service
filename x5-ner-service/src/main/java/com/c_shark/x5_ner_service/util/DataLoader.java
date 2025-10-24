package com.c_shark.x5_ner_service.util;

import com.c_shark.x5_ner_service.entity.Brand;
import com.c_shark.x5_ner_service.entity.Category;
import com.c_shark.x5_ner_service.entity.Product;
import com.c_shark.x5_ner_service.repository.BrandRepository;
import com.c_shark.x5_ner_service.repository.CategoryRepository;
import com.c_shark.x5_ner_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() > 0) {
            log.info("База данных уже содержит товары. Пропускаем загрузку.");
            return;
        }

        log.info("Начинаем загрузку данных из CSV...");
        loadDataFromCSV();
        log.info("Загрузка завершена! Загружено товаров: {}", productRepository.count());
    }

    private void loadDataFromCSV() {
        try {
            ClassPathResource resource = new ClassPathResource("products_data.csv");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
            );

            String line;
            boolean isFirstLine = true;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                try {
                    String[] data = parseCsvLine(line);

                    if (data.length < 9) {
                        log.warn("Строка {} содержит недостаточно данных, пропускаем", lineNumber);
                        continue;
                    }

                    Brand brand = brandRepository.findByNameIgnoreCase(data[1].trim())
                            .orElseGet(() -> {
                                Brand newBrand = new Brand();
                                newBrand.setName(data[1].trim());
                                return brandRepository.save(newBrand);
                            });

                    Category category = categoryRepository.findByNameIgnoreCase(data[2].trim())
                            .orElseGet(() -> {
                                Category newCategory = new Category();
                                newCategory.setName(data[2].trim());
                                return categoryRepository.save(newCategory);
                            });

                    Product product = new Product();
                    product.setName(data[0].trim());
                    product.setBrand(brand);
                    product.setCategory(category);
                    product.setPrice(new BigDecimal(data[3].trim()));
                    product.setVolume(data[4].trim());
                    product.setPercentage(data[5].trim());
                    product.setDescription(data[6].trim());
                    product.setImageUrl(data[7].trim());
                    product.setInStock(Boolean.parseBoolean(data[8].trim()));

                    productRepository.save(product);

                } catch (Exception e) {
                    log.error("Ошибка при обработке строки {}: {}", lineNumber, e.getMessage());
                }
            }

            reader.close();

        } catch (Exception e) {
            log.error("Ошибка при загрузке данных из CSV: {}", e.getMessage());
            throw new RuntimeException("Failed to load data from CSV", e);
        }
    }

    private String[] parseCsvLine(String line) {
        String[] result = new String[9];
        int fieldIndex = 0;
        StringBuilder currentField = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                insideQuotes = !insideQuotes;
            } else if (c == ',' && !insideQuotes) {
                result[fieldIndex++] = currentField.toString();
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }

        if (fieldIndex < 9) {
            result[fieldIndex] = currentField.toString();
        }

        return result;
    }
}
