package com.zmc.ierbar_web_app.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zmc.ierbar_web_app.models.user.General;
import com.zmc.ierbar_web_app.repositories.UserRepository;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class AdminController {
    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    @GetMapping("/utilizatori")
    public ResponseEntity<List<General>> getTotiUtilizatori(Principal principal){
        General userCurent=userRepository.cautaUserDupaEmail(principal.getName());
        if(userCurent==null || !userCurent.getTip_user().name().equals("ADMIN")){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<General> lista=userRepository.extrageTotiUtilizatorii();
        return ResponseEntity.ok(lista);
    }

    @PutMapping("/utilizatori/{id}/status")
    public ResponseEntity<?> schimbaStatus(@PathVariable int id, @RequestBody Map<String, String> payload, Principal principal){
        General userCurent=userRepository.cautaUserDupaEmail(principal.getName());

        if(userCurent==null || !userCurent.getTip_user().name().equals("ADMIN")){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("eroare", "Acces interzis"));
        }

        if(userCurent.getId()==id){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("eroare", "Nu te poti bloca singur"));
        }

        String newStatus=payload.get("status");
        userRepository.schimbaStatusUtilizator(id, newStatus);

        return ResponseEntity.ok(Map.of("mesaj", "Status actualizat cu succes"));
    }

    @DeleteMapping("/utilizatori/{id}")
    public ResponseEntity<?> stergeUtilizator(@PathVariable int id, Principal principal){
        String emailUtilizatorLogat=principal.getName();
        General userCurent=userRepository.cautaUserDupaEmail(emailUtilizatorLogat);

        if(userCurent==null || !userCurent.getTip_user().name().equals("ADMIN")){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("eroare", "Acces interzis! Nu ai drepturi de administrator."));
        }

        if(userCurent.getId()==id){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("eroare", "Nu te poti sterge pe tine insuti din sistem."));
        }

        userRepository.stergeUser(id);

        return ResponseEntity.ok(Map.of("mesaj", "Utilizator sters cu succes"));
    }
}
