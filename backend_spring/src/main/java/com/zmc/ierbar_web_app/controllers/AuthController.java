package com.zmc.ierbar_web_app.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zmc.ierbar_web_app.models.user.General;
import com.zmc.ierbar_web_app.models.user.TipUser;
import com.zmc.ierbar_web_app.repositories.UserRepository;
import com.zmc.ierbar_web_app.security.JwtUtil;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins={"http://localhost:5173", "http://127.0.0.1:5173", "https://ierbarel.netlify.app"})
public class AuthController{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> request){
        String username=request.get("username");
        String email=request.get("email");
        String parolaNecriptata=request.get("password");

        if(userRepository.cautaUserDupaEmail(email)!=null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mesaj", "Emsilul exista deja!"));
        }

        TipUser tipUser=TipUser.GENERAL;
        String parolaCriptata=passwordEncoder.encode(parolaNecriptata);
        userRepository.salveazaUserNou(username, email, parolaCriptata, tipUser);

        return ResponseEntity.ok(Map.of("mesaj", "Cont creat cu succes!"));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request){
        String email=request.get("email");
        String parolaPrimita=request.get("password");

        General user=userRepository.cautaUserDupaEmail(email);

        if(user==null || !passwordEncoder.matches(parolaPrimita, user.getPassword())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("mesaj", "Email sau parola incorecta"));
        }

        if(user.getStatus()!=null && user.getStatus().equals("Blocat")){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("mesaj", "Contul tau a fost blocat de administrator!"));
        }

        String token=jwtUtil.genereazaToken(user.getId(), user.getEmail(), user.getTip_user().toString());

        Map<String, Object> raspuns=new HashMap<>();
        raspuns.put("mesaj", "Logare reusita!");
        raspuns.put("token", token);
        raspuns.put("id", user.getId());
        raspuns.put("tip_user", user.getTip_user().toString());

        return ResponseEntity.ok(raspuns);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(){
        return ResponseEntity.ok(Map.of("mesaj", "Deconectat cu succes."));
    }
}