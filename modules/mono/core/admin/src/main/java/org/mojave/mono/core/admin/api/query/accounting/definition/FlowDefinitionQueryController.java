package org.mojave.mono.core.admin.api.query.accounting.definition;

import org.mojave.core.accounting.contract.data.FlowDefinitionData;
import org.mojave.core.accounting.contract.query.FlowDefinitionQuery;
import org.mojave.mono.core.admin.api.BaseApiController;
import org.mojave.scheme.rule.identifier.accounting.FlowDefinitionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
public class FlowDefinitionQueryController extends BaseApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlowDefinitionQueryController.class);

    private final FlowDefinitionQuery flowDefinitionQuery;

    public FlowDefinitionQueryController(final FlowDefinitionQuery flowDefinitionQuery) {

        Objects.requireNonNull(flowDefinitionQuery);

        this.flowDefinitionQuery = flowDefinitionQuery;
    }

    @GetMapping("/accounting/definition/get-by-id")
    public ResponseEntity<FlowDefinitionData> getById(
        @RequestParam final FlowDefinitionId flowDefinitionId) {

        return this.respond(
            LOGGER,
            "FlowDefinitionQuery.getById",
            new FlowDefinitionQuery.GetByIdInput(flowDefinitionId),
            () -> this.flowDefinitionQuery.get(flowDefinitionId));
    }

    @GetMapping("/accounting/definition/get-all")
    public ResponseEntity<List<FlowDefinitionData>> getAll() {

        return this.respond(
            LOGGER,
            "FlowDefinitionQuery.getAll",
            new FlowDefinitionQuery.GetAllInput(),
            this.flowDefinitionQuery::getAll);
    }

    @GetMapping("/accounting/definition/get-by-name-contains")
    public ResponseEntity<List<FlowDefinitionData>> getByNameContains(
        @RequestParam final String name) {

        return this.respond(
            LOGGER,
            "FlowDefinitionQuery.getByNameContains",
            new FlowDefinitionQuery.GetByNameContainsInput(name),
            () -> this.flowDefinitionQuery.getByNameContains(name));
    }

}
