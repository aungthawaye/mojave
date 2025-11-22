package io.mojaloop.mono.admin.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

    @GetMapping("/**")
    public ResponseEntity<io.mojaloop.core.participant.admin.controller.api.WelcomeController.Response> welcome() {

        return ResponseEntity.ok(
            new io.mojaloop.core.participant.admin.controller.api.WelcomeController.Response(
                "1.0",
                "Welcome to the Mojave Admin API."));
    }

    public record Response(String version, String message) { }

}

