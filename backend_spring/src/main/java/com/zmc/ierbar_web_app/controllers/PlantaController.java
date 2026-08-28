package com.zmc.ierbar_web_app.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.zmc.ierbar_web_app.models.CuriozitatePlanta;
import com.zmc.ierbar_web_app.models.factory.CategoriePlanta;
import com.zmc.ierbar_web_app.models.factory.PlantaFactory;
import com.zmc.ierbar_web_app.models.simple_factory.Planta;
import com.zmc.ierbar_web_app.models.simple_factory.TipPlanta;
import com.zmc.ierbar_web_app.models.user.General;
import com.zmc.ierbar_web_app.repositories.PlantaRepository;
import com.zmc.ierbar_web_app.repositories.UserRepository;
import com.zmc.ierbar_web_app.servicies.CuriozitateSchedulerService;
import com.zmc.ierbar_web_app.servicies.PlantaService;

@RestController
@RequestMapping("/api/plante")
@CrossOrigin(origins={"http://localhost:5173", "http://127.0.0.1:5173", "https://ierbarel.netlify.app"})
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
        return plantaRepository.extragePlantePublice();
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

    // ==========================================
    // 📚 IERBAR PERSONAL
    // ==========================================

    @GetMapping("/ierbar-personal")
    public ResponseEntity<?> getIerbarPersonal(Principal principal) {
        try {
            General user = obtineUtilizatorDinPrincipal(principal);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilizator neautentificat.");
            }

            List<CapturaPlanta> capturi = plantaRepository.extrageIerbarPersonalUser(user.getId());
            return ResponseEntity.ok(capturi);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Eroare la preluarea ierbarului: " + e.getMessage());
        }
    }

    @PostMapping("/ierbar-personal/{plantaId}")
    public ResponseEntity<?> salveazaPlanta(@PathVariable int plantaId, Principal principal){
        General user = obtineUtilizatorDinPrincipal(principal);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        plantaRepository.adaugaInIerbar(user.getId(), plantaId);
        return ResponseEntity.ok(Map.of("mesaj", "Planta adaugata in ierbarul tau"));
    }

    @DeleteMapping("/ierbar-personal/{plantaId}")
    public ResponseEntity<?> stergePlantaDinIerbar(@PathVariable int plantaId, Principal principal){
        General user = obtineUtilizatorDinPrincipal(principal);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        plantaRepository.stergeDinIerbar(user.getId(), plantaId);
        return ResponseEntity.ok(Map.of("mesaj", "Planta stearsa din ierbarul tau personal"));
    }

    // ==========================================
    // 🌿 ADMIN, SCANARE & SALVARE
    // ==========================================

    // Helper local pentru normalizare nume (ignoră diacritice, spații, litere mari/mici)
    private String normalizeazaText(String text) {
        if (text == null) return "";
        String nfdNormalized = java.text.Normalizer.normalize(text.toLowerCase().trim(), java.text.Normalizer.Form.NFD);
        return nfdNormalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    // 1. PASUL 1: SALVARE STRICT ÎN IERBARUL PERSONAL (PRIVAT)
    @PostMapping("/salveaza-scanare")
    public ResponseEntity<?> salveazaScanarePersonala(@RequestBody Map<String, Object> payload, Principal principal) {
        try {
            General user = obtineUtilizatorDinPrincipal(principal);
            if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            int userId = user.getId();
            
            // EXTRAGERE DATE DE BAZĂ
            String numeUzual = payload.getOrDefault("nume_uzual", "Plantă Scanată").toString().trim();
            String denumireStiintifica = payload.getOrDefault("denumire_stiintifica", "Specie Botanică").toString().trim();
            String descriere = payload.getOrDefault("descriere", "Identificată prin scanare foto.").toString();
            String familie = payload.getOrDefault("familie", "Familie Botanică").toString();
            String imagineUrl = payload.get("imagine_url") != null ? payload.get("imagine_url").toString() : "";
            String locatie = payload.getOrDefault("locatie", "Nespecificată").toString().trim();

            // EXTRAGERE DATE AVANSATE (DINAMICE DE LA AI)
            String categorieStr = payload.getOrDefault("categorie_planta", "FLOARE").toString().toUpperCase();
            CategoriePlanta categoriePlanta;
            try { categoriePlanta = CategoriePlanta.valueOf(categorieStr); } 
            catch (Exception e) { categoriePlanta = CategoriePlanta.FLOARE; }

            String tipStr = payload.getOrDefault("tip_planta", "ORNAMENTALA").toString().toUpperCase();
            TipPlanta tipPlanta;
            try { tipPlanta = TipPlanta.valueOf(tipStr); } 
            catch (Exception e) { tipPlanta = TipPlanta.ORNAMENTALA; }

            float inaltimeMaxima = 0.5f;
            try { inaltimeMaxima = Float.parseFloat(payload.getOrDefault("inaltime_maxima", "0.5").toString()); } 
            catch (Exception e) {}

            String perioadaInflorire = payload.getOrDefault("perioada_inflorire", "Primăvară-Vară").toString();
            String cicluDeViata = payload.getOrDefault("ciclu_de_viata", "PEREN").toString();
            
            int numarPetale = 5;
            try { numarPetale = Integer.parseInt(payload.getOrDefault("numar_petale", "5").toString()); } 
            catch (Exception e) {}

            String culoare = payload.getOrDefault("culoare", "Diverse").toString();
            String tipCoroana = payload.getOrDefault("tip_coroana", "Nespecificată").toString();
            String tipFrunza = payload.getOrDefault("tip_frunza", "Simplă").toString();
            String tipTulpina = payload.getOrDefault("tip_tulpina", "Erectă").toString();

            boolean pomFructifer = Boolean.parseBoolean(payload.getOrDefault("pom_fructifer", "false").toString());
            boolean produceFructe = Boolean.parseBoolean(payload.getOrDefault("produce_fructe", "false").toString());
            boolean poateFiUscata = Boolean.parseBoolean(payload.getOrDefault("poate_fi_uscata", "false").toString());

            String numeNorm = normalizeazaText(numeUzual);

            // Căutăm dacă specia există deja
            List<Planta> plante = plantaRepository.extrageToatePlantele();
            Optional<Planta> potrivire = plante.stream()
                    .filter(p -> normalizeazaText(p.getNume_uzual()).equals(numeNorm))
                    .findFirst();

            int targetPlantaId;

            if (potrivire.isPresent()) {
                targetPlantaId = potrivire.get().getId();
            } else {
                // DACĂ E SPECIE NOUĂ, FOLOSIM VARIABILELE DINAMICE, NU HARDCODĂRI!
                PlantaFactory factory = new PlantaFactory();
                Planta nouaPlanta = factory.creazaPlanta(
                        categoriePlanta, userId, numeUzual, denumireStiintifica,
                        familie, descriere, inaltimeMaxima, perioadaInflorire, cicluDeViata, tipPlanta,
                        locatie, imagineUrl, numarPetale, culoare, tipCoroana, tipFrunza, pomFructifer, produceFructe, tipTulpina, poateFiUscata
                );

                plantaRepository.salveazaPlantaNoua(
                    nouaPlanta, userId, imagineUrl, numarPetale, culoare, tipCoroana, tipFrunza, pomFructifer, produceFructe, tipTulpina
                );

                targetPlantaId = plantaRepository.extrageUltimulIdPlanta();
            }

            // Salvare în capturi (Ierbar Personal)
            plantaRepository.adaugaCapturaInGalerie(targetPlantaId, userId, imagineUrl, locatie, false);

            List<CapturaPlanta> capturi = plantaRepository.extrageIerbarPersonalUser(userId);
            int capturaId = capturi.isEmpty() ? 0 : capturi.get(0).getId();

            return ResponseEntity.ok(Map.of(
                "mesaj", "Plantă salvată în Ierbarul Personal!",
                "planta_id", targetPlantaId,
                "captura_id", capturaId
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("mesaj", e.getMessage()));
        }
    }

    @PutMapping("/capturi/{capturaId}/publica")
    public ResponseEntity<?> publicaCapturaDinIerbarPersonal(
            @PathVariable int capturaId, 
            @RequestBody Map<String, Object> payload, 
            Principal principal) {
        try {
            General user = obtineUtilizatorDinPrincipal(principal);
            if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            // Extragem locația editată pe care a validat-o utilizatorul în prompt
            String locatie = payload.getOrDefault("locatie", "Nespecificată").toString();

            // Trimitem locația mai departe în repository
            plantaRepository.marcheazaCapturaPublica(capturaId, user.getId(), locatie);

            return ResponseEntity.ok(Map.of("mesaj", "Fotografia a devenit publică!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Eroare la publicare: " + e.getMessage());
        }
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

            // Apelăm noua metodă redenumită și curățată
            Planta plantaRecunoscuta = plantaService.recunoastePlanta(file, userId);
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
            General user = obtineUtilizatorDinPrincipal(principal);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            plantaRepository.stergeCapturaPersonala(capturaId, user.getId());
            return ResponseEntity.ok(Map.of("mesaj", "Fotografia scanată a fost ștearsă din Ierbarul tău!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Eroare la ștergere: " + e.getMessage());
        }
    }

    private General obtineUtilizatorDinPrincipal(Principal principal) {
        if (principal == null) return null;
        String identitate = principal.getName();
        General user = null;
        try {
            user = userRepository.cautaUserDupaEmail(identitate);
        } catch (Exception ignored) {}
        
        if (user == null) {
            try {
                user = userRepository.cautaUserDupaUsername(identitate);
            } catch (Exception ignored) {}
        }
        return user;
    }

    @Autowired
    private CuriozitateSchedulerService curiozitateSchedulerService;

    @GetMapping("/curiozitatea-zilei")
    public ResponseEntity<?> getCuriozitateaZilei() {
        try {
            List<CuriozitatePlanta> curiozitati = plantaRepository.extrageIstoricCuriozitati();
            java.time.LocalDate azi = java.time.LocalDate.now();

            // Verificăm dacă există cel puțin o curiozitate generată AȘADAR PENTRU ZIUA DE AZI
            boolean areCuriozitateAzi = curiozitati.stream()
                    .anyMatch(c -> c.getDataGenerare() != null && c.getDataGenerare().equals(azi));

            // Dacă nu avem nicio curiozitate pentru azi, o generăm automat pe loc!
            if (!areCuriozitateAzi) {
                curiozitateSchedulerService.genereazaSiSalveazaCuriozitate();
                // Reîncărcăm lista actualizată
                curiozitati = plantaRepository.extrageIstoricCuriozitati();
            }

            return ResponseEntity.ok(curiozitati);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Eroare la preluarea curiozităților: " + e.getMessage());
        }
    }
}