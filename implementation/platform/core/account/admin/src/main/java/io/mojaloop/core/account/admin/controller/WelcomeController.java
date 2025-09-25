package io.mojaloop.core.account.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

    @GetMapping("/**")
    public ResponseEntity<Response> welcome() {

        return ResponseEntity.ok(new Response("1.0", "Welcome to the Account Admin API."));
    }

    public record Response(String version, String message) { }

}
