package org.mojave.core.settlement.domain.command.record;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.core.settlement.contract.command.record.CreateSettlementRecordCommand;
import org.mojave.core.settlement.contract.exception.definition.SettlementModelDefinitionNotFoundException;
import org.mojave.core.settlement.contract.exception.record.SettlementRecordAlreadyExistsException;
import org.mojave.core.settlement.contract.exception.window.SettlementWindowNotFoundException;
import org.mojave.core.settlement.domain.model.SettlementWindow;
import org.mojave.core.settlement.domain.model.record.SettlementRecord;
import org.mojave.core.settlement.domain.repository.SettlementModelDefinitionRepository;
import org.mojave.core.settlement.domain.repository.SettlementRecordRepository;
import org.mojave.core.settlement.domain.repository.SettlementWindowRepository;
import org.mojave.scheme.rule.identifier.settlement.SettlementAmountId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Objects;

@Service
public class CreateSettlementRecordCommandHandler implements CreateSettlementRecordCommand {

    private final SettlementModelDefinitionRepository settlementModelDefinitionRepository;

    private final SettlementWindowRepository settlementWindowRepository;

    private final SettlementRecordRepository settlementRecordRepository;

    public CreateSettlementRecordCommandHandler(
        final SettlementModelDefinitionRepository settlementModelDefinitionRepository,
        final SettlementWindowRepository settlementWindowRepository,
        final SettlementRecordRepository settlementRecordRepository) {

        Objects.requireNonNull(settlementModelDefinitionRepository);
        Objects.requireNonNull(settlementWindowRepository);
        Objects.requireNonNull(settlementRecordRepository);

        this.settlementModelDefinitionRepository = settlementModelDefinitionRepository;
        this.settlementWindowRepository = settlementWindowRepository;
        this.settlementRecordRepository = settlementRecordRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(final Input input) {

        if (this.settlementRecordRepository
                .findOne(
                    SettlementRecordRepository.Filters.withTransactionId(input.transactionId()))
                .isPresent()) {
            throw new SettlementRecordAlreadyExistsException(input.transactionId());
        }

        final var definition = this.settlementModelDefinitionRepository
                                   .findById(input.settlementModelDefinitionId())
                                   .orElseThrow(
                                       () -> new SettlementModelDefinitionNotFoundException(
                                           input.settlementModelDefinitionId()));

        final SettlementWindow settlementWindow;

        if (input.settlementWindowId() == null) {
            settlementWindow = null;
        } else {
            settlementWindow = this.settlementWindowRepository
                                   .findById(input.settlementWindowId())
                                   .orElseThrow(() -> new SettlementWindowNotFoundException(
                                       input.settlementWindowId()));
        }

        final var record = new SettlementRecord(
            input.transactionId(), input.scenario(), input.currency(), definition,
            settlementWindow);
        final var settlementAmountIds = new ArrayList<SettlementAmountId>();

        for (final var amount : input.settlementAmounts()) {
            final var savedAmount = record.addSettlementAmount(
                amount.participant(), amount.fspId(),
                amount.amountName(), amount.amount(), amount.currency(), amount.direction());
            settlementAmountIds.add(savedAmount.getId());
        }

        final var saved = this.settlementRecordRepository.save(record);

        return new Output(saved.getId(), settlementAmountIds);
    }

}
