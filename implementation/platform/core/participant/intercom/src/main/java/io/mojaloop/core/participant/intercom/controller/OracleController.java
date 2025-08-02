package io.mojaloop.core.participant.intercom.controller;

import io.mojaloop.core.participant.contract.data.OracleData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.logging.Logger;

@RestController
public class OracleController {

    private static final Logger LOGGER = Logger.getLogger(OracleController.class.getName());

    @GetMapping("/oracles")
    public ResponseEntity<List<OracleData>> getFsps() {

        return ResponseEntity.ok(List.of());
    }

}
