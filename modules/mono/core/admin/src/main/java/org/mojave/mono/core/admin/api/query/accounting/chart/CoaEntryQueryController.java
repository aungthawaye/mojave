package org.mojave.mono.core.admin.api.query.accounting.chart;

import org.mojave.core.accounting.contract.data.CoaEntryData;
import org.mojave.core.accounting.contract.query.CoaEntryQuery;
import org.mojave.mono.core.admin.api.BaseApiController;
import org.mojave.scheme.rule.identifier.accounting.CoaEntryId;
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
public class CoaEntryQueryController extends BaseApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoaEntryQueryController.class);

    private final CoaEntryQuery coaEntryQuery;

    public CoaEntryQueryController(final CoaEntryQuery coaEntryQuery) {

        Objects.requireNonNull(coaEntryQuery);

        this.coaEntryQuery = coaEntryQuery;
    }

    @GetMapping("/accounting/chart/get-entry-by-id")
    public ResponseEntity<CoaEntryData> getById(@RequestParam final CoaEntryId coaEntryId) {

        return this.respond(
            LOGGER,
            "CoaEntryQuery.getById",
            new CoaEntryQuery.GetByIdInput(coaEntryId),
            () -> this.coaEntryQuery.get(coaEntryId));
    }

    @GetMapping("/accounting/chart/get-entries-by-coa-id")
    public ResponseEntity<List<CoaEntryData>> getByCoaId(@RequestParam final CoaId coaId) {

        return this.respond(
            LOGGER,
            "CoaEntryQuery.getByCoaId",
            new CoaEntryQuery.GetByCoaIdInput(coaId),
            () -> this.coaEntryQuery.get(coaId));
    }

    @GetMapping("/accounting/chart/get-entries-by-category")
    public ResponseEntity<List<CoaEntryData>> getByCategory(@RequestParam final String category) {

        return this.respond(
            LOGGER,
            "CoaEntryQuery.getByCategory",
            new CoaEntryQuery.GetByCategoryInput(category),
            () -> this.coaEntryQuery.get(category));
    }

    @GetMapping("/accounting/chart/get-all-entries")
    public ResponseEntity<List<CoaEntryData>> getAll() {

        return this.respond(
            LOGGER,
            "CoaEntryQuery.getAll",
            new CoaEntryQuery.GetAllInput(),
            this.coaEntryQuery::getAll);
    }

}
