package com.c_shark.x5_ner_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для критериев поиска товаров на основе распознанных сущностей.
 * Соответствует типам из NER-модели: B-BRAND, B-TYPE, B-VOLUME, B-PERCENT
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchCriteria {
    private String brandName;
    private String categoryName;
    private String volume;
    private String percentage;
}
