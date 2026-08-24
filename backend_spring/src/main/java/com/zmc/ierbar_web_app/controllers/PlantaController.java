package com.zmc.ierbar_web_app.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import com.zmc.ierbar_web_app.models.CapturaPlanta;
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

    // ==========================================
    // 🖼️ ENDPOINT-URI PENTRU GALERIE ȘI HARTĂ
    // ==========================================

    @GetMapping("/{plantaId}/galerie")
    public ResponseEntity<?> getGaleriePlanta(@PathVariable int plantaId) {
        try {
            List<CapturaPlanta> galerie = plantaRepository.extrageGalerieSpecie(plantaId);
            return ResponseEntity.ok(galerie);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Eroare la încărcarea galeriei: " + e.getMessage());
        }
    }

    @GetMapping("/{plantaId}/locatii")
    public ResponseEntity<List<String>> getLocatiiSpecie(@PathVariable int plantaId) {
        try {
            List<String> locatii = plantaRepository.extrageLocatiiSpecie(plantaId);
            return ResponseEntity.ok(locatii);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }

    @PostMapping("/{plantaId}/publica-galerie")
    public ResponseEntity<?> adaugaInGalerie(
            @PathVariable int plantaId, 
            @RequestBody Map<String, Object> payload,
            Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilizator neautentificat.");
            }

            General user = userRepository.cautaUserDupaEmail(principal.getName());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            int targetPlantaId = plantaId;
            String imagineUrl = (String) payload.get("imagine_url");
            String locatie = payload.getOrDefault("locatie", "Nespecificată").toString();

            // Dacă planta nu există încă în BD (ID este 0), o salvăm mai întâi în catalogul oficial
            if (targetPlantaId == 0) {
                String numeUzual = (String) payload.getOrDefault("nume_uzual", "Planta Scanata");
                String denumireStiintifica = (String) payload.getOrDefault("denumire_stiintifica", "Specie Botanica");
                String descriere = (String) payload.getOrDefault("descriere", "Identificată prin scanare foto.");
                String familie = (String) payload.getOrDefault("familie", "Familie Botanică");

                PlantaFactory factory = new PlantaFactory();
                Planta nouaPlanta = factory.creazaPlanta(
                        CategoriePlanta.FLOARE, user.getId(), numeUzual, denumireStiintifica,
                        familie, descriere, 0.4f, "Primăvară-Vară", "PEREN", TipPlanta.ORNAMENTALA,
                        locatie, imagineUrl, 5, "Diverse", "-", "Simplă", false, false, "Erectă", true
                );

                plantaRepository.salveazaPlantaNoua(
                    nouaPlanta, user.getId(), imagineUrl, 5, "Diverse", "-", "Simplă", false, false, "Erectă"
                );

                List<Planta> plante = plantaRepository.extrageToatePlantele();
                targetPlantaId = plante.get(plante.size() - 1).getId();
            }

            // Adăugăm captura foto în tabelul capturi_plante pentru Ierbarul Personal / Galerie
            plantaRepository.adaugaCapturaInGalerie(targetPlantaId, user.getId(), imagineUrl, locatie);

            return ResponseEntity.ok(Map.of("mesaj", "Fotografia ta a fost salvată cu succes!", "planta_id", targetPlantaId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Eroare la salvare: " + e.getMessage());
        }
    }

    // ==========================================
    // 📚 IERBAR PERSONAL
    // ==========================================

    @GetMapping("/ierbar-personal")
    public ResponseEntity<?> getIerbarPersonal(Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilizator neautentificat.");
            }
            General user = userRepository.cautaUserDupaEmail(principal.getName());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            List<CapturaPlanta> capturi = plantaRepository.extrageIerbarPersonalUser(user.getId());
            return ResponseEntity.ok(capturi);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Eroare la preluarea ierbarului: " + e.getMessage());
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

    // ==========================================
    // 🌿 ADMIN & SCANARE
    // ==========================================

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
            String locatie = datePlanta.get("locatie");
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
                tipTulpina
            );

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
                                                 @RequestParam(value = "user_id", defaultValue = "1") int userId) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("eroare", "Fișierul încărcat este gol."));
            }

            Planta plantaRecunoscuta = plantaService.recunoasteSiAdaugPlanta(file, userId);
            return ResponseEntity.ok(plantaRecunoscuta);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("eroare", "Eroare la procesarea YOLO: " + e.getMessage()));
        }
    }

    @DeleteMapping("/captura/{capturaId}")
    public ResponseEntity<?> stergeCapturaPersonala(@PathVariable int capturaId, Principal principal) {
        try {
            General user = userRepository.cautaUserDupaEmail(principal.getName());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            plantaRepository.stergeCapturaPersonala(capturaId, user.getId());
            return ResponseEntity.ok(Map.of("mesaj", "Fotografia scanată a fost ștearsă din Ierbarul tău!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Eroare la ștergere: " + e.getMessage());
        }
    }

    @PostMapping("/salveaza-scanare")
    public ResponseEntity<?> salveazaScanarePersonala(
            @RequestBody Map<String, Object> payload,
            Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilizator neautentificat.");
            }

            General user = userRepository.cautaUserDupaEmail(principal.getName());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String numeUzual = (String) payload.getOrDefault("nume_uzual", "Planta Scanata");
            String denumireStiintifica = (String) payload.getOrDefault("denumire_stiintifica", "Specie Botanica");
            String descriere = (String) payload.getOrDefault("descriere", "Specie identificată prin scanare foto.");
            String familie = (String) payload.getOrDefault("familie", "Familie Botanică");
            String imagineUrl = (String) payload.get("imagine_url");
            String locatie = (String) payload.getOrDefault("locatie", "Nespecificată");

            // 1. Verificăm dacă planta există deja în catalogul global de plante
            List<Planta> plante = plantaRepository.extrageToatePlantele();
            Optional<Planta> potrivire = plante.stream()
                    .filter(p -> p.getNume_uzual() != null && p.getNume_uzual().equalsIgnoreCase(numeUzual))
                    .findFirst();

            int targetPlantaId;

            if (potrivire.isPresent()) {
                targetPlantaId = potrivire.get().getId();
            } else {
                // Dacă nu există, creăm mai întâi planta în BD ca să obținem un ID valid
                PlantaFactory factory = new PlantaFactory();
                Planta nouaPlanta = factory.creazaPlanta(
                        CategoriePlanta.FLOARE, user.getId(), numeUzual, denumireStiintifica,
                        familie, descriere, 0.4f, "Primăvară-Vară", "PEREN", TipPlanta.ORNAMENTALA,
                        locatie, imagineUrl, 5, "Diverse", "-", "Simplă", false, false, "Erectă", true
                );

                plantaRepository.salveazaPlantaNoua(
                    nouaPlanta, user.getId(), imagineUrl, 5, "Diverse", "-", "Simplă", false, false, "Erectă"
                );

                List<Planta> dupaSalvare = plantaRepository.extrageToatePlantele();
                targetPlantaId = dupaSalvare.get(dupaSalvare.size() - 1).getId();
            }

            // 2. Salvăm captura foto în `capturi_plante` cu ID-ul real de plantă obținut
            plantaRepository.adaugaCapturaInGalerie(targetPlantaId, user.getId(), imagineUrl, locatie);

            return ResponseEntity.ok(Map.of("mesaj", "Fotografia a fost salvată în Ierbarul tău!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Eroare la salvare: " + e.getMessage());
        }
    }
}