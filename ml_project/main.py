import time
import spacy
import re
import json
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List
import logging
import uvicorn
from pyaspeller import YandexSpeller

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

app = FastAPI(title="X5 NER API v2.0")

nlp_model = None
speller = None
MODEL_PATH = "my_ner_model_new"

REGEX_PATTERNS = {
    'VOLUME': re.compile(
        r'(\d+(?:[.,]\d+)?)\s*(г|кг|мл|л|литр|литра|литров|грамм|грамма|граммов|килограмм|килограмма|килограммов|миллилитр|миллилитра|миллилитров|шт|штук|штука|банка|банок|банке|пачка|пачек|упаковка)\b',
        re.IGNORECASE | re.UNICODE
    ),
    'PERCENT': re.compile(
        r'(\d+(?:[.,]\d+)?)\s*%',
        re.IGNORECASE | re.UNICODE
    )
}

class PredictionRequest(BaseModel):
    input: str

class EntityResponse(BaseModel):
    text_entity: str
    entity: str

class HealthCheckResponse(BaseModel):
    status: str
    message: str

@app.on_event("startup")
async def load_models():
    global nlp_model, speller
    try:
        logger.info(f"Загрузка NER модели из {MODEL_PATH}...")
        nlp_model = spacy.load(MODEL_PATH)
        logger.info("NER модель успешно загружена!")
        
        speller = YandexSpeller()
        
        logger.info("Прогрев модели...")
        test_doc = nlp_model("тестовый запрос")
        logger.info(f"Система готова к работе!")
        
    except Exception as e:
        logger.error(f"Ошибка при загрузке: {e}")

def correct_spelling(text):
    try:
        if speller:
            corrected = speller.spelled(text)
            if corrected != text:
                logger.info(f"Исправлено: '{text}' -> '{corrected}'")
            return corrected
        return text
    except Exception as e:
        logger.warning(f"Ошибка проверки орфографии: {e}")
        return text

def merge_bio_entities(doc):
    entities = []
    for ent in doc.ents:
        entities.append({
            'text_entity': ent.text,
            'entity': ent.label_,
            'start': ent.start_char,
            'end': ent.end_char
        })
    return entities

def extract_regex_entities(text):
    regex_entities = []
    for entity_type, pattern in REGEX_PATTERNS.items():
        for match in pattern.finditer(text):
            regex_entities.append({
                'text_entity': match.group(0),
                'entity': entity_type,
                'start': match.start(),
                'end': match.end()
            })
    return regex_entities

def merge_entities(ner_entities, regex_entities):
    all_entities = []
    
    for ent in regex_entities:
        all_entities.append(ent)
    
    for ner_ent in ner_entities:
        overlaps = False
        for regex_ent in regex_entities:
            if not (ner_ent['end'] <= regex_ent['start'] or
                    ner_ent['start'] >= regex_ent['end']):
                overlaps = True
                break
        if not overlaps:
            all_entities.append(ner_ent)
    
    all_entities.sort(key=lambda x: x['start'])
    return all_entities

def merge_consecutive_same_type(entities):
    if not entities:
        return entities
    
    merged = []
    current = entities[0].copy()
    
    for i in range(1, len(entities)):
        next_ent = entities[i]
        gap = next_ent['start'] - current['end']
        
        if next_ent['entity'] == current['entity'] and gap <= 3:
            current['text_entity'] += ' ' + next_ent['text_entity']
            current['end'] = next_ent['end']
        else:
            merged.append(current)
            current = next_ent.copy()
    
    merged.append(current)
    return merged

def process_query(query_text):
    corrected_query = correct_spelling(query_text)
    
    doc = nlp_model(corrected_query)
    ner_entities = merge_bio_entities(doc)
    
    regex_entities = extract_regex_entities(corrected_query)
    
    all_entities = merge_entities(ner_entities, regex_entities)
    all_entities = merge_consecutive_same_type(all_entities)
    
    final_entities = [
        {'text_entity': ent['text_entity'], 'entity': ent['entity']}
        for ent in all_entities
    ]
    
    return {
        'original_query': query_text,
        'corrected_query': corrected_query,
        'entities': final_entities
    }

@app.get("/", response_model=HealthCheckResponse)
async def root():
    if nlp_model is None:
        return {"status": "error", "message": "Модель не загружена"}
    return {"status": "ok", "message": "NER API v2.0 is running"}

@app.post("/api/predict")
async def predict(request: PredictionRequest):
    start_time = time.time()
    
    try:
        if nlp_model is None:
            raise HTTPException(status_code=503, detail="Модель не загружена")

        input_text = request.input.strip()
        
        if not input_text:
            raise HTTPException(status_code=400, detail="Пустой запрос")

        logger.info(f"Обработка: '{input_text}'")

        result = process_query(input_text)
        
        elapsed = time.time() - start_time
        logger.info(f"   Обработано за {elapsed:.3f}с")
        logger.info(f"   Исправлено: '{result['corrected_query']}'")
        logger.info(f"   Найдено сущностей: {len(result['entities'])}")
        
        for ent in result['entities']:
            logger.info(f"   - {ent['entity']}: '{ent['text_entity']}'")
        
        return result['entities']

    except HTTPException as he:
        raise he
    except Exception as e:
        logger.error(f"Ошибка: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=f"Ошибка обработки: {str(e)}")

@app.get("/stats")
async def get_stats():
    if nlp_model is None:
        return {"status": "error", "message": "Модель не загружена"}
    
    return {
        "status": "ok",
        "model_loaded": nlp_model is not None,
        "speller_loaded": speller is not None,
        "model_path": MODEL_PATH
    }

# --- Запуск ---
if __name__ == "__main__":
    logger.info("  Запуск NER API")
    uvicorn.run(
        app, 
        host="0.0.0.0", 
        port=8000,
        log_level="info"
    )
