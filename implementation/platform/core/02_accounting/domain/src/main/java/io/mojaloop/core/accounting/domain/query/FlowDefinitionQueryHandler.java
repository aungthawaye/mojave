package io.mojaloop.core.accounting.domain.query;

import io.mojaloop.component.jpa.routing.annotation.Read;
import io.mojaloop.core.accounting.contract.data.FlowDefinitionData;
import io.mojaloop.core.accounting.contract.exception.definition.FlowDefinitionNotFoundException;
import io.mojaloop.core.accounting.contract.query.FlowDefinitionQuery;
import io.mojaloop.core.accounting.domain.model.FlowDefinition;
import io.mojaloop.core.accounting.domain.repository.FlowDefinitionRepository;
import io.mojaloop.core.common.datatype.identifier.accounting.FlowDefinitionId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FlowDefinitionQueryHandler implements FlowDefinitionQuery {

    private final FlowDefinitionRepository flowDefinitionRepository;

    public FlowDefinitionQueryHandler(final FlowDefinitionRepository flowDefinitionRepository) {

        assert flowDefinitionRepository != null;

        this.flowDefinitionRepository = flowDefinitionRepository;
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public FlowDefinitionData get(final FlowDefinitionId flowDefinitionId) throws FlowDefinitionNotFoundException {

        return this.flowDefinitionRepository.findById(flowDefinitionId).orElseThrow(
            () -> new FlowDefinitionNotFoundException(flowDefinitionId)).convert();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<FlowDefinitionData> getAll() {

        return this.flowDefinitionRepository.findAll().stream().map(FlowDefinition::convert).toList();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<FlowDefinitionData> getByNameContains(final String name) {

        return this.flowDefinitionRepository.findAll(FlowDefinitionRepository.Filters.withNameContains(name)).stream().map(
            FlowDefinition::convert).toList();
    }

}
