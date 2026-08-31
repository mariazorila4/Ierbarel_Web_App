package com.zmc.ierbar_web_app.controllers;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zmc.ierbar_web_app.models.user.Admin;
import com.zmc.ierbar_web_app.models.user.General;
import com.zmc.ierbar_web_app.repositories.UserRepository;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins={"http://localhost:5173", "http://127.0.0.1:5173", "https://ierbarel.netlify.app"})
public class UserController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ==========================================
    // 📌 ENDPOINT-URI EXISTENTE
    // ==========================================

    @GetMapping("/general/{id}")
    public General getProfilGeneral(@PathVariable int id){
        return userRepository.extrageProfilGeneral(id);
    }

    @GetMapping("/admin/{id}")
    public Admin getProfilAdmin(@PathVariable int id){
        return userRepository.extrageProfilAdmin(id);
    }

    // ==========================================
    // 👤 ENDPOINT-URI NOI PENTRU GESTIUNE PROFIL
    // ==========================================

    // 1. Extragere profil pentru utilizatorul logat curent (din Token JWT)
    @GetMapping("/profil")
    public ResponseEntity<?> getProfilCurent(Principal principal) {
        General user = obtineUserDinPrincipal(principal);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilizator neautentificat.");
        }

        user.setPassword(null); // Ascundem hash-ul parolei pentru securitate
        return ResponseEntity.ok(user);
    }

    // 2. Editare Username, Email și URL Imagine Profil
    @PutMapping("/profil")
    public ResponseEntity<?> actualizeazaProfil(@RequestBody Map<String, String> payload, Principal principal) {
        General user = obtineUserDinPrincipal(principal);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String noulUsername = payload.getOrDefault("username", user.getUsername()).trim();
        String noulEmail = payload.getOrDefault("email", user.getEmail()).trim();
        
        // Preluăm URL-ul imaginii din payload sau îl păstrăm pe cel vechi
        String nouaImagine = payload.get("imagine_url");
        if (nouaImagine == null) {
            nouaImagine = user.getImagine_url();
        } else {
            nouaImagine = nouaImagine.trim();
        }

        try {
            // Trimitem 4 parametri către metoda actualizată din UserRepository
            userRepository.actualizeazaProfil(user.getId(), noulUsername, noulEmail, nouaImagine);
            return ResponseEntity.ok(Map.of("mesaj", "Profilul a fost actualizat cu succes!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Eroare la actualizare: Numele sau emailul există deja.");
        }
    }

    // 3. Schimbare Parolă
    @PutMapping("/profil/schimba-parola")
    public ResponseEntity<?> schimbaParola(@RequestBody Map<String, String> payload, Principal principal) {
        General user = obtineUserDinPrincipal(principal);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String parolaVeche = payload.get("parola_veche");
        String parolaNoua = payload.get("parola_noua");

        if (parolaVeche == null || parolaNoua == null || parolaNoua.length() < 6) {
            return ResponseEntity.badRequest().body("Parola nouă trebuie să aibă minim 6 caractere.");
        }

        if (!passwordEncoder.matches(parolaVeche, user.getPassword())) {
            return ResponseEntity.badRequest().body("Parola curentă este incorectă.");
        }

        String parolaEncodata = passwordEncoder.encode(parolaNoua);
        userRepository.actualizeazaParola(user.getId(), parolaEncodata);

        return ResponseEntity.ok(Map.of("mesaj", "Parola a fost schimbată cu succes!"));
    }

    @GetMapping("/admin/statistici")
    public ResponseEntity<?> getStatisticiSistem(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Map<String, Object> statistici = userRepository.extrageStatisticiSistem();
        return ResponseEntity.ok(statistici);
    }

    // Helper intern pentru extragerea utilizatorului curent
    private General obtineUserDinPrincipal(Principal principal) {
        if (principal == null) return null;
        String identitate = principal.getName();
        General user = null;
        try { user = userRepository.cautaUserDupaEmail(identitate); } catch (Exception ignored) {}
        if (user == null) {
            try { user = userRepository.cautaUserDupaUsername(identitate); } catch (Exception ignored) {}
        }
        return user;
    }
}