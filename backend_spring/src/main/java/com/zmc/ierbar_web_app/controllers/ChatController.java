package com.zmc.ierbar_web_app.controllers;

import java.security.Principal;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zmc.ierbar_web_app.models.user.MesajChat;
import com.zmc.ierbar_web_app.models.user.User;
import com.zmc.ierbar_web_app.repositories.UserRepository;
import com.zmc.ierbar_web_app.servicies.AgentAIService;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins={"http://localhost:5173", "[http://127.0.0.1:5173](http://127.0.0.1:5173)"})
public class ChatController {
    private final AgentAIService agentAIService;
    private final UserRepository userRepository;

    public ChatController(AgentAIService agentAIService, UserRepository userRepository) {
        this.agentAIService = agentAIService;
        this.userRepository=userRepository;
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
