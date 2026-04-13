package org.mojave.mono.core.admin.api.query.participant.oracle;

import org.mojave.core.participant.contract.data.OracleData;
import org.mojave.core.participant.contract.query.OracleQuery;
import org.mojave.mono.core.admin.api.BaseApiController;
import org.mojave.scheme.rule.enums.participant.PartyIdType;
import org.mojave.scheme.rule.identifier.participant.OracleId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
public class OracleQueryController extends BaseApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OracleQueryController.class);

    private final OracleQuery oracleQuery;

    public OracleQueryController(final OracleQuery oracleQuery) {

        Objects.requireNonNull(oracleQuery);

        this.oracleQuery = oracleQuery;
    }

    @GetMapping("/participant/oracles/find-by-type")
    public ResponseEntity<OracleData> findByType(@RequestParam final PartyIdType type) {

        return this.respondOptional(
            LOGGER,
            "OracleQuery.findByType",
            new OracleQuery.FindByTypeInput(type),
            () -> this.oracleQuery.find(type));
    }

    @GetMapping("/participant/oracles/get-by-type")
    public ResponseEntity<OracleData> getByType(@RequestParam final PartyIdType type) {

        return this.respond(
            LOGGER,
            "OracleQuery.getByType",
            new OracleQuery.GetByTypeInput(type),
            () -> this.oracleQuery.get(type));
    }

    @GetMapping("/participant/oracles/get-by-id")
    public ResponseEntity<OracleData> getById(@RequestParam final OracleId oracleId) {

        return this.respond(
            LOGGER,
            "OracleQuery.getById",
            new OracleQuery.GetByIdInput(oracleId),
            () -> this.oracleQuery.get(oracleId));
    }

    @GetMapping("/participant/oracles/get-all")
    public ResponseEntity<List<OracleData>> getAll() {

        return this.respond(
            LOGGER,
            "OracleQuery.getAll",
            new OracleQuery.GetAllInput(),
            this.oracleQuery::getAll);
    }

}
