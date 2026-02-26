package org.mojave.core.settlement.domain.query;

import org.mojave.common.datatype.identifier.settlement.SettlementDefinitionId;
import org.mojave.component.jpa.routing.annotation.Read;
import org.mojave.core.settlement.contract.data.SettlementDefinitionData;
import org.mojave.core.settlement.contract.exception.SettlementDefinitionIdNotFoundException;
import org.mojave.core.settlement.contract.query.SettlementDefinitionQuery;
import org.mojave.core.settlement.domain.model.SettlementDefinition;
import org.mojave.core.settlement.domain.repository.SettlementDefinitionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class SettlementDefinitionQueryHandler implements SettlementDefinitionQuery {

    private final SettlementDefinitionRepository settlementDefinitionRepository;

    public SettlementDefinitionQueryHandler(
        final SettlementDefinitionRepository settlementDefinitionRepository) {

        Objects.requireNonNull(settlementDefinitionRepository);

        this.settlementDefinitionRepository = settlementDefinitionRepository;
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public SettlementDefinitionData get(final SettlementDefinitionId settlementDefinitionId) {

        return this.settlementDefinitionRepository
                   .findById(settlementDefinitionId)
                   .orElseThrow(() -> new SettlementDefinitionIdNotFoundException(
                       settlementDefinitionId))
                   .convert();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<SettlementDefinitionData> getAll() {

        return this.settlementDefinitionRepository
                   .findAll()
                   .stream()
                   .map(SettlementDefinition::convert)
                   .toList();
    }

}
