package com.c_shark.x5_ner_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для сущностей из новой ML-модели
 * Формат: {"text_entity": "кефир", "entity": "TYPE"}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityResponse {

    @JsonProperty("text_entity")
    private String textEntity;

    private String entity;

    @JsonProperty("start_index")
    private Integer startIndex;

    @JsonProperty("end_index")
    private Integer endIndex;
}
