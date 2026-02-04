package org.mojave.core.settlement.domain.command.definition;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.settlement.contract.command.definition.RemoveSettlementDefinitionCommand;
import org.mojave.core.settlement.contract.exception.SettlementDefinitionIdNotFoundException;
import org.mojave.core.settlement.domain.repository.SettlementDefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class RemoveSettlementDefinitionCommandHandler implements RemoveSettlementDefinitionCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        RemoveSettlementDefinitionCommandHandler.class);

    private final SettlementDefinitionRepository settlementDefinitionRepository;

    public RemoveSettlementDefinitionCommandHandler(final SettlementDefinitionRepository settlementDefinitionRepository) {

        Objects.requireNonNull(settlementDefinitionRepository);
        this.settlementDefinitionRepository = settlementDefinitionRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(final Input input) {

        LOGGER.info("RemoveSettlementDefinitionCommand : input: ({})", ObjectLogger.log(input));

        var definition = this.settlementDefinitionRepository
                             .findById(input.settlementDefinitionId())
                             .orElseThrow(() -> new SettlementDefinitionIdNotFoundException(
                                 input.settlementDefinitionId()));

        this.settlementDefinitionRepository.delete(definition);

        var output = new Output(definition.getId());

        LOGGER.info("RemoveSettlementDefinitionCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}
