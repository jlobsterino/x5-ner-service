package com.c_shark.x5_ner_service.service;

import com.c_shark.x5_ner_service.dto.EntityResponse;
import java.util.List;


public interface NerService {

    List<EntityResponse> getEntities(String text);


}
