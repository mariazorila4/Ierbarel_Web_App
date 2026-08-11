package com.zmc.ierbar_web_app.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zmc.ierbar_web_app.models.user.Admin;
import com.zmc.ierbar_web_app.models.user.General;
import com.zmc.ierbar_web_app.repositories.UserRepository;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins={"http://localhost:5173", "[http://127.0.0.1:5173](http://127.0.0.1:5173)"})
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
