package com.misha.springbootnewswagger.controller;

import com.misha.springbootnewswagger.entities.SitterEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.misha.springbootnewswagger.repositories.SitterRepository;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

@Controller
public class AppController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/home")
    public String homePage() {
        return "index";
    }

    @GetMapping("/register")
    public String register() {
        return "RegisterSitter";
    }

    @GetMapping("/searchSitter")
    public String searchSitter() {
        return "SearchSitter";
    }
 
    @GetMapping("/sitterList")
    public String sitterList() {
        return "SitterList";
    }

    @Autowired
    private SitterRepository sitterRepository;

    @GetMapping("/userProfile")
    public String userProfile(Authentication authentication, Model model) {
        String email = authentication.getName();

        SitterEntity user = sitterRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);

        return "userProfile";
    }

}
