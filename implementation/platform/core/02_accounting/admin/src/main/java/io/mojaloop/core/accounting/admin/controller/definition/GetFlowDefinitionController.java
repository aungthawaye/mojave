package io.mojaloop.core.accounting.admin.controller.definition;

import io.mojaloop.core.accounting.contract.data.FlowDefinitionData;
import io.mojaloop.core.accounting.contract.exception.definition.FlowDefinitionNotFoundException;
import io.mojaloop.core.accounting.contract.query.FlowDefinitionQuery;
import io.mojaloop.core.common.datatype.identifier.accounting.FlowDefinitionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GetFlowDefinitionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetFlowDefinitionController.class);

    private final FlowDefinitionQuery flowDefinitionQuery;

    public GetFlowDefinitionController(final FlowDefinitionQuery flowDefinitionQuery) {

        assert flowDefinitionQuery != null;

        this.flowDefinitionQuery = flowDefinitionQuery;
    }

    @GetMapping("/definitions/flows/get-all-definitions")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<FlowDefinitionData> allFlowDefinitions() {

        return this.flowDefinitionQuery.getAll();
    }

    @GetMapping("/definitions/flows/get-by-id")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public FlowDefinitionData byId(@RequestParam final Long flowDefinitionId) throws FlowDefinitionNotFoundException {

        return this.flowDefinitionQuery.get(new FlowDefinitionId(flowDefinitionId));
    }

    @GetMapping("/definitions/flows/get-by-name-contains")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<FlowDefinitionData> byNameContains(@RequestParam final String name) {

        return this.flowDefinitionQuery.getByNameContains(name);
    }
}
