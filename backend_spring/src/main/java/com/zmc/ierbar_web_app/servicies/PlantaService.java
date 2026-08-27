package com.zmc.ierbar_web_app.servicies;

import java.util.Base64;
import java.util.List;
import java.util.Map;

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

    /**
     * Identifică specia din imagine (YOLO + Gemini AI) și generează dinamic
     * un obiect de previzualizare (fără salvare în Baza de Date).
     */
    public Planta recunoastePlanta(MultipartFile file, int userId) throws Exception {
        // 1. Detectare specie prin YOLO
        String clasaDetectata = agentAIService.detecteazaPlantaYOLO(file);
        
        // 💡 MODIFICARE AICI: Dacă YOLO dă greș, nu mai dăm crash aplicației!
        // Trecem mai departe cu un nume generic, pe care Gemini îl va trata ca atare.
        if (clasaDetectata == null || clasaDetectata.isBlank() || clasaDetectata.equalsIgnoreCase("Necunoscuta")) {
            clasaDetectata = "Plantă Necunoscută";
        }

        // 2. Conversie imagine în Base64
        String imagineBase64 = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=500";
        if (file != null && !file.isEmpty()) {
            try {
                String mimeType = file.getContentType() != null ? file.getContentType() : "image/jpeg";
                String base64Data = Base64.getEncoder().encodeToString(file.getBytes());
                imagineBase64 = "data:" + mimeType + ";base64," + base64Data;
            } catch (Exception e) {}
        }

        String speciaInRomana = mapeazaNumeSpecie(clasaDetectata);

        // 3. Extragere dinamică a TUTUROR detaliilor botanice generate de Gemini AI
        Map<String, String> detaliiAI = agentAIService.genereazaDetaliiBotanice(speciaInRomana);

        String denumireStiintifica = detaliiAI.getOrDefault("denumire_stiintifica", speciaInRomana + " spp.");
        String familie = detaliiAI.getOrDefault("familie", "Familie Botanică");
        String descriereAI = detaliiAI.getOrDefault("descriere", "Specie identificată automat.");
        String perioadaInflorire = detaliiAI.getOrDefault("perioada_inflorire", "Nespecificată");
        String cicluViata = detaliiAI.getOrDefault("ciclu_de_viata", "PEREN");
        
        CategoriePlanta categorie = parseazaCategorie(detaliiAI.getOrDefault("categorie_planta", "FLOARE"));
        TipPlanta tipPlanta = parseazaTip(detaliiAI.getOrDefault("tip_planta", "ORNAMENTALA"));

        int nrPetale = parseazaInt(detaliiAI.get("numar_petale"), 0);
        String culoare = detaliiAI.getOrDefault("culoare", "Diverse");
        String tipCoroana = detaliiAI.getOrDefault("tip_coroana", "Nespecificată");
        String tipFrunza = detaliiAI.getOrDefault("tip_frunza", "Nespecificată");
        String tipTulpina = detaliiAI.getOrDefault("tip_tulpina", "Erectă");
        boolean pomFructifer = parseazaBoolean(detaliiAI.get("pom_fructifer"));
        boolean produceFructe = parseazaBoolean(detaliiAI.get("produce_fructe"));
        boolean poateFiUscata = parseazaBoolean(detaliiAI.get("poate_fi_uscata"));

        float inaltime = 0.5f;
        try {
            if (detaliiAI.containsKey("inaltime_maxima")) {
                inaltime = Float.parseFloat(detaliiAI.get("inaltime_maxima").replaceAll("[^0-9.]", ""));
            }
        } catch (Exception ignored) {}

        PlantaFactory factory = new PlantaFactory();

        // 4. Creăm Obiectul Preview pentru frontend (ID = 0, FĂRĂ BAZĂ DE DATE încă!)
        Planta plantaPreview = factory.creazaPlanta(
                categorie, userId, 
                detaliiAI.getOrDefault("nume_uzual", speciaInRomana),                         
                denumireStiintifica,                         
                familie,                    
                descriereAI,
                inaltime, 
                perioadaInflorire, 
                cicluViata, 
                tipPlanta,                  
                "Nespecificată", 
                imagineBase64, 
                nrPetale, culoare, tipCoroana, tipFrunza, pomFructifer, produceFructe, tipTulpina, poateFiUscata
        );

        plantaPreview.setId(0); 
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

    private CategoriePlanta parseazaCategorie(String val) {
        try { return CategoriePlanta.valueOf(val.toUpperCase().trim()); }
        catch (Exception e) { return CategoriePlanta.FLOARE; }
    }

    private TipPlanta parseazaTip(String val) {
        try { return TipPlanta.valueOf(val.toUpperCase().trim()); }
        catch (Exception e) { return TipPlanta.ORNAMENTALA; }
    }

    private int parseazaInt(String val, int defaultVal) {
        try { return Integer.parseInt(val.replaceAll("[^0-9]", "")); }
        catch (Exception e) { return defaultVal; }
    }

    private boolean parseazaBoolean(String val) {
        if (val == null) return false;
        String s = val.toLowerCase().trim();
        return s.equals("true") || s.equals("da") || s.equals("1");
    }
}