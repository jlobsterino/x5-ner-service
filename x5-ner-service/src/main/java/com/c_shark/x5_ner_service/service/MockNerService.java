package com.c_shark.x5_ner_service.service;

import com.c_shark.x5_ner_service.dto.EntityResponse;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Profile("dev")
@Primary
public class MockNerService implements NerService {

    @Override
    public List<EntityResponse> getEntities(String text) {
        System.out.println("MockNerService обрабатывает текст: " + text);
        List<EntityResponse> entities = new ArrayList<>();

        if (text.toLowerCase().contains("молоко")) {
            EntityResponse entity = new EntityResponse();
            entity.setTextEntity("молоко");
            entity.setEntity("TYPE");
            entities.add(entity);
        }

        if (text.toLowerCase().contains("lays") || text.toLowerCase().contains("лейс")) {
            EntityResponse entity = new EntityResponse();
            entity.setTextEntity("Lay's");
            entity.setEntity("BRAND");
            entities.add(entity);
        }

        if (text.toLowerCase().contains("простоквашино")) {
            EntityResponse entity = new EntityResponse();
            entity.setTextEntity("Простоквашино");
            entity.setEntity("BRAND");
            entities.add(entity);
        }

        if (text.toLowerCase().contains("кола") || text.toLowerCase().contains("cola")) {
            EntityResponse entity = new EntityResponse();
            entity.setTextEntity("кола");
            entity.setEntity("TYPE");
            entities.add(entity);
        }

        if (text.contains("1л")) {
            EntityResponse entity = new EntityResponse();
            entity.setTextEntity("1л");
            entity.setEntity("VOLUME");
            entities.add(entity);
        }

        if (text.contains("2л")) {
            EntityResponse entity = new EntityResponse();
            entity.setTextEntity("2л");
            entity.setEntity("VOLUME");
            entities.add(entity);
        }

        if (text.contains("500мл") || text.contains("0.5л")) {
            EntityResponse entity = new EntityResponse();
            entity.setTextEntity("500мл");
            entity.setEntity("VOLUME");
            entities.add(entity);
        }

        if (text.contains("3.2%")) {
            EntityResponse entity = new EntityResponse();
            entity.setTextEntity("3.2%");
            entity.setEntity("PERCENT");
            entities.add(entity);
        }

        if (text.contains("2.5%")) {
            EntityResponse entity = new EntityResponse();
            entity.setTextEntity("2.5%");
            entity.setEntity("PERCENT");
            entities.add(entity);
        }

        if (text.toLowerCase().contains("без сахара") || text.contains("0%")) {
            EntityResponse entity = new EntityResponse();
            entity.setTextEntity("0%");
            entity.setEntity("PERCENT");
            entities.add(entity);
        }

        if (text.toLowerCase().contains("coca") || text.toLowerCase().contains("кока")) {
            EntityResponse entity = new EntityResponse();
            entity.setTextEntity("Coca-Cola");
            entity.setEntity("BRAND");
            entities.add(entity);
        }

        if (text.toLowerCase().contains("pepsi") || text.toLowerCase().contains("пепси")) {
            EntityResponse entity = new EntityResponse();
            entity.setTextEntity("PepsiCo");
            entity.setEntity("BRAND");
            entities.add(entity);
        }

        System.out.println("MockNerService распознал " + entities.size() + " сущностей");
        return entities;
    }
}
