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
    public String home(HttpSession httpSession) {
        if (checkUserLogged(httpSession)) {
            return "redirect:/dashboard";
        } else {
            httpSession.invalidate();
            return "index";
        }
    }

    @RequestMapping(value = "/dashboard", method = { RequestMethod.GET, RequestMethod.POST })
    public String dashboard(@AuthenticationPrincipal UserPrincipal principal,
                            Model model, HttpSession httpSession) {
        String userName = (String) httpSession.getAttribute("loggedUser");
        if (userName != null) {

            model.addAttribute("username", userName);
            return "dashboard";
        }

        if (principal != null) {
            httpSession.setAttribute("loggedUser", principal.getUsername());
            model.addAttribute("username", principal.getUsername());
            return "dashboard";

        } else {

            httpSession.invalidate();
            return "redirect:/";
        }
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
                           @RequestParam String password){

        userService.registerNewUser(name,password);
        return "redirect:/";
    }
}