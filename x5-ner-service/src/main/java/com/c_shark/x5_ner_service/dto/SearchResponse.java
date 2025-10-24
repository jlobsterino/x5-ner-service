package com.c_shark.x5_ner_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResponse {
    private String originalQuery;
    private List<EntityResponse> recognizedEntities;
    private SearchCriteria searchCriteria;
    private List<ProductDTO> products;
    private int totalCount;
}
