package com.zmc.ierbar_web_app.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframewoek.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zmc.ierbar_web_app.models.user.General;
import com.zmc.ierbar_web_app.models.user.MesajChat;
import com.zmc.ierbar_web_app.models.user.User;
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
        this.userRepository=userRepository;
    }

    @GetMapping("/istoric")
    public ResponseEntity<?> extrageIstoric(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilizator neautentificat.");
        }

        String identitate = principal.getName();
        General user = userRepository.cautaUserDupaEmail(identitate);
        if (user == null) {
            user = userRepository.cautaUserDupaUsername(identitate);
        }

        if (user == null) {
            return ResponseEntity.badRequest().body("Utilizatorul nu a fost găsit.");
        }

        List<MesajChat> istoric = userRepository.extrageIstoricChat(user.getId());
        return ResponseEntity.ok(istoric);
    }

    @PostMapping("/trimite")
    public MesajChat trmiteMesaj(@RequestBody Map<String, String> payload, Principal principal){
        String emailUtilizatorLogat=principal.getName();
        User user=userRepository.cautaUserDupaEmail(emailUtilizatorLogat);

        if(user==null){
            throw new RuntimeException("Utilizatorul nu a fost gasit");
        }

        int userId=user.getId();
        String textUtilizator=payload.get("mesaj");

        MesajChat raspunsIerbarel=agentAIService.proceseazaConversatia(userId, textUtilizator);

        return raspunsIerbarel;
    }
}
