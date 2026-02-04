package org.mojave.core.settlement.domain.command.definition;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.settlement.contract.command.definition.CreateSettlementDefinitionCommand;
import org.mojave.core.settlement.contract.exception.FilterGroupIdNotFoundException;
import org.mojave.core.settlement.contract.exception.SettlementDefinitionAlreadyConfiguredException;
import org.mojave.core.settlement.contract.exception.SettlementDefinitionNameAlreadyExistsException;
import org.mojave.core.settlement.domain.model.SettlementDefinition;
import org.mojave.core.settlement.domain.repository.FilterGroupRepository;
import org.mojave.core.settlement.domain.repository.SettlementDefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class CreateSettlementDefinitionCommandHandler implements CreateSettlementDefinitionCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        CreateSettlementDefinitionCommandHandler.class);

    private final SettlementDefinitionRepository settlementDefinitionRepository;

    private final FilterGroupRepository filterGroupRepository;

    public CreateSettlementDefinitionCommandHandler(final SettlementDefinitionRepository settlementDefinitionRepository,
                                                    final FilterGroupRepository filterGroupRepository) {

        Objects.requireNonNull(settlementDefinitionRepository);
        Objects.requireNonNull(filterGroupRepository);

        this.settlementDefinitionRepository = settlementDefinitionRepository;
        this.filterGroupRepository = filterGroupRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(final Input input) {

        LOGGER.info("CreateSettlementDefinitionCommand : input: ({})", ObjectLogger.log(input));

        if (this.settlementDefinitionRepository
                .findOne(SettlementDefinitionRepository.Filters.withNameEquals(input.name()))
                .isPresent()) {
            throw new SettlementDefinitionNameAlreadyExistsException(input.name());
        }

        var duplicateSpec = SettlementDefinitionRepository.Filters
                                .withCurrency(input.currency())
                                .and(SettlementDefinitionRepository.Filters.withPayerFilterGroupId(
                                    input.payerFilterGroupId()))
                                .and(SettlementDefinitionRepository.Filters.withPayeeFilterGroupId(
                                    input.payeeFilterGroupId()));

        if (this.settlementDefinitionRepository.findOne(duplicateSpec).isPresent()) {
            throw new SettlementDefinitionAlreadyConfiguredException(
                input.currency(),
                input.payerFilterGroupId(), input.payeeFilterGroupId());
        }

        var payerGroup = this.filterGroupRepository
                             .findById(input.payerFilterGroupId())
                             .orElseThrow(() -> new FilterGroupIdNotFoundException(
                                 input.payerFilterGroupId()));
        var payeeGroup = this.filterGroupRepository
                             .findById(input.payeeFilterGroupId())
                             .orElseThrow(() -> new FilterGroupIdNotFoundException(
                                 input.payeeFilterGroupId()));

        var definition = new SettlementDefinition(
            input.name(), payerGroup, payeeGroup,
            input.currency(), input.startAt(), input.desiredProviderId());

        definition = this.settlementDefinitionRepository.save(definition);

        var output = new Output(definition.getId());

        LOGGER.info("CreateSettlementDefinitionCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}
