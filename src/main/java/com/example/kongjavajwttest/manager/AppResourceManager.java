package com.example.kongjavajwttest.manager;

import com.example.kongjavajwttest.domain.AppConsumer;
import com.example.kongjavajwttest.domain.AppJwtCredential;
import com.example.kongjavajwttest.service.BasicAuthService;
import com.example.kongjavajwttest.service.JwtAuthService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Service
@Log4j2
public class AppResourceManager {

    @Autowired
    private JwtAuthService jwtAuthService;

    @Autowired
    private BasicAuthService basicAuthService;


    private static final Map<Long, AppConsumer> appConsumers = new HashMap<>();

    private static final RestTemplate kongClient = new RestTemplateBuilder()
            .rootUri("http://localhost:8001")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    public String login(long userId, String basicAuth) {
        basicAuthService.verifyBasicAuth(basicAuth, userId);
        AppConsumer consumer = getConsumer(userId);
        return createConsumerJwt(consumer);
    }

    public String getResource(long userId, String jwtAuth) {
        jwtAuthService.verifyJwtAuth(jwtAuth, userId);
        return "Here is your resource";
    }

    private AppConsumer getConsumer(long userId) {
        if (appConsumers.containsKey(userId)) {
            return appConsumers.get(userId);
        }
        try {
            AppConsumer newConsumer = new AppConsumer(userId, jwtAuthService.getJwtCredentialBuilder());
            syncConsumerToKong(newConsumer);
            syncConsumerJwtCredentialsToKong(newConsumer);
            appConsumers.put(userId, newConsumer);
            return newConsumer;
        } catch (Exception e) {
            log.info("Error creating consumer", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL SERVER ERROR");
        }
    }

    private String createConsumerJwt(AppConsumer consumer) {
        try {
            return jwtAuthService.createJwt(String.valueOf(consumer.getId()));
        } catch (Exception e) {
            log.info("Error generating jwt", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL SERVER ERROR");
        }
    }

    private void syncConsumerToKong(AppConsumer consumer) {
        ResponseEntity<AppConsumer> consumerUpsert = kongClient.exchange("/consumers/{consumerUuid}", HttpMethod.PUT, new HttpEntity<>(consumer), AppConsumer.class, consumer.getKongUuid());
        if (consumerUpsert.getStatusCode().isError()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL SERVER ERROR");
        }
    }

    private void syncConsumerJwtCredentialsToKong(AppConsumer consumer) {
        ResponseEntity<AppJwtCredential> consumerUpsert = kongClient.exchange("/consumers/{consumerUuid}/jwt/{credUuid}", HttpMethod.PUT, new HttpEntity<>(consumer.getJwtCredential()), AppJwtCredential.class, consumer.getKongUuid(), consumer.getJwtCredential().getUuid());
        if (consumerUpsert.getStatusCode().isError()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL SERVER ERROR");
        }
    }

}
