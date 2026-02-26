package org.mojave.core.settlement.domain.command.definition;

import org.mojave.common.datatype.enums.ActivationStatus;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.participant.FspGroupId;
import org.mojave.common.datatype.identifier.settlement.SettlementDefinitionId;
import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.settlement.contract.command.definition.CreateSettlementDefinitionCommand;
import org.mojave.core.settlement.contract.exception.SettlementDefinitionInvalidTimeRangeException;
import org.mojave.core.settlement.contract.exception.SettlementDefinitionNameAlreadyExistsException;
import org.mojave.core.settlement.contract.exception.SettlementDefinitionTimeRangeOverlappingException;
import org.mojave.core.settlement.domain.model.SettlementDefinition;
import org.mojave.core.settlement.domain.repository.SettlementDefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;

@Service
public class CreateSettlementDefinitionCommandHandler implements CreateSettlementDefinitionCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        CreateSettlementDefinitionCommandHandler.class);

    private final SettlementDefinitionRepository settlementDefinitionRepository;

    public CreateSettlementDefinitionCommandHandler(
        final SettlementDefinitionRepository settlementDefinitionRepository) {

        Objects.requireNonNull(settlementDefinitionRepository);

        this.settlementDefinitionRepository = settlementDefinitionRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(final Input input) {

        LOGGER.info("CreateSettlementDefinitionCommand : input: ({})", ObjectLogger.log(input));

        this.validateTimeRange(input.startAt(), input.endAt());

        if (this.settlementDefinitionRepository
                .findOne(SettlementDefinitionRepository.Filters.withNameEquals(input.name()))
                .isPresent()) {
            throw new SettlementDefinitionNameAlreadyExistsException(input.name());
        }

        this.validateNoActiveOverlap(
            input.payerFspGroupId(), input.payeeFspGroupId(), input.currency(), input.startAt(),
            input.endAt(), null);

        var definition = new SettlementDefinition(
            input.name(),
            input.payerFspGroupId(),
            input.payeeFspGroupId(),
            input.currency(),
            input.startAt(),
            input.endAt(),
            input.desiredProviderId());

        definition = this.settlementDefinitionRepository.save(definition);

        final var output = new Output(definition.getId());

        LOGGER.info("CreateSettlementDefinitionCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

    private void validateNoActiveOverlap(final FspGroupId payerFspGroupId,
                                         final FspGroupId payeeFspGroupId,
                                         final Currency currency,
                                         final Instant startAt,
                                         final Instant endAt,
                                         final SettlementDefinitionId excludeId) {

        var spec = SettlementDefinitionRepository.Filters
                       .withActivationStatus(ActivationStatus.ACTIVE)
                       .and(SettlementDefinitionRepository.Filters.withPayerFspGroupId(payerFspGroupId))
                       .and(SettlementDefinitionRepository.Filters.withPayeeFspGroupId(payeeFspGroupId))
                       .and(SettlementDefinitionRepository.Filters.withCurrency(currency))
                       .and(SettlementDefinitionRepository.Filters.withTimeRangeOverlapping(startAt, endAt));

        if (excludeId != null) {
            spec = spec.and(SettlementDefinitionRepository.Filters.withIdNotEquals(excludeId));
        }

        if (!this.settlementDefinitionRepository.findAll(spec).isEmpty()) {
            throw new SettlementDefinitionTimeRangeOverlappingException(
                currency, payerFspGroupId, payeeFspGroupId);
        }
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
