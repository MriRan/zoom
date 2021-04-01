package com.mountblue.zoomclone.project.controller;

import javax.servlet.http.HttpSession;

import com.mountblue.zoomclone.project.serviceImplementation.UserServiceImplementation;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
public class LoginController {

    private final UserServiceImplementation userService;

    @RequestMapping(value = "/")
    public String logout(HttpSession httpSession) {
        if (checkUserLogged(httpSession)) {
            return "redirect:/dashboard";
        } else {
            httpSession.invalidate();
            return "index";
        }
    }

    @RequestMapping(value = "/dashboard", method = { RequestMethod.GET, RequestMethod.POST })
    public String login(@RequestParam String user,
                        Model model, HttpSession httpSession) {

        String userName = (String) httpSession.getAttribute("loggedUser");
        if (userName != null) {
            model.addAttribute("username", userName);
        } else {

            model.addAttribute("username", user);
        }

        return "dashboard";
    }

    private boolean checkUserLogged(HttpSession httpSession) {
        return !(httpSession == null || httpSession.getAttribute("loggedUser") == null);
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
                           @RequestParam String email,
                           @RequestParam String password){

        userService.registerNewUser(name,password,"SUBSCRIBER");
        return "redirect:/";
    }
}