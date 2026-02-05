package org.mojave.core.settlement.domain.command.definition;

import org.mojave.common.datatype.enums.ActivationStatus;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.settlement.contract.command.definition.FindSettlementProviderCommand;
import org.mojave.core.settlement.domain.repository.SettlementDefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;

@Service
public class FindSettlementProviderCommandHandler implements FindSettlementProviderCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        FindSettlementProviderCommandHandler.class);

    private final SettlementDefinitionRepository settlementDefinitionRepository;

    public FindSettlementProviderCommandHandler(final SettlementDefinitionRepository settlementDefinitionRepository) {

        Objects.requireNonNull(settlementDefinitionRepository);
        this.settlementDefinitionRepository = settlementDefinitionRepository;
    }

    @Override
    public Output execute(final Input input) {

        LOGGER.info("FindSettlementProviderCommand : input: ({})", ObjectLogger.log(input));

        var spec = SettlementDefinitionRepository.Filters
                       .withCurrency(input.currency())
                       .and(SettlementDefinitionRepository.Filters.withActivationStatus(
                           ActivationStatus.ACTIVE));

        var now = Instant.now();

        var output = new Output(null);

        LOGGER.info("FindSettlementProviderCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}
