package com.example.kongjavajwttest.controller;

import com.example.kongjavajwttest.manager.AppResourceManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@Log4j2
public class DemoController {

    @Autowired
    private AppResourceManager appResourceManager;

    @RequestMapping(value = "{userId}/login", method = GET)
    public ResponseEntity<String> login(@PathVariable long userId, @RequestHeader("Authorization") String basicAuth, @RequestHeader HttpHeaders headers) {
        log.info(headers.toString());
        String jwt = appResourceManager.login(userId, basicAuth);
        return new ResponseEntity<>(jwt, HttpStatus.OK);
    }

    @RequestMapping(value = "{userId}/resource", method = GET)
    public ResponseEntity<String> getResource(@PathVariable long userId, @RequestHeader("Authorization") String jwtAuth, @RequestHeader HttpHeaders headers) {
        log.info(headers.toString());
        String resource = appResourceManager.getResource(userId, jwtAuth);
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }



}
