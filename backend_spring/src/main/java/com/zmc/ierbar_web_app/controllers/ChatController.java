package com.zmc.ierbar_web_app.controllers;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zmc.ierbar_web_app.models.user.General;
import com.zmc.ierbar_web_app.models.user.MesajChat;
import com.zmc.ierbar_web_app.repositories.UserRepository;
import com.zmc.ierbar_web_app.servicies.AgentAIService;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins={"http://localhost:5173", "http://127.0.0.1:5173", "https://ierbarel.netlify.app"})
public class ChatController {
    private final AgentAIService agentAIService;
    private final UserRepository userRepository;

    public ChatController(AgentAIService agentAIService, UserRepository userRepository) {
        this.agentAIService = agentAIService;
        this.userRepository = userRepository;
    }

    @GetMapping("/conversatii")
    public ResponseEntity<?> getConversatii(Principal principal) {
        General user = obtineUser(principal);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(userRepository.extrageConversatiiUser(user.getId()));
    }

    @GetMapping("/conversatii/{conversatieId}/mesaje")
    public ResponseEntity<?> getMesajeConversatie(@PathVariable int conversatieId, Principal principal) {
        General user = obtineUser(principal);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(userRepository.extrageMesajeConversatie(conversatieId));
    }

    @PostMapping("/trimite")
    public ResponseEntity<?> trimiteMesaj(@RequestBody Map<String, Object> payload, Principal principal) {
        General user = obtineUser(principal);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String textUtilizator = (String) payload.get("mesaj");
        Integer conversatieId = (Integer) payload.get("conversatieId");

        if (conversatieId == null || conversatieId == 0) {
            String titlu = textUtilizator.length() > 35 ? textUtilizator.substring(0, 32) + "..." : textUtilizator;
            conversatieId = userRepository.creeazaConversatie(user.getId(), titlu);
        }

        MesajChat raspuns = agentAIService.proceseazaConversatia(user.getId(), conversatieId, textUtilizator);
        
        return ResponseEntity.ok(Map.of(
            "conversatieId", conversatieId,
            "mesaj", raspuns.getMesaj(),
            "este_bot", true
        ));
    }

    private General obtineUser(Principal principal) {
        if (principal == null) return null;
        String identitate = principal.getName();
        General user = userRepository.cautaUserDupaEmail(identitate);
        return (user != null) ? user : userRepository.cautaUserDupaUsername(identitate);
    }
}