package io.mojaloop.mono.intercom.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

    @GetMapping("/**")
    public ResponseEntity<WelcomeController.Response> welcome() {

        return ResponseEntity.ok(
            new WelcomeController.Response("1.0", "Welcome to the Mojave Intercom API."));
    }

    public record Response(String version, String message) { }

}

