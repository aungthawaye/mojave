package org.mojave.mono.core.admin.api.query.participant.fsp;

import org.mojave.core.participant.contract.data.FspGroupData;
import org.mojave.core.participant.contract.query.FspGroupQuery;
import org.mojave.mono.core.admin.api.BaseApiController;
import org.mojave.scheme.rule.identifier.participant.FspGroupId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
public class FspGroupQueryController extends BaseApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FspGroupQueryController.class);

    private final FspGroupQuery fspGroupQuery;

    public FspGroupQueryController(final FspGroupQuery fspGroupQuery) {

        Objects.requireNonNull(fspGroupQuery);

        this.fspGroupQuery = fspGroupQuery;
    }

    @GetMapping("/participant/fsp-groups/get-by-id")
    public ResponseEntity<FspGroupData> getById(@RequestParam final FspGroupId fspGroupId) {

        return this.respond(
            LOGGER,
            "FspGroupQuery.getById",
            new FspGroupQuery.GetByIdInput(fspGroupId),
            () -> this.fspGroupQuery.get(fspGroupId));
    }

    @GetMapping("/participant/fsp-groups/get-all")
    public ResponseEntity<List<FspGroupData>> getAll() {

        return this.respond(
            LOGGER,
            "FspGroupQuery.getAll",
            new FspGroupQuery.GetAllInput(),
            this.fspGroupQuery::getAll);
    }

}
