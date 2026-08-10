package com.zmc.ierbar_web_app.controllers;

import com.zmc.ierbar_web_app.models.user.MesajChat;
import com.zmc.ierbar_web_app.servicies.AgentAIService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins="*")
public class ChatController {
    private final AgentAIService agentAIService;

    public ChatController(AgentAIService agentAIService) {
        this.agentAIService = agentAIService;
    }

    @PostMapping("/trimite/{userId}")
    public MesajChat trmiteMesaj(@PathVariable int userId, @RequestBody Map<String, String> payload){
        String textUtilizator=payload.get("mesaj");
        MesajChat raspunsIerbarel=agentAIService.proceseazaConversatia(userId, textUtilizator);

        return raspunsIerbarel;
    }
}
