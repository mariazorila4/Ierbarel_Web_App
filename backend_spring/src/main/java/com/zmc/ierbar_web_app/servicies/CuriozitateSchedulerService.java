package com.zmc.ierbar_web_app.servicies;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.zmc.ierbar_web_app.models.simple_factory.Planta;
import com.zmc.ierbar_web_app.repositories.PlantaRepository;

@Service
public class CuriozitateSchedulerService {

    @Autowired
    private PlantaRepository plantaRepository;

    @Autowired
    private AgentAIService agentAIService;

    // Rulează automat în fiecare zi la miezul nopții (00:00)
    @Scheduled(cron = "0 0 0 * * ?")
    public void genereazaCuriozitateZilnica() {
        genereazaSiSalveazaCuriozitate();
    }

    public void genereazaSiSalveazaCuriozitate() {
        List<Planta> planteGlobal = plantaRepository.extrageToatePlantele();
        if (planteGlobal.isEmpty()) return;

        // 1. Alege o plantă aleatorie
        Random random = new Random();
        Planta plantaAleasa = planteGlobal.get(random.nextInt(planteGlobal.size()));

        // 2. Extrage curiozitățile deja existente pentru această plantă
        List<String> istoricVechi = plantaRepository.extrageIstoricCuriozitatiPlanta(plantaAleasa.getId());

        // 3. Generează o curiozitate NOUĂ prin Gemini AI
        String curiozitateText = agentAIService.genereazaCuriozitateInedita(plantaAleasa.getNume_uzual(), istoricVechi);

        // 4. Pregătește titlul și iconița
        String titlu = "Știai că... (" + plantaAleasa.getNume_uzual() + ")?";
        String iconita = alegeIconitaPlanta(plantaAleasa.getNume_uzual());

        // 5. Salvează în baza de date
        plantaRepository.salveazaCuriozitate(plantaAleasa.getId(), titlu, curiozitateText, iconita);
    }

    private String alegeIconitaPlanta(String numePlanta) {
        String nume = numePlanta.toLowerCase();
        if (nume.contains("trandafir") || nume.contains("floare")) return "🌹";
        if (nume.contains("lalea")) return "🌷";
        if (nume.contains("papadie") || nume.contains("musetel")) return "🌼";
        if (nume.contains("copac") || nume.contains("stejar") || nume.contains("brad")) return "🌲";
        if (nume.contains("bambus")) return "🎍";
        if (nume.contains("fruct") || nume.contains("măr") || nume.contains("cireș")) return "🍎";
        return "🌿";
    }
}