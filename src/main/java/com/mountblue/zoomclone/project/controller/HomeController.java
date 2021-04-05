package com.mountblue.zoomclone.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class HomeController {


    @RequestMapping(value = "/dashboard", method = { RequestMethod.GET, RequestMethod.POST })
    public String dashboard(@RequestParam String link,
                            Model model,
                            HttpSession httpSession) {

        String userName = (String) httpSession.getAttribute("loggedUser");
        String sessionName = link.substring(link.length()-6);
        model.addAttribute("sessionName", sessionName);
        if (userName != null) {

            model.addAttribute("username", userName);

        } else {
            httpSession.invalidate();
        }
        return "dashboard";
    }

}
