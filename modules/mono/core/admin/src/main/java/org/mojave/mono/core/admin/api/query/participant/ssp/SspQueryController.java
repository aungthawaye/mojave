package org.mojave.mono.core.admin.api.query.participant.ssp;

import org.mojave.core.participant.contract.data.SspData;
import org.mojave.core.participant.contract.query.SspQuery;
import org.mojave.mono.core.admin.api.BaseApiController;
import org.mojave.scheme.rule.identifier.participant.SspId;
import org.mojave.scheme.rule.type.participant.SspCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
public class SspQueryController extends BaseApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SspQueryController.class);

    private final SspQuery sspQuery;

    public SspQueryController(final SspQuery sspQuery) {

        Objects.requireNonNull(sspQuery);

        this.sspQuery = sspQuery;
    }

    @GetMapping("/participant/ssps/get-by-id")
    public ResponseEntity<SspData> getById(@RequestParam final SspId sspId) {

        return this.respond(
            LOGGER,
            "SspQuery.getById",
            new SspQuery.GetByIdInput(sspId),
            () -> this.sspQuery.get(sspId));
    }

    @GetMapping("/participant/ssps/get-by-code")
    public ResponseEntity<SspData> getByCode(@RequestParam final SspCode sspCode) {

        return this.respond(
            LOGGER,
            "SspQuery.getByCode",
            new SspQuery.GetByCodeInput(sspCode),
            () -> this.sspQuery.get(sspCode));
    }

    @GetMapping("/participant/ssps/get-all")
    public ResponseEntity<List<SspData>> getAll() {

        return this.respond(
            LOGGER,
            "SspQuery.getAll",
            new SspQuery.GetAllInput(),
            this.sspQuery::getAll);
    }

}
