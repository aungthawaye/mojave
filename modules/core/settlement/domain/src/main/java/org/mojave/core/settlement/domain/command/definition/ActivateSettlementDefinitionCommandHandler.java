package org.mojave.core.settlement.domain.command.definition;

import org.mojave.common.datatype.enums.ActivationStatus;
import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.settlement.contract.command.definition.ActivateSettlementDefinitionCommand;
import org.mojave.core.settlement.contract.exception.SettlementDefinitionIdNotFoundException;
import org.mojave.core.settlement.contract.exception.SettlementDefinitionInvalidTimeRangeException;
import org.mojave.core.settlement.contract.exception.SettlementDefinitionTimeRangeOverlappingException;
import org.mojave.core.settlement.domain.repository.SettlementDefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;

@Service
public class ActivateSettlementDefinitionCommandHandler
    implements ActivateSettlementDefinitionCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        ActivateSettlementDefinitionCommandHandler.class);

    private final SettlementDefinitionRepository settlementDefinitionRepository;

    public ActivateSettlementDefinitionCommandHandler(
        final SettlementDefinitionRepository settlementDefinitionRepository) {

        Objects.requireNonNull(settlementDefinitionRepository);
        this.settlementDefinitionRepository = settlementDefinitionRepository;
    }

    @Transactional
    @Write
    @Override
    public Output execute(final Input input) {

        LOGGER.info("ActivateSettlementDefinitionCommand : input: ({})", ObjectLogger.log(input));

        final var definition = this.settlementDefinitionRepository
                                   .findById(input.settlementDefinitionId())
                                   .orElseThrow(() -> new SettlementDefinitionIdNotFoundException(
                                       input.settlementDefinitionId()));

        this.validateTimeRange(definition.getStartAt(), definition.getEndAt());

        final var overlapSpec = SettlementDefinitionRepository.Filters
                                    .withActivationStatus(ActivationStatus.ACTIVE)
                                    .and(SettlementDefinitionRepository.Filters.withPayerFspGroupId(
                                        definition.getPayerFspGroupId()))
                                    .and(SettlementDefinitionRepository.Filters.withPayeeFspGroupId(
                                        definition.getPayeeFspGroupId()))
                                    .and(SettlementDefinitionRepository.Filters.withCurrency(
                                        definition.getCurrency()))
                                    .and(SettlementDefinitionRepository.Filters.withTimeRangeOverlapping(
                                        definition.getStartAt(), definition.getEndAt()))
                                    .and(SettlementDefinitionRepository.Filters.withIdNotEquals(
                                        definition.getId()));

        if (!this.settlementDefinitionRepository.findAll(overlapSpec).isEmpty()) {
            throw new SettlementDefinitionTimeRangeOverlappingException(
                definition.getCurrency(),
                definition.getPayerFspGroupId(),
                definition.getPayeeFspGroupId());
        }

        definition.activate();
        this.settlementDefinitionRepository.save(definition);

        final var output = new Output(definition.getId());

        LOGGER.info(
            "ActivateSettlementDefinitionCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

    private void validateTimeRange(final Instant startAt,
                                   final Instant endAt) {

        if (startAt == null) {
            throw new SettlementDefinitionInvalidTimeRangeException(startAt, endAt);
        }

        if (endAt != null && !startAt.isBefore(endAt)) {
            throw new SettlementDefinitionInvalidTimeRangeException(startAt, endAt);
        }
    }

}
