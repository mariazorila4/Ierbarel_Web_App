package com.zmc.ierbar_web_app.servicies;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.zmc.ierbar_web_app.models.factory.CategoriePlanta;
import com.zmc.ierbar_web_app.models.factory.PlantaFactory;
import com.zmc.ierbar_web_app.models.simple_factory.Planta;
import com.zmc.ierbar_web_app.models.simple_factory.TipPlanta;
import com.zmc.ierbar_web_app.repositories.PlantaRepository;

@Service
public class PlantaService {
    private final PlantaRepository plantaRepository;
    private final AgentAIService agentAIService;

    public PlantaService(PlantaRepository plantaRepository, AgentAIService agentAIService) {
        this.plantaRepository = plantaRepository;
        this.agentAIService = agentAIService;
    }

    public List<Planta> obtineIerbarulOnline() {
        return plantaRepository.extrageToatePlantele();
    }

    public Planta recunoasteSiAdaugPlanta(MultipartFile file, int userId) throws Exception {
        String speciaIdentificata = agentAIService.detecteazaPlantaYOLO(file);

        if (speciaIdentificata == null || speciaIdentificata.isEmpty() || speciaIdentificata.equalsIgnoreCase("Necunoscuta")) {
            throw new Exception("YOLO nu a putut recunoaște planta din imagine.");
        }

        PlantaFactory factory = new PlantaFactory();
        
        Planta plantaNoua = factory.creazaPlanta(
            CategoriePlanta.FLOARE, 
            userId, 
            speciaIdentificata,                  // nume_uzual
            "denumire_generata",                 // denumire_stiintifica
            "familie",                           // familie
            "Planta a fost identificata automat prin modulul de Computer Vision YOLO.", // descriere
            50f,                                 // inaltime_maxima
            "Nedefinita",                        // perioada_inflorire
            "PEREN",                             // ciclu_de_viata
            TipPlanta.ORNAMENTALA,               // tip_planta
            "România",                           // locatie
            0,                                   // numar_petale
            "-",                                 // culoare
            "-",                                 // tip_coroana
            "-",                                 // tip_frunza
            false,                               // pom_fructifer
            false,                               // produce_fructe
            "-",                                 // tip_tulpina
            false                                // poate_fi_uscata
        );

        String urlImagineDefault = "https://images.unsplash.com/photo-1628808168235-96bece30fc6e?w=500";
        plantaNoua.setImagine_url(urlImagineDefault);

        plantaRepository.salveazaPlantaNoua(plantaNoua, userId, urlImagineDefault, 0, "-", "-", "-", false, false, "-");

        return plantaNoua;
    }
}