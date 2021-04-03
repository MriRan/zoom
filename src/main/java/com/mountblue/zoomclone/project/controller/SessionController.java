package com.mountblue.zoomclone.project.controller;

import io.openvidu.java.client.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class SessionController {

    private final OpenVidu openVidu;

    private final Map<String, Session> mapSessions = new ConcurrentHashMap<>();
    private final Map<String, Map<String, OpenViduRole>> mapSessionNamesTokens = new ConcurrentHashMap<>();

    public SessionController(@Value("${openvidu.secret}") String secret,
                             @Value("${openvidu.url}") String openviduUrl) {

        this.openVidu = new OpenVidu(openviduUrl, secret);
    }

    @RequestMapping("/j/{sessionName}")
    public String joinWithUrl(@PathVariable String sessionName,
                              Model model){

        model.addAttribute("sessionName", sessionName);
        System.out.println(sessionName);
        return "dashboard";
    }

    @RequestMapping(value = "/session", method = RequestMethod.POST)
    public String joinSession(@RequestParam(name = "data") String clientData,
                              @RequestParam String sessionName,
                              Model model, HttpSession httpSession) {

        System.out.println("Getting sessionId and token | {sessionName}={" + sessionName + "}");

        OpenViduRole role = OpenViduRole.PUBLISHER;
        String serverData = "{\"serverData\": \"" + httpSession.getAttribute("loggedUser") + "\"}";

        ConnectionProperties connectionProperties = new ConnectionProperties.Builder().type(ConnectionType.WEBRTC)
                .role(role).data(serverData).build();

        if (this.mapSessions.get(sessionName) != null) {
            System.out.println("Existing session " + sessionName);
            try {

                String token = this.mapSessions.get(sessionName).createConnection(connectionProperties).getToken();
                return setModelAttributeForSession(clientData, sessionName, model, httpSession, role, token);

            } catch (Exception e) {
                model.addAttribute("username", httpSession.getAttribute("loggedUser"));
                return "dashboard";
            }
        } else {
            System.out.println("New session " + sessionName);
            try {

                Session session = this.openVidu.createSession();
                String token = session.createConnection(connectionProperties).getToken();
                this.mapSessions.put(sessionName, session);
                this.mapSessionNamesTokens.put(sessionName, new ConcurrentHashMap<>());
                return setModelAttributeForSession(clientData, sessionName, model, httpSession, role, token);

            } catch (Exception e) {
                model.addAttribute("username", httpSession.getAttribute("loggedUser"));
                return "dashboard";
            }
        }
    }

    @RequestMapping(value = "/leave-session", method = RequestMethod.POST)
    public String removeUser(@RequestParam(name = "sessionName") String sessionName,
                             @RequestParam(name = "token") String token) throws Exception {

        System.out.println("Removing user | sessionName=" + sessionName + ", token=" + token);

        if (this.mapSessions.get(sessionName) != null && this.mapSessionNamesTokens.get(sessionName) != null) {

            if (this.mapSessionNamesTokens.get(sessionName).remove(token) != null) {
                if (this.mapSessionNamesTokens.get(sessionName).isEmpty()) {
                    this.mapSessions.remove(sessionName);
                }

            } else {
                System.out.println("Problems in the app server: the TOKEN wasn't valid");
            }

        } else {
            System.out.println("Problems in the app server: the SESSION does not exist");
        }
        return "redirect:/";
    }

    @RequestMapping("/host-meeting")
    public String host(Model model, HttpSession httpSession) {

        OpenViduRole role = OpenViduRole.PUBLISHER;
        String serverData = "{\"serverData\": \"" + httpSession.getAttribute("loggedUser") + "\"}";

        ConnectionProperties connectionProperties = new ConnectionProperties.Builder().type(ConnectionType.WEBRTC)
                .role(role).data(serverData).build();
        String sessionName = getRandomChars();

        try {

            Session session = this.openVidu.createSession();
            String token = session.createConnection(connectionProperties).getToken();
            this.mapSessions.put(sessionName, session);
            this.mapSessionNamesTokens.put(sessionName, new ConcurrentHashMap<>());
            this.mapSessionNamesTokens.get(sessionName).put(token, role);
            model.addAttribute("sessionName", sessionName);

            return "index";

        } catch (Exception e) {
            model.addAttribute("username", httpSession.getAttribute("loggedUser"));
            return "index";
        }
    }

    private String setModelAttributeForSession(@RequestParam(name = "data") String clientData,
                                               @RequestParam(name = "session-name") String sessionName,
                                               Model model, HttpSession httpSession,
                                               OpenViduRole role,
                                               String token) {

        this.mapSessionNamesTokens.get(sessionName).put(token, role);
        model.addAttribute("sessionName", sessionName);
        model.addAttribute("token", token);
        model.addAttribute("nickName", clientData);
        model.addAttribute("userName", httpSession.getAttribute("loggedUser"));

        return "session";
    }

    private String getRandomChars() {

        StringBuilder randomUrl = new StringBuilder();
        String possibleChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for (int i = 0; i < 6; i++)
            randomUrl.append(possibleChars.charAt((int) Math.floor(Math.random() * possibleChars.length())));
        System.out.println(randomUrl);
        return randomUrl.toString();
    }

}

