package org.mojave.core.settlement.domain.command.definition;

import org.mojave.common.datatype.enums.ActivationStatus;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.settlement.contract.command.definition.FindSettlementProviderCommand;
import org.mojave.core.settlement.contract.exception.SettlementDefinitionAmbiguousMatchException;
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

    public FindSettlementProviderCommandHandler(
        final SettlementDefinitionRepository settlementDefinitionRepository) {

        Objects.requireNonNull(settlementDefinitionRepository);
        this.settlementDefinitionRepository = settlementDefinitionRepository;
    }

    @Override
    public Output execute(final Input input) {

        LOGGER.info("FindSettlementProviderCommand : input: ({})", ObjectLogger.log(input));

        if (input.payerFspGroupId() == null || input.payeeFspGroupId() == null || input.currency() == null) {
            final var emptyOutput = new Output(null);

            LOGGER.info("FindSettlementProviderCommand : output : ({})", ObjectLogger.log(emptyOutput));

            return emptyOutput;
        }

        final var transactionAt = input.transactionAt() == null ? Instant.now() : input.transactionAt();

        final var spec = SettlementDefinitionRepository.Filters
                             .withActivationStatus(ActivationStatus.ACTIVE)
                             .and(SettlementDefinitionRepository.Filters.withCurrency(input.currency()))
                             .and(SettlementDefinitionRepository.Filters.withPayerFspGroupId(
                                 input.payerFspGroupId()))
                             .and(SettlementDefinitionRepository.Filters.withPayeeFspGroupId(
                                 input.payeeFspGroupId()));

        final var matchedDefinitions = this.settlementDefinitionRepository
                                        .findAll(spec)
                                        .stream()
                                        .filter(definition -> definition.matches(
                                            input.currency(),
                                            input.payerFspGroupId(),
                                            input.payeeFspGroupId(),
                                            transactionAt))
                                        .toList();

        if (matchedDefinitions.size() > 1) {
            throw new SettlementDefinitionAmbiguousMatchException(
                input.currency(),
                input.payerFspGroupId(),
                input.payeeFspGroupId(),
                transactionAt);
        }

        final var output = matchedDefinitions.isEmpty()
            ? new Output(null)
            : new Output(matchedDefinitions.getFirst().getDesiredProviderId());

        LOGGER.info("FindSettlementProviderCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}
