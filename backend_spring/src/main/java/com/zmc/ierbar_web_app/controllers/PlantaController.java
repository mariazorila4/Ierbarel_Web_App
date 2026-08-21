package com.zmc.ierbar_web_app.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.zmc.ierbar_web_app.models.factory.CategoriePlanta;
import com.zmc.ierbar_web_app.models.factory.PlantaFactory;
import com.zmc.ierbar_web_app.models.simple_factory.Planta;
import com.zmc.ierbar_web_app.models.simple_factory.TipPlanta;
import com.zmc.ierbar_web_app.models.user.General;
import com.zmc.ierbar_web_app.repositories.PlantaRepository;
import com.zmc.ierbar_web_app.repositories.UserRepository;
import com.zmc.ierbar_web_app.servicies.PlantaService;

@RestController
@RequestMapping("/api/plante")
@CrossOrigin(origins={"http://localhost:5173", "http://127.0.0.1:5173"})
public class PlantaController {
    private final PlantaRepository plantaRepository;
    private final UserRepository userRepository;
    private final PlantaService plantaService;

    public PlantaController(PlantaRepository plantaRepository, UserRepository userRepository, PlantaService plantaService) {
        this.plantaRepository = plantaRepository;
        this.userRepository = userRepository;
        this.plantaService = plantaService;
    }

    @GetMapping
    public List<Planta> getToatePlantele(){
        return plantaRepository.extrageToatePlantele();
    }

    @PostMapping("/admin/{adminId}")
    public ResponseEntity<String> adaugaPlantaNoua(@PathVariable int adminId, @RequestBody Map<String, String> datePlanta){
        try {
            String categorie = datePlanta.get("categorie_planta");
            String numeUzual = datePlanta.get("nume_uzual");
            String denumireStiintifica = datePlanta.get("denumire_stiintifica");
            String familie = datePlanta.get("familie");
            String descriere = datePlanta.get("descriere");
            String inaltimeMaxima = datePlanta.get("inaltime_maxima");
            String perioadaInflorire = datePlanta.get("perioada_inflorire");
            String poateFiUscata = datePlanta.get("poate_fi_uscata");
            String cicluDeViata = datePlanta.get("ciclu_de_viata");
            String tipPlanta = datePlanta.get("tip_planta");
            String locatie=datePlanta.get("locatie");
            String imagineUrl = datePlanta.get("imagine_url");
            String nrPetale = datePlanta.get("numar_petale");
            String culoare = datePlanta.get("culoare");
            String tipCoroana = datePlanta.get("tip_coroana");
            String tipFrunza = datePlanta.get("tip_frunza");
            String pomFructifer = datePlanta.get("pom_fructifer");
            String produceFructe = datePlanta.get("produce_fructe");
            String tipTulpina = datePlanta.get("tip_tulpina");

            PlantaFactory factory = new PlantaFactory();
            
            Planta plantaNoua = factory.creazaPlanta(CategoriePlanta.valueOf(categorie), adminId, numeUzual, denumireStiintifica, familie, descriere,
                            Float.parseFloat(inaltimeMaxima), perioadaInflorire, cicluDeViata, TipPlanta.valueOf(tipPlanta), locatie, imagineUrl,
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
            plantaNoua.setLocatie(locatie);
            plantaNoua.setImagine_url(imagineUrl);

            plantaRepository.salveazaPlantaNoua(
            plantaNoua, 
            adminId, 
            imagineUrl, 
            Integer.parseInt(nrPetale), 
            culoare, 
            tipCoroana, 
            tipFrunza, 
            Boolean.parseBoolean(pomFructifer), 
            Boolean.parseBoolean(produceFructe), 
            tipTulpina);

            return ResponseEntity.ok("Planta a fost adaugata cu succes in ierbarul online.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Eroare la adăugarea plantei: " + e.getMessage());
        }
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<String> stergePlanta(@PathVariable("id") int id){
        try{
            plantaRepository.stergePlantaDefinitiv(id);
            return ResponseEntity.ok("Planta a fost stearsa cu succes!");
        }catch(Exception e){
            return ResponseEntity.badRequest().body("Eroare la stergere: "+e.getMessage());
        }
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<String> editeazaPlanta(@PathVariable("id") int id, @RequestBody Map<String, String> datePlanta) {
        try {
            String categorie = datePlanta.get("categorie_planta");
            String numeUzual = datePlanta.get("nume_uzual");
            String denumireStiintifica = datePlanta.get("denumire_stiintifica");
            String familie = datePlanta.get("familie");
            String descriere = datePlanta.get("descriere");
            String locatie = datePlanta.get("locatie");
            String imagineUrl = datePlanta.get("imagine_url");
            float inaltime = Float.parseFloat(datePlanta.get("inaltime_maxima"));
            String perioadaInflorire = datePlanta.get("perioada_inflorire");
            boolean poateFiUscata = Boolean.parseBoolean(datePlanta.get("poate_fi_uscata"));
            String cicluViata = datePlanta.get("ciclu_de_viata");
            TipPlanta tipPlanta = TipPlanta.valueOf(datePlanta.get("tip_planta"));
            int nrPetale = Integer.parseInt(datePlanta.getOrDefault("numar_petale", "0"));
            String culoare = datePlanta.getOrDefault("culoare", "-");
            String tipCoroana = datePlanta.getOrDefault("tip_coroana", "-");
            String tipFrunza = datePlanta.getOrDefault("tip_frunza", "-");
            boolean pomFructifer = Boolean.parseBoolean(datePlanta.getOrDefault("pom_fructifer", "false"));
            boolean produceFructe = Boolean.parseBoolean(datePlanta.getOrDefault("produce_fructe", "false"));
            String tipTulpina = datePlanta.getOrDefault("tip_tulpina", "-");

            PlantaFactory fabrica = new PlantaFactory();
            Planta plantaActualizata = fabrica.creazaPlanta(CategoriePlanta.valueOf(categorie), id, numeUzual, 
                    denumireStiintifica, familie, descriere, inaltime, perioadaInflorire, cicluViata, tipPlanta, 
                    locatie, imagineUrl, nrPetale, culoare, tipCoroana, tipFrunza, pomFructifer, produceFructe, 
                    tipTulpina, poateFiUscata);

            plantaRepository.actualizeazaPlanta(id, plantaActualizata, locatie, imagineUrl, nrPetale, culoare, 
                    tipCoroana, tipFrunza, pomFructifer, produceFructe, tipTulpina);

            return ResponseEntity.ok("Planta a fost actualizată cu succes!");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Eroare la actualizarea plantei: " + e.getMessage());
        }
    }


    @PostMapping(value = "/scaneaza", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> scaneazaImagineYOLO(@RequestParam("file") MultipartFile file,
                                                 @RequestParam("user_id") int userId){

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<String> stergePlanta(@PathVariable("id") int id){
        try{
            plantaRepository.stergePlantaDefinitiv(id);
            return ResponseEntity.ok("Planta a fost stearsa cu succes!");
        }catch(Exception e){
            return ResponseEntity.badRequest().body("Eroare la stergere: "+e.getMessage());
        }
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<String> editeazaPlanta(@PathVariable("id") int id, @RequestBody Map<String, String> datePlanta) {
        try {
            String categorie = datePlanta.get("categorie_planta");
            String numeUzual = datePlanta.get("nume_uzual");
            String denumireStiintifica = datePlanta.get("denumire_stiintifica");
            String familie = datePlanta.get("familie");
            String descriere = datePlanta.get("descriere");
            String locatie = datePlanta.get("locatie");
            String imagineUrl = datePlanta.get("imagine_url");
            float inaltime = Float.parseFloat(datePlanta.get("inaltime_maxima"));
            String perioadaInflorire = datePlanta.get("perioada_inflorire");
            boolean poateFiUscata = Boolean.parseBoolean(datePlanta.get("poate_fi_uscata"));
            String cicluViata = datePlanta.get("ciclu_de_viata");
            TipPlanta tipPlanta = TipPlanta.valueOf(datePlanta.get("tip_planta"));
            int nrPetale = Integer.parseInt(datePlanta.getOrDefault("numar_petale", "0"));
            String culoare = datePlanta.getOrDefault("culoare", "-");
            String tipCoroana = datePlanta.getOrDefault("tip_coroana", "-");
            String tipFrunza = datePlanta.getOrDefault("tip_frunza", "-");
            boolean pomFructifer = Boolean.parseBoolean(datePlanta.getOrDefault("pom_fructifer", "false"));
            boolean produceFructe = Boolean.parseBoolean(datePlanta.getOrDefault("produce_fructe", "false"));
            String tipTulpina = datePlanta.getOrDefault("tip_tulpina", "-");

            PlantaFactory fabrica = new PlantaFactory();
            Planta plantaActualizata = fabrica.creazaPlanta(CategoriePlanta.valueOf(categorie), id, numeUzual, 
                    denumireStiintifica, familie, descriere, inaltime, perioadaInflorire, cicluViata, tipPlanta, 
                    locatie, imagineUrl, nrPetale, culoare, tipCoroana, tipFrunza, pomFructifer, produceFructe, 
                    tipTulpina, poateFiUscata);

            plantaRepository.actualizeazaPlanta(id, plantaActualizata, locatie, imagineUrl, nrPetale, culoare, 
                    tipCoroana, tipFrunza, pomFructifer, produceFructe, tipTulpina);

            return ResponseEntity.ok("Planta a fost actualizată cu succes!");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Eroare la actualizarea plantei: " + e.getMessage());
        }
    }

    @PostMapping("/scaneaza")
    public ResponseEntity<?> scaneazaImagineYOLO(@RequestBody Map<String,String> payload){
        try{
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Fișierul încărcat este gol.");
            }

            Planta plantaRecunoscuta = plantaService.recunoasteSiAdaugPlanta(file, userId);

            return ResponseEntity.ok(plantaRecunoscuta);
        }catch(Exception e){
            return ResponseEntity.badRequest().body("Eroare la procesarea YOLO: " + e.getMessage());
        }
    }

    @PostMapping("/ierbar-personal/{plantaId}")
    public ResponseEntity<?> salveazaPlanta(@PathVariable int plantaId, Principal principal){
        General user = userRepository.cautaUserDupaEmail(principal.getName());
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        plantaRepository.adaugaInIerbar(user.getId(), plantaId);
        
        return ResponseEntity.ok(Map.of("mesaj", "Planta adaugata in ierbarul tau"));
    }

    @DeleteMapping("/ierbar-personal/{plantaId}")
    public ResponseEntity<?> stergePlantaDinIerbar(@PathVariable int plantaId, Principal principal){
        General user = userRepository.cautaUserDupaEmail(principal.getName());
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        plantaRepository.stergeDinIerbar(user.getId(), plantaId);
        return ResponseEntity.ok(Map.of("mesaj", "Planta stearsa din ierbarul tau personal"));
    }
}