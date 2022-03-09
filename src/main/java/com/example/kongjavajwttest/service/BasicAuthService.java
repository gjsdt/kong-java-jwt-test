package com.example.kongjavajwttest.service;

import com.example.kongjavajwttest.domain.BasicAuthCredentials;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class BasicAuthService {

    private final Map<String, BasicAuthCredentials> credentials = new HashMap<>();

    public void verifyBasicAuth(String basicAuth, long userId) {
        BasicAuthCredentials actualCredentials;
        BasicAuthCredentials expectedCredentials;
        //Authentication
        try {
            actualCredentials = BasicAuthCredentials.parse(basicAuth, userId);
            expectedCredentials = credentials.get(actualCredentials.getUsername());
            if (!BasicAuthCredentials.isAuthenticated(expectedCredentials, actualCredentials)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
        }
        //Authorization
        try {
            if (!BasicAuthCredentials.isAuthorized(expectedCredentials, actualCredentials)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "FORBIDDEN");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "FORBIDDEN");
        }
    }

    @PostConstruct
    public void setup() {
        addCredentials();
    }

    private void addCredentials() {
        credentials.put("jon", new BasicAuthCredentials("jon", "secret", 1234L));
        credentials.put("pete", new BasicAuthCredentials("pete", "secret", 5679L));
    }
}
