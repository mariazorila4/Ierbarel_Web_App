package com.zmc.ierbar_web_app.servicies;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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

    public String detecteazaPlantaYOLO(MultipartFile file) {
        String urlPython = "http://localhost:5000/predict";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(urlPython, requestEntity, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());

            return root.path("clasa").asText();
        } catch (Exception e) {
            System.err.println("Eroare la apelul Python YOLO: " + e.getMessage());
            return null;
        }
    }

    public MesajChat proceseazaConversatia(int userId, String textUtilizator) {
        salveazaMesajInBazaDeDate(userId, textUtilizator, false);

        String textRaspunsAi;

        try{
            textRaspunsAi=apeleazaGeminiApi(textUtilizator);
        }catch(Exception e){
            System.err.println("Eroare la comunicarea cu gemini: "+e.getMessage());
            textRaspunsAi="Ne pare rau, Ierbarel a intampinat o eroare de conexiune. Mai incearca o data.";
        }

        salveazaMesajInBazaDeDate(userId, textRaspunsAi, true);

        MesajChat mesajBot=new MesajChat();

        mesajBot.setMesaj(textRaspunsAi);
        mesajBot.setEste_bot(true);
        mesajBot.setData_trimiterii(LocalDateTime.now());

        return mesajBot;
    }

    private void salveazaMesajInBazaDeDate(int userId, String mesaj, boolean este_bot) {
        String sql="INSERT INTO istoric_chat(user_id, mesaj, este_bot) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, mesaj, este_bot);
    }

    private String apeleazaGeminiApi(String promptUtilizator) throws Exception {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent?key=" + apiKey;
        
        String promptComplet="Esti Ghiocel, un sistent virtual botanist, prietenos, cute si educativ. Nu trebuie sa te preziniti la fiecare inceput de conversatie, este pusa deja o introducere default."+
                        "Raspunde scurt (maxim 2 paragrafe) si prietenos la urmatoarea solicitare: "+promptUtilizator;

        Map<String, Object> requestBody=Map.of(
            "contents", List.of(Map.of("parts", List.of(Map.of("text", promptComplet))))
        );

        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity=new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<String> response=restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        JsonNode root=objectMapper.readTree(response.getBody());
        String raspunsCurat=root.path("candidates").get(0).path("content")
                            .path("parts").get(0).path("text").asText();

        return raspunsCurat;
    }
    
}
