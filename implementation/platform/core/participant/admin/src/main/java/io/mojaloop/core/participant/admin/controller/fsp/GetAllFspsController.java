package io.mojaloop.core.participant.admin.controller.fsp;

import io.mojaloop.core.participant.contract.exception.FspIdNotFoundException;
import io.mojaloop.core.participant.contract.query.FspQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetAllFspsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetFspController.class.getName());

    private final FspQuery fspQuery;

    public GetAllFspsController(FspQuery fspQuery) {

        assert fspQuery != null;

        this.fspQuery = fspQuery;
    }

    @GetMapping("/fsps/get-all-fsps")
    public ResponseEntity<?> execute() throws FspIdNotFoundException {

        return ResponseEntity.ok(this.fspQuery.getAll());
    }

}
