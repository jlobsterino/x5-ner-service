package com.c_shark.x5_ner_service.controller;

import com.c_shark.x5_ner_service.dto.EntityResponse;
import com.c_shark.x5_ner_service.service.NerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@Controller
public class WebController {

    private final NerService nerService;

    public WebController(NerService nerService) {
        this.nerService = nerService;
    }

    @GetMapping("/")
    public String showSearchPage(Model model) {
        model.addAttribute("entities", Collections.emptyList());
        return "search";
    }

    @PostMapping("/search")
    public String performSearch(@RequestParam("query") String query, Model model) {
        List<EntityResponse> entities = nerService.getEntities(query);

        model.addAttribute("entities", entities);
        model.addAttribute("lastQuery", query);

        return "search";
    }
}

