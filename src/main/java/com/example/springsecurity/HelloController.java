package com.example.springsecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HelloController {

    @Autowired
    private SessionRegistry sessionRegistry;

    @GetMapping("/")
    public String home() {
        return "You're in home page";
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello world with auth";
    }

    @GetMapping("/session")
    public ResponseEntity<?> getSession() {

        Map<String, Object> mapResponse = new HashMap<>();
        String sessionID = null;
        User userObj = null;

        try {

            userObj = (User) sessionRegistry.getAllPrincipals().stream()
                    .filter(obj -> obj instanceof User)
                    .findFirst().orElse(null);

            sessionID = sessionRegistry.getAllSessions(userObj, false)
                    .get(0).getSessionId();

            mapResponse.put("SessionID", sessionID);
            mapResponse.put("User", userObj);

        } catch (NullPointerException e) {
            ResponseEntity.noContent();
            mapResponse.put("Error", "Please try /logout and try again");
        }

        return ResponseEntity.ok(mapResponse);
    }
}

