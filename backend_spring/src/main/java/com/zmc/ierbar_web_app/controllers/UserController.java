package com.zmc.ierbar_web_app.controllers;

import com.zmc.ierbar_web_app.models.user.*;
import com.zmc.ierbar_web_app.repositories.UserRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins="*")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/general/{id}")
    public General getProfilGeneral(@PathVariable int id){
        return userRepository.extrageProfilGeneral(id);
    }

    @GetMapping("/admin/{id}")
    public Admin getProfilAdmin(@PathVariable int id){
        return userRepository.extrageProfilAdmin(id);
    }
}
