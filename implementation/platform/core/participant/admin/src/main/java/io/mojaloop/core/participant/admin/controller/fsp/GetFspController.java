package io.mojaloop.core.participant.admin.controller.fsp;

import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.participant.contract.exception.FspIdNotFoundException;
import io.mojaloop.core.participant.contract.query.FspQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetFspController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetFspController.class.getName());

    private final FspQuery fspQuery;

    public GetFspController(FspQuery fspQuery) {

        assert fspQuery != null;

        this.fspQuery = fspQuery;
    }

    @GetMapping("/fsps/get-fsp")
    public ResponseEntity<?> execute(@RequestParam Long fspId) throws FspIdNotFoundException {

        return ResponseEntity.ok(this.fspQuery.get(new FspId(fspId)));
    }

}
