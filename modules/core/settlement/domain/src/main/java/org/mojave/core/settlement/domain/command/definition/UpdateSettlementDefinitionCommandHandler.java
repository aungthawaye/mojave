package org.mojave.core.settlement.domain.command.definition;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.settlement.contract.command.definition.UpdateSettlementDefinitionCommand;
import org.mojave.core.settlement.contract.exception.SettlementDefinitionIdNotFoundException;
import org.mojave.core.settlement.contract.exception.SettlementDefinitionNameAlreadyExistsException;
import org.mojave.core.settlement.domain.repository.SettlementDefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class UpdateSettlementDefinitionCommandHandler implements UpdateSettlementDefinitionCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        UpdateSettlementDefinitionCommandHandler.class);

    private final SettlementDefinitionRepository settlementDefinitionRepository;

    public UpdateSettlementDefinitionCommandHandler(final SettlementDefinitionRepository settlementDefinitionRepository) {

        Objects.requireNonNull(settlementDefinitionRepository);

        this.settlementDefinitionRepository = settlementDefinitionRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(final Input input) {

        LOGGER.info("UpdateSettlementDefinitionCommand : input: ({})", ObjectLogger.log(input));

        var definition = this.settlementDefinitionRepository
                             .findById(input.settlementDefinitionId())
                             .orElseThrow(() -> new SettlementDefinitionIdNotFoundException(
                                 input.settlementDefinitionId()));

        if (input.name() != null) {
            var existing = this.settlementDefinitionRepository.findOne(
                SettlementDefinitionRepository.Filters.withNameEquals(input.name()));
            if (existing.isPresent() && !existing.get().getId().equals(definition.getId())) {
                throw new SettlementDefinitionNameAlreadyExistsException(input.name());
            }
        }

        definition.update(
            input.name(), input.payerFspGroupId(), input.payeeFspGroupId(), input.currency(),
            input.startAt(),
            input.desiredProviderId());

        definition = this.settlementDefinitionRepository.save(definition);

        var output = new Output(definition.getId());

        LOGGER.info("UpdateSettlementDefinitionCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}
