package com.zmc.ierbar_web_app.servicies;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.zmc.ierbar_web_app.models.factory.CategoriePlanta;
import com.zmc.ierbar_web_app.models.factory.PlantaFactory;
import com.zmc.ierbar_web_app.models.simple_factory.Planta;
import com.zmc.ierbar_web_app.models.simple_factory.TipPlanta;
import com.zmc.ierbar_web_app.repositories.PlantaRepository;

@Service
public class PlantaService {

    @Autowired
    private AgentAIService agentAIService;

    @Autowired
    private PlantaRepository plantaRepository;

    public List<Planta> gasesteToatePlantele() {
        return plantaRepository.extrageToatePlantele();
    }

    public Planta recunoasteSiAdaugPlanta(MultipartFile file, int userId) throws Exception {
        // 1. Detectare YOLO
        String clasaDetectata = agentAIService.detecteazaPlantaYOLO(file);
        if (clasaDetectata == null || clasaDetectata.isBlank() || clasaDetectata.equalsIgnoreCase("Necunoscuta")) {
            throw new Exception("YOLO nu a putut identifica nicio specie în imagine.");
        }

        // 2. Convertim imaginea în Base64
        String imagineBase64 = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=500";
        if (file != null && !file.isEmpty()) {
            try {
                String mimeType = file.getContentType() != null ? file.getContentType() : "image/jpeg";
                String base64Data = Base64.getEncoder().encodeToString(file.getBytes());
                imagineBase64 = "data:" + mimeType + ";base64," + base64Data;
            } catch (Exception e) {
                System.err.println("Eroare conversie Base64: " + e.getMessage());
            }
        }

        String speciaInRomana = mapeazaNumeSpecie(clasaDetectata);

        // 3. Generăm dinamic descrierea și proprietățile botanice prin Gemini AI
        Map<String, String> detaliiAI = agentAIService.genereazaDetaliiBotanice(speciaInRomana);

        String denumireStiintifica = detaliiAI.getOrDefault("denumire_stiintifica", clasaDetectata);
        String familie = detaliiAI.getOrDefault("familie", "Familie Botanică");
        String descriereAI = detaliiAI.getOrDefault("descriere", "");
        String perioadaInflorire = detaliiAI.getOrDefault("perioada_inflorire", "Primăvară - Vară");
        String cicluViata = detaliiAI.getOrDefault("ciclu_de_viata", "PEREN");

        float inaltime = 0.5f;
        try {
            if (detaliiAI.containsKey("inaltime_maxima")) {
                inaltime = Float.parseFloat(detaliiAI.get("inaltime_maxima").replaceAll("[^0-9.]", ""));
            }
        } catch (Exception ignored) {}

        // Căutăm dacă specia există deja în BD
        List<Planta> planteExistente = plantaRepository.extrageToatePlantele();
        Optional<Planta> potrivire = planteExistente.stream()
                .filter(p -> (p.getNume_uzual() != null && p.getNume_uzual().equalsIgnoreCase(speciaInRomana)) ||
                            (p.getDenumire_stiintifica() != null && clasaDetectata.toLowerCase().contains(p.getDenumire_stiintifica().toLowerCase())))
                .findFirst();

        PlantaFactory factory = new PlantaFactory();

        // Dacă specia există în BD, dăm prioritate descrierii generate dinamic dacă cea din BD e goală/veche
        if (potrivire.isPresent()) {
            Planta plantaOficiala = potrivire.get();
            String descriereExistenta = plantaOficiala.getDescriere();

            if (descriereExistenta == null || descriereExistenta.isBlank() || descriereExistenta.length() < 30) {
                descriereExistenta = descriereAI;
            }

            Planta plantaTemporara = factory.creazaPlanta(
                    plantaOficiala.getCategorie_planta() != null ? plantaOficiala.getCategorie_planta() : CategoriePlanta.FLOARE,
                    userId,
                    plantaOficiala.getNume_uzual(),
                    plantaOficiala.getDenumire_stiintifica(),
                    plantaOficiala.getFamilie(),
                    descriereExistenta,
                    plantaOficiala.getInaltime_maxima(),
                    plantaOficiala.getPerioada_inflorire(),
                    plantaOficiala.getCiclu_de_viata(),
                    plantaOficiala.getTip_planta() != null ? plantaOficiala.getTip_planta() : TipPlanta.ORNAMENTALA,
                    "Nespecificată",
                    imagineBase64,
                    5, "Diverse", "-", "Simplă", false, false, "Erectă",
                    plantaOficiala.isPoate_fi_uscata()
            );
            plantaTemporara.setId(plantaOficiala.getId());
            return plantaTemporara;
        }

        // Specie nouă: returnăm obiectul cu datele dinamice obținute direct de la AI
        Planta plantaPreview = factory.creazaPlanta(
                CategoriePlanta.FLOARE, 
                userId, 
                speciaInRomana,                         
                denumireStiintifica,                         
                familie,                    
                descriereAI, // Descrierea primită dinamic de la Gemini AI
                inaltime, 
                perioadaInflorire, 
                cicluViata, 
                TipPlanta.ORNAMENTALA,                  
                "Nespecificată", 
                imagineBase64, 
                5, "Diverse", "-", "Simplă", false, false, "Erectă", true
        );

        plantaPreview.setId(0); // Păstrăm ID 0 până la apăsarea butonului de salvare
        return plantaPreview;
    }

    private String mapeazaNumeSpecie(String clasaYolo) {
        String input = clasaYolo.toLowerCase().trim().replace("_", " ").replace("-", " ");

        if (input.contains("rose") || input.contains("rosa") || input.contains("trandafir")) {
            return "Trandafir";
        } else if (input.contains("lily") || input.contains("lacramioara")) {
            return "Lăcrămioară";
        } else if (input.contains("dandelion") || input.contains("papadie")) {
            return "Păpădie";
        } else if (input.contains("chamomile") || input.contains("musetel")) {
            return "Mușețel";
        } else if (input.contains("tulip") || input.contains("lalea")) {
            return "Lalea";
        }

        return clasaYolo.substring(0, 1).toUpperCase() + clasaYolo.substring(1);
    }
}