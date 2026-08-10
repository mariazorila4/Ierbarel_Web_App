package com.zmc.ierbar_web_app.controllers;

import com.zmc.ierbar_web_app.models.factory.*;
import com.zmc.ierbar_web_app.models.simple_factory.*;
import com.zmc.ierbar_web_app.repositories.PlantaRepository;
import com.zmc.ierbar_web_app.servicies.PlantaService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/plante")
@CrossOrigin(origins="*")
public class PlantaController {
    private final PlantaRepository plantaRepository;
    private final PlantaService plantaService;

    public PlantaController(PlantaRepository plantaRepository, PlantaService plantaService) {
        this.plantaRepository = plantaRepository;
        this.plantaService=plantaService;
    }

    @GetMapping
    public List<Planta> getToatePlantele(){
        return plantaRepository.extrageToatePlantele();
    }

    @PostMapping("/admin/{adminId}")
    public String adaugaPlantaNoua(@PathVariable int adminId, @RequestBody Map<String, String> datePlanta){
        String categorie=datePlanta.get("categorie_planta");
        String numeUzual=datePlanta.get("nume_uzual");
        String denumireStiintifica=datePlanta.get("denumire_stiintifica");
        String familie=datePlanta.get("familie");
        String descriere=datePlanta.get("descriere");
        String inaltimeMaxima=datePlanta.get("inaltime_maxima");
        String perioadaInflorire=datePlanta.get("perioada_inflorire");
        String poateFiUscata=datePlanta.get("poate_fi_uscata");
        String cicluDeViata=datePlanta.get("ciclu_de_viata");
        String tipPlanta=datePlanta.get("tip_planta");
        String nrPetale=datePlanta.get("numar_petale");
        String culoare=datePlanta.get("culoare");
        String tipCoroana=datePlanta.get("tip_coroana");
        String tipFrunza=datePlanta.get("tip_frunza");
        String pomFructifer=datePlanta.get("pom_fructifer");
        String produceFructe=datePlanta.get("produce_fructe");
        String tipTulpina=datePlanta.get("tip_tulpina");

        PlantaFactory factory=new PlantaFactory();
        Planta plantaNoua=factory.creazaPlanta(CategoriePlanta.valueOf(categorie), adminId, numeUzual, denumireStiintifica, familie, descriere,
                        Float.parseFloat(inaltimeMaxima), perioadaInflorire, cicluDeViata, TipPlanta.valueOf(tipPlanta),
                        Integer.parseInt(nrPetale), culoare, tipCoroana, tipFrunza, Boolean.parseBoolean(pomFructifer), Boolean.parseBoolean(produceFructe), tipTulpina, Boolean.parseBoolean(poateFiUscata));
        plantaNoua.setNume_uzual(numeUzual);
        plantaNoua.setDenumire_stiintifica(denumireStiintifica);
        plantaNoua.setFamilie(familie);
        plantaNoua.setDescriere(descriere);
        plantaNoua.setInaltime_maxima(Float.parseFloat(inaltimeMaxima));
        plantaNoua.setPerioada_inflorire(perioadaInflorire);
        plantaNoua.setPoate_fi_uscata(Boolean.parseBoolean(poateFiUscata));
        plantaNoua.setCiclu_de_viata(cicluDeViata);
        plantaNoua.setTip_planta(TipPlanta.valueOf(tipPlanta));

        plantaRepository.salveazaPlantaNoua(plantaNoua, adminId);

        return "Planta a fost adaugata cu succes in ierbarul online.";
    }

    @PostMapping("/scaneaza")
    public ResponseEntity<?> scaneazaImagineYOLO(@RequestBody Map<String,String> payload){
        try{
            String urlImagine=payload.get("image_url");
            int userId=Integer.parseInt(payload.get("user_id"));

            Planta plantaRecunoscuta=plantaService.recunoasteSiAdaugPlanta(urlImagine, userId);

            return ResponseEntity.ok(plantaRecunoscuta);
        }catch(Exception e){
            return ResponseEntity.badRequest().body("Eroare la procesarea YOLO: "+e.getMessage());
        }
    }
}