package org.mojave.mono.core.admin.api.query.accounting.chart;

import org.mojave.core.accounting.contract.data.CoaData;
import org.mojave.core.accounting.contract.query.CoaQuery;
import org.mojave.mono.core.admin.api.BaseApiController;
import org.mojave.scheme.rule.identifier.accounting.CoaId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
public class CoaQueryController extends BaseApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoaQueryController.class);

    private final CoaQuery coaQuery;

    public CoaQueryController(final CoaQuery coaQuery) {

        Objects.requireNonNull(coaQuery);

        this.coaQuery = coaQuery;
    }

    @GetMapping("/accounting/charts/get-by-id")
    public ResponseEntity<CoaData> getById(@RequestParam final CoaId coaId) {

        return this.respond(
            LOGGER,
            "CoaQuery.getById",
            new CoaQuery.GetByIdInput(coaId),
            () -> this.coaQuery.get(coaId));
    }

    @GetMapping("/accounting/charts/get-all")
    public ResponseEntity<List<CoaData>> getAll() {

        return this.respond(
            LOGGER,
            "CoaQuery.getAll",
            new CoaQuery.GetAllInput(),
            this.coaQuery::getAll);
    }

    @GetMapping("/accounting/charts/get-by-name-contains")
    public ResponseEntity<List<CoaData>> getByNameContains(@RequestParam final String name) {

        return this.respond(
            LOGGER,
            "CoaQuery.getByNameContains",
            new CoaQuery.GetByNameContainsInput(name),
            () -> this.coaQuery.getByNameContains(name));
    }

}
