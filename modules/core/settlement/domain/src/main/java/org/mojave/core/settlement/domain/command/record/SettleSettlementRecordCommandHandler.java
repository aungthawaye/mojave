package org.mojave.core.settlement.domain.command.record;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.core.settlement.contract.command.record.SettleSettlementRecordCommand;
import org.mojave.core.settlement.contract.exception.record.SettlementRecordNotFoundException;
import org.mojave.core.settlement.domain.repository.SettlementRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class SettleSettlementRecordCommandHandler implements SettleSettlementRecordCommand {

    private final SettlementRecordRepository settlementRecordRepository;

    public SettleSettlementRecordCommandHandler(
        final SettlementRecordRepository settlementRecordRepository) {

        Objects.requireNonNull(settlementRecordRepository);

        this.settlementRecordRepository = settlementRecordRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(final Input input) {

        final var record = this.settlementRecordRepository
                               .findById(input.settlementRecordId())
                               .orElseThrow(() -> new SettlementRecordNotFoundException(
                                   input.settlementRecordId()));

        record.settle();

        return new Output(record.getId());
    }

}
