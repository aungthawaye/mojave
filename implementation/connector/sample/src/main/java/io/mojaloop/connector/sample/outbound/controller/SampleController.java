package io.mojaloop.connector.sample.outbound.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    @GetMapping("/sample-api")
    public ResponseEntity<SampleController.Response> welcome() {

        return ResponseEntity.ok(new SampleController.Response("key", "value"));
    }

    public record Response(String key, String value) { }

}
