package com.zmc.ierbar_web_app.servicies;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.zmc.ierbar_web_app.models.user.MesajChat;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class AgentAIService {
    private final JdbcTemplate jdbcTemplate;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    public AgentAIService(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
    }

    // ==============================================================
    // 🌿 IDENTIFICARE VIZUALĂ DIRECTĂ CU GEMINI VISION (MULTIMODAL)
    // ==============================================================
    public Map<String, String> scaneazaPlantaCuGeminiVision(MultipartFile file) {
        try {
            // 1. Convertim imaginea încărcată în Base64
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
            String mimeType = (file.getContentType() != null && !file.getContentType().isBlank()) 
                    ? file.getContentType() : "image/jpeg";

            // 2. Endpoint-ul oficial Gemini 1.5 Flash Multimodal
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

            String prompt = "Ești un expert botanist. Analizează această imagine cu o plantă și identific-o cu precizie botanică. " +
                    "Răspunde STRICT sub formă de JSON în limba română (fără formatare markdown ```json): " +
                    "{" +
                    "\"nume_uzual\": \"...\", \"denumire_stiintifica\": \"...\", \"familie\": \"...\", " +
                    "\"descriere\": \"... (maxim 2 fraze captivante)\", " +
                    "\"categorie_planta\": \"FLOARE/ARBORE/ARBUST/IERBURI/FERIGA/MUSCHI/ALTA\", " +
                    "\"tip_planta\": \"ORNAMENTALA/MEDICINALA/AROMATICA/TOXICA/FRUCTIFERA/CARNIVORA/ALTA\", " +
                    "\"inaltime_maxima\": \"0.5\", \"perioada_inflorire\": \"...\", \"ciclu_de_viata\": \"PEREN/ANUAL/BIENAL\", " +
                    "\"numar_petale\": \"5\", \"culoare\": \"...\", \"tip_coroana\": \"...\", \"tip_frunza\": \"...\", \"tip_tulpina\": \"...\", " +
                    "\"pom_fructifer\": \"false\", \"produce_fructe\": \"false\", \"poate_fi_uscata\": \"false\"" +
                    "}";

            // 3. Construim structura de date trimisă către Gemini (Text + Base64 Image)
            Map<String, Object> imagePart = Map.of(
                "inline_data", Map.of(
                    "mime_type", mimeType,
                    "data", base64Image
                )
            );
            Map<String, Object> textPart = Map.of("text", prompt);

            Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of("parts", List.of(textPart, imagePart)))
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            // 4. Extragem răspunsul din JSON-ul Gemini
            JsonNode root = objectMapper.readTree(response.getBody());
            String jsonText = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

            // Curățăm marcajele de cod markdown
            String cleanJson = jsonText.replaceAll("(?s)```json\\n?(.*?)\\n?```", "$1")
                                       .replaceAll("(?s)```\\n?(.*?)\\n?```", "$1")
                                       .trim();

            @SuppressWarnings("unchecked")
            Map<String, Object> rawMap = objectMapper.readValue(cleanJson, Map.class);

            Map<String, String> finalMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : rawMap.entrySet()) {
                finalMap.put(entry.getKey(), String.valueOf(entry.getValue()));
            }

            return finalMap;

        } catch (Exception e) {
            System.err.println("❌ Eroare la identificarea vizuală Gemini: " + e.getMessage());
            return Map.of(
                "nume_uzual", "Plantă Necunoscută",
                "denumire_stiintifica", "Specie neidentificată",
                "descriere", "Nu am putut identifica cu precizie planta din imagine. Asigură-te că imaginea este clară și bine luminată.",
                "categorie_planta", "FLOARE",
                "tip_planta", "ORNAMENTALA"
            );
        }
    }

    public MesajChat proceseazaConversatia(int userId, int conversatieId, String textUtilizator) {
        salveazaMesajInBazaDeDate(userId, conversatieId, textUtilizator, false);

        String textRaspunsAi;
        try {
            textRaspunsAi = apeleazaGeminiApi(textUtilizator);
        } catch (Exception e) {
            textRaspunsAi = "Ne pare rău, Ghiocel a întâmpinat o eroare de conexiune.";
        }

        salveazaMesajInBazaDeDate(userId, conversatieId, textRaspunsAi, true);

        MesajChat mesajBot = new MesajChat();
        mesajBot.setMesaj(textRaspunsAi);
        mesajBot.setEste_bot(true);
        mesajBot.setData_trimiterii(LocalDateTime.now());
        return mesajBot;
    }

    private void salveazaMesajInBazaDeDate(int userId, int conversatieId, String mesaj, boolean este_bot) {
        String sql = "INSERT INTO istoric_chat(user_id, conversatie_id, mesaj, este_bot) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, userId, conversatieId, mesaj, este_bot);
    }
    
    private String apeleazaGeminiApi(String promptUtilizator) throws Exception {
        String url = "[https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=](https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=)" + apiKey;

        String promptComplet = "Esti Ghiocel, un asistent virtual botanist, prietenos, cute si educativ. Nu trebuie sa te prezinti la fiecare inceput de conversatie." +
                "Raspunde scurt (maxim 2 paragrafe) si prietenos la urmatoarea solicitare: " + promptUtilizator;

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", promptComplet))))
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        JsonNode root = objectMapper.readTree(response.getBody());
        return root.path("candidates").get(0).path("content")
                .path("parts").get(0).path("text").asText();
    }

    public Map<String, String> genereazaDetaliiBotanice(String numeSpecie) {
        try {
            String prompt = "Ești un expert botanist. Generează fișa tehnică detaliată pentru planta cu denumirea: '" + numeSpecie + "'. " +
                    "Răspunde STRICT sub formă de JSON cu următoarele chei și folosește exclusiv limba română: " +
                    "\"nume_uzual\", \"denumire_stiintifica\", \"familie\", \"descriere\" (maxim 2 fraze captivante), " +
                    "\"categorie_planta\" (alege strict una din: FLOARE, ARBORE, ARBUST, IERBURI, FERIGA, MUSCHI, ALTA), " +
                    "\"tip_planta\" (alege strict una din: ORNAMENTALA, MEDICINALA, AROMATICA, TOXICA, FRUCTIFERA, CARNIVORA, ALTA), " +
                    "\"inaltime_maxima\" (doar cifre, ex: 0.5), \"perioada_inflorire\", \"ciclu_de_viata\" (PEREN, ANUAL, BIENAL), " +
                    "\"numar_petale\" (cifra, pune 0 daca nu are), \"culoare\", \"tip_coroana\", \"tip_frunza\", \"tip_tulpina\", " +
                    "\"pom_fructifer\" (true/false), \"produce_fructe\" (true/false), \"poate_fi_uscata\" (true/false). " +
                    "NU adăuga absolut deloc formatare markdown sau block-uri de cod (```json).";

            String descriereGenerata = apeleazaGeminiApi(prompt);

            String jsonResult = descriereGenerata.replaceAll("(?s)```json\\n?(.*?)\\n?```", "$1").trim();
            jsonResult = jsonResult.replaceAll("(?s)```\\n?(.*?)\\n?```", "$1").trim();

            @SuppressWarnings("unchecked")
            Map<String, Object> rawMap = objectMapper.readValue(jsonResult, Map.class);

            Map<String, String> finalMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : rawMap.entrySet()) {
                finalMap.put(entry.getKey(), String.valueOf(entry.getValue()));
            }

            return finalMap;
            
        } catch (Exception e) {
            System.err.println("Eroare la apelul Gemini AI (Text) pentru specia " + numeSpecie + ": " + e.getMessage());
            
            return Map.of(
                "nume_uzual", numeSpecie,
                "denumire_stiintifica", numeSpecie + " spp.",
                "familie", "Necunoscută",
                "descriere", "O plantă interesantă din natură.",
                "categorie_planta", "FLOARE",
                "tip_planta", "ORNAMENTALA"
            );
        }
    }

    public String genereazaCuriozitateInedita(String numePlanta, List<String> curiozitatiVechi) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Spune-mi o curiozitate fascinantă, surprinzătoare și puțin cunoscută despre planta '")
            .append(numePlanta)
            .append("'.\n");

        if (curiozitatiVechi != null && !curiozitatiVechi.isEmpty()) {
            prompt.append("IMPORTANT: Te rog să NU repeți și să NU menționezi ideile sau informațiile din următoarele curiozități deja folosite:\n");
            for (String veche : curiozitatiVechi) {
                prompt.append("- ").append(veche).append("\n");
            }
            prompt.append("Vreau un fapt botanic complet NOU și diferit!");
        }

        try {
            return apeleazaGeminiApi(prompt.toString());
        } catch (Exception e) {
            System.err.println("Eroare la generarea curiozității pentru " + numePlanta + ": " + e.getMessage());
            return "Planta " + numePlanta + " are proprietăți adaptative uimitoare și joacă un rol esențial în ecosistemul său.";
        }
    }
}