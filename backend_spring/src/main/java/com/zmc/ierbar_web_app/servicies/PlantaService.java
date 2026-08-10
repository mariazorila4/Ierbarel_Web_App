package com.zmc.ierbar_web_app.servicies;

import com.zmc.ierbar_web_app.models.simple_factory.*;
import com.zmc.ierbar_web_app.models.factory.*;
import com.zmc.ierbar_web_app.repositories.*;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.*;

import java.util.List;
import java.util.Map;

@Service
public class PlantaService {
    private final PlantaRepository plantaRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final String YOLO_SERVER_URL="http://localhost:5000/predict";
    
    public PlantaService(PlantaRepository plantaRepository) {
        this.plantaRepository = plantaRepository;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    
    public List<Planta> obtineIerbarulOnline(){
        return plantaRepository.extrageToatePlantele();
    }

    public Planta recunoasteSiAdaugPlanta(String imagineUrl, int userId) throws Exception{
        Map<String, String> cerereYolo=Map.of("image_url", imagineUrl);
        ResponseEntity<String> raspuns=restTemplate.postForEntity(YOLO_SERVER_URL, cerereYolo, String.class);

        JsonNode jsonYolo=objectMapper.readTree(raspuns.getBody());
        String speciaIdentificata=jsonYolo.path("clasa").asText();

        if(speciaIdentificata==null || speciaIdentificata.isEmpty()){
            throw new Exception("YOLO nu a putut recunoaste planta din imagine");
        }

        PlantaFactory factory=new PlantaFactory();
        Planta plantaNoua=factory.creazaPlanta(CategoriePlanta.FLOARE, 2, speciaIdentificata,
             "denumire_generata", "familie", "descriere", 50f, "speciaIdentificata", "speciaIdentificata", TipPlanta.ORNAMENTALA, 0, "speciaIdentificata", "speciaIdentificata", imagineUrl, false, false, "speciaIdentificata", false);

        plantaRepository.salveazaPlantaNoua(plantaNoua, userId);

        return plantaNoua;
    }
}
