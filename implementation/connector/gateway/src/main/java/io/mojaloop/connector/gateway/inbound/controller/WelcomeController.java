package io.mojaloop.connector.gateway.inbound.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WelcomeController.class.getName());

    @GetMapping("/**")
    public ResponseEntity<WelcomeController.Response> welcome() {

        LOGGER.debug("Received GET /** (welcome)");
        return ResponseEntity.ok(new Response("1.0", "Welcome to the Mojaloop Connector Inbound Service."));
    }

    public record Response(String version, String message) { }

}
