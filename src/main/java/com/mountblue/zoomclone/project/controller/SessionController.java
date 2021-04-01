package com.mountblue.zoomclone.project.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import io.openvidu.java.client.ConnectionProperties;
import io.openvidu.java.client.ConnectionType;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduRole;
import io.openvidu.java.client.Session;

@Controller
public class SessionController {

    private final OpenVidu openVidu;

    private final Map<String, Session> mapSessions = new ConcurrentHashMap<>();
    private final Map<String, Map<String, OpenViduRole>> mapSessionNamesTokens = new ConcurrentHashMap<>();

    public SessionController(@Value("${openvidu.secret}") String secret, @Value("${openvidu.url}") String openviduUrl) {
        this.openVidu = new OpenVidu(openviduUrl, secret);
    }

    @RequestMapping(value = "/session", method = RequestMethod.POST)
    public String joinSession(@RequestParam(name = "data") String clientData,
                              @RequestParam(name = "session-name") String sessionName, Model model, HttpSession httpSession) {

        try {
            checkUserLogged(httpSession);
        } catch (Exception e) {
            return "index";
        }
        System.out.println("Getting sessionId and token | {sessionName}={" + sessionName + "}");

        OpenViduRole role = LoginController.users.get(httpSession.getAttribute("loggedUser")).getRole();
        String serverData = "{\"serverData\": \"" + httpSession.getAttribute("loggedUser") + "\"}";

        ConnectionProperties connectionProperties = new ConnectionProperties.Builder().type(ConnectionType.WEBRTC)
                .role(role).data(serverData).build();

        if (this.mapSessions.get(sessionName) != null) {
            System.out.println("Existing session " + sessionName);
            try {
                String token = this.mapSessions.get(sessionName).createConnection(connectionProperties).getToken();

                return getString(clientData, sessionName, model, httpSession, role, token);

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
                return getString(clientData, sessionName, model, httpSession, role, token);

            } catch (Exception e) {
                model.addAttribute("username", httpSession.getAttribute("loggedUser"));
                return "dashboard";
            }
        }
    }

    private String getString(@RequestParam(name = "data") String clientData, @RequestParam(name = "session-name") String sessionName, Model model, HttpSession httpSession, OpenViduRole role, String token) {
        this.mapSessionNamesTokens.get(sessionName).put(token, role);

        model.addAttribute("sessionName", sessionName);
        model.addAttribute("token", token);
        model.addAttribute("nickName", clientData);
        model.addAttribute("userName", httpSession.getAttribute("loggedUser"));

        return "session";
    }

    @RequestMapping(value = "/leave-session", method = RequestMethod.POST)
    public String removeUser(@RequestParam(name = "session-name") String sessionName,
                             @RequestParam(name = "token") String token, Model model, HttpSession httpSession) throws Exception {

        try {
            checkUserLogged(httpSession);
        } catch (Exception e) {
            return "index";
        }
        System.out.println("Removing user | sessioName=" + sessionName + ", token=" + token);

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
        return "redirect:/dashboard";
    }

    private void checkUserLogged(HttpSession httpSession) throws Exception {
        if (httpSession == null || httpSession.getAttribute("loggedUser") == null) {
            throw new Exception("User not logged");
        }
    }

}

