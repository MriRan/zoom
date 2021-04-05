package com.mountblue.zoomclone.project.controller;

import com.mountblue.zoomclone.project.serviceImplementation.UserPrincipal;
import com.mountblue.zoomclone.project.serviceImplementation.UserServiceImplementation;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@AllArgsConstructor
public class LoginController {

    private final UserServiceImplementation userService;

    @RequestMapping(value = "/")
    public String home() {
            return "index";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/signUp")
    public String signUp(){
        return "signUp";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name,
                           @RequestParam String password){

        userService.registerNewUser(name,password);
        return "redirect:/";
    }
    @RequestMapping("/success")
    public String success(@AuthenticationPrincipal UserPrincipal principal,
                          HttpSession httpSession){

        httpSession.setAttribute("loggedUser", principal.getUsername());
        return "index";
    }
}