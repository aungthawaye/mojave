package org.mojave.mono.core.admin.api.query.participant.fsp;

import org.mojave.core.participant.contract.data.FspData;
import org.mojave.core.participant.contract.query.FspQuery;
import org.mojave.mono.core.admin.api.BaseApiController;
import org.mojave.scheme.rule.identifier.participant.FspId;
import org.mojave.scheme.rule.type.participant.FspCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
public class FspQueryController extends BaseApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FspQueryController.class);

    private final FspQuery fspQuery;

    public FspQueryController(final FspQuery fspQuery) {

        Objects.requireNonNull(fspQuery);

        this.fspQuery = fspQuery;
    }

    @GetMapping("/participant/fsps/get-by-id")
    public ResponseEntity<FspData> getById(@RequestParam final FspId fspId) {

        return this.respond(
            LOGGER,
            "FspQuery.getById",
            new FspQuery.GetByIdInput(fspId),
            () -> this.fspQuery.get(fspId));
    }

    @GetMapping("/participant/fsps/get-by-code")
    public ResponseEntity<FspData> getByCode(@RequestParam final FspCode fspCode) {

        return this.respond(
            LOGGER,
            "FspQuery.getByCode",
            new FspQuery.GetByCodeInput(fspCode),
            () -> this.fspQuery.get(fspCode));
    }

    @GetMapping("/participant/fsps/get-all")
    public ResponseEntity<List<FspData>> getAll() {

        return this.respond(
            LOGGER,
            "FspQuery.getAll",
            new FspQuery.GetAllInput(),
            this.fspQuery::getAll);
    }

}
