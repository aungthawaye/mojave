package io.mojaloop.core.accounting.contract.query;

import io.mojaloop.core.accounting.contract.data.FlowDefinitionData;
import io.mojaloop.core.accounting.contract.exception.definition.FlowDefinitionNotFoundException;
import io.mojaloop.core.common.datatype.identifier.accounting.FlowDefinitionId;

import java.util.List;

public interface FlowDefinitionQuery {

    FlowDefinitionData get(FlowDefinitionId flowDefinitionId) throws FlowDefinitionNotFoundException;

    List<FlowDefinitionData> getByNameContains(String name);

    List<FlowDefinitionData> getAll();
}
