package com.c_shark.x5_ner_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String name;
    private String brandName;
    private String categoryName;
    private BigDecimal price;
    private String volume;
    private String percentage;
    private String description;
    private String imageUrl;
    private Boolean inStock;
}
