package org.mojave.core.settlement.domain.command.definition;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.settlement.contract.command.definition.CreateSettlementModelDefinitionCommand;
import org.mojave.core.settlement.contract.exception.definition.SettlementModelDefinitionAlreadyConfiguredException;
import org.mojave.core.settlement.contract.exception.definition.SettlementModelDefinitionNameTakenException;
import org.mojave.core.settlement.contract.exception.model.SettlementModelNotFoundException;
import org.mojave.core.settlement.domain.model.definition.SettlementModelDefinition;
import org.mojave.core.settlement.domain.repository.SettlementModelDefinitionRepository;
import org.mojave.core.settlement.domain.repository.SettlementModelRepository;
import org.mojave.scheme.rule.identifier.settlement.SettlementModelDefinitionLineId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Objects;

@Service
public class CreateSettlementModelDefinitionCommandHandler
    implements CreateSettlementModelDefinitionCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        CreateSettlementModelDefinitionCommandHandler.class);

    private final SettlementModelDefinitionRepository settlementModelDefinitionRepository;

    private final SettlementModelRepository settlementModelRepository;

    public CreateSettlementModelDefinitionCommandHandler(
        final SettlementModelDefinitionRepository settlementModelDefinitionRepository,
        final SettlementModelRepository settlementModelRepository) {

        Objects.requireNonNull(settlementModelDefinitionRepository);
        Objects.requireNonNull(settlementModelRepository);

        this.settlementModelDefinitionRepository = settlementModelDefinitionRepository;
        this.settlementModelRepository = settlementModelRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(final Input input) {

        LOGGER.info(
            "CreateSettlementModelDefinitionCommand : input: ({})", ObjectLogger.log(input));

        final var withScenario = SettlementModelDefinitionRepository.Filters.withScenario(
            input.scenario());
        final var withCurrency = SettlementModelDefinitionRepository.Filters.withCurrency(
            input.currency());
        final var withFspGroupId = SettlementModelDefinitionRepository.Filters.withFspGroupId(
            input.fspGroupId());
        final var withTransactionPeriod = SettlementModelDefinitionRepository.Filters.withTransactionPeriod(
            input.transactionPeriod());

        if (this.settlementModelDefinitionRepository
                .findOne(
                    withScenario.and(withCurrency).and(withFspGroupId).and(withTransactionPeriod))
                .isPresent()) {
            throw new SettlementModelDefinitionAlreadyConfiguredException(
                input.scenario(), input.currency(), input.fspGroupId(), input.transactionPeriod());
        }

        if (this.settlementModelDefinitionRepository
                .findOne(SettlementModelDefinitionRepository.Filters.withNameEquals(input.name()))
                .isPresent()) {
            throw new SettlementModelDefinitionNameTakenException(input.name());
        }

        final var settlementModel = this.settlementModelRepository
                                        .findById(input.settlementModelId())
                                        .orElseThrow(() -> new SettlementModelNotFoundException(
                                            input.settlementModelId()));

        final var definition = new SettlementModelDefinition(
            input.scenario(), input.currency(),
            input.fspGroupId(), input.transactionPeriod(), input.name(), input.description(),
            settlementModel);

        final var lineIds = new ArrayList<SettlementModelDefinitionLineId>();

        for (final var line : input.settlementModelDefinitionLines()) {
            final var savedLine = definition.addSettlementModelDefinitionLine(
                line.step(),
                line.participant(), line.amountName(), line.currency(), line.direction(),
                line.description());
            lineIds.add(savedLine.getId());
        }

        final var saved = this.settlementModelDefinitionRepository.save(definition);
        final var output = new Output(saved.getId(), lineIds);

        LOGGER.info(
            "CreateSettlementModelDefinitionCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}
