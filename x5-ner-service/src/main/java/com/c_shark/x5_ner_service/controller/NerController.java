package com.c_shark.x5_ner_service.controller;

import com.c_shark.x5_ner_service.dto.EntityResponse;
import com.c_shark.x5_ner_service.dto.PredictionRequest;
import com.c_shark.x5_ner_service.service.NerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class NerController {

    private final NerService nerService;

    @Autowired
    public NerController(NerService nerService) {
        this.nerService = nerService;
    }

    @PostMapping("/predict")
    public List<EntityResponse> predict(@RequestBody PredictionRequest request) {
        return nerService.getEntities(request.getInput());
    }
}
