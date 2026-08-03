package org.mojave.core.settlement.domain.query;

import org.mojave.component.jpa.routing.annotation.Read;
import org.mojave.core.settlement.contract.data.SettlementModelDefinitionData;
import org.mojave.core.settlement.contract.exception.definition.SettlementModelDefinitionNotConfiguredException;
import org.mojave.core.settlement.contract.exception.definition.SettlementModelDefinitionNotFoundException;
import org.mojave.core.settlement.contract.query.SettlementModelDefinitionQuery;
import org.mojave.core.settlement.domain.model.definition.SettlementModelDefinition;
import org.mojave.core.settlement.domain.repository.SettlementModelDefinitionRepository;
import org.mojave.scheme.rule.enums.ActivationStatus;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.enums.TerminationStatus;
import org.mojave.scheme.rule.enums.settlement.TransactionPeriod;
import org.mojave.scheme.rule.identifier.participant.FspGroupId;
import org.mojave.scheme.rule.identifier.settlement.SettlementDefinitionId;
import org.mojave.scheme.rule.scenario.ScenarioType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class SettlementModelDefinitionQueryHandler implements SettlementModelDefinitionQuery {

    private final SettlementModelDefinitionRepository settlementModelDefinitionRepository;

    public SettlementModelDefinitionQueryHandler(
        final SettlementModelDefinitionRepository settlementModelDefinitionRepository) {

        Objects.requireNonNull(settlementModelDefinitionRepository);

        this.settlementModelDefinitionRepository = settlementModelDefinitionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    @Read
    public SettlementModelDefinitionData get(
        final SettlementDefinitionId settlementModelDefinitionId)
        throws SettlementModelDefinitionNotFoundException {

        return this.settlementModelDefinitionRepository
                   .findById(settlementModelDefinitionId)
                   .orElseThrow(() -> new SettlementModelDefinitionNotFoundException(
                       settlementModelDefinitionId))
                   .convert();
    }

    @Override
    @Transactional(readOnly = true)
    @Read
    public List<SettlementModelDefinitionData> getAll() {

        return this.settlementModelDefinitionRepository
                   .findAll()
                   .stream()
                   .map(SettlementModelDefinition::convert)
                   .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Read
    public List<SettlementModelDefinitionData> getByNameContains(final String name) {

        return this.settlementModelDefinitionRepository
                   .findAll(SettlementModelDefinitionRepository.Filters.withNameContains(name))
                   .stream()
                   .map(SettlementModelDefinition::convert)
                   .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Read
    public SettlementModelDefinitionData get(final ScenarioType scenario, final Currency currency,
                                             final FspGroupId fspGroupId,
                                             final TransactionPeriod transactionPeriod)
        throws SettlementModelDefinitionNotFoundException {

        final var filter = SettlementModelDefinitionRepository.Filters
                               .withScenario(scenario)
                               .and(SettlementModelDefinitionRepository.Filters.withCurrency(
                                   currency))
                               .and(SettlementModelDefinitionRepository.Filters.withFspGroupId(
                                   fspGroupId))
                               .and(
                                   SettlementModelDefinitionRepository.Filters.withTransactionPeriod(
                                       transactionPeriod))
                               .and(
                                   SettlementModelDefinitionRepository.Filters.withActivationStatus(
                                       ActivationStatus.ACTIVE))
                               .and(
                                   SettlementModelDefinitionRepository.Filters.withTerminationStatus(
                                       TerminationStatus.ALIVE));

        return this.settlementModelDefinitionRepository
                   .findOne(filter)
                   .orElseThrow(
                       () -> new SettlementModelDefinitionNotConfiguredException(
                           scenario, currency,
                           fspGroupId, transactionPeriod))
                   .convert();
    }

}
