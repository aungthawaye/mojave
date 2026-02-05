package org.mojave.core.settlement.domain.command.record;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.settlement.contract.command.record.CompleteSettlementCommand;
import org.mojave.core.settlement.contract.exception.SettlementRecordNotFoundException;
import org.mojave.core.settlement.domain.repository.SettlementRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class CompleteSettlementCommandHandler implements CompleteSettlementCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        CompleteSettlementCommandHandler.class);

    private final SettlementRecordRepository settlementRecordRepository;

    public CompleteSettlementCommandHandler(final SettlementRecordRepository settlementRecordRepository) {

        Objects.requireNonNull(settlementRecordRepository);
        this.settlementRecordRepository = settlementRecordRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(final Input input) {

        LOGGER.info("CompleteSettlementCommand : input: ({})", ObjectLogger.log(input));

        var record = this.settlementRecordRepository
                         .findById(input.settlementRecordId())
                         .orElseThrow(() -> new SettlementRecordNotFoundException(
                             input.settlementRecordId()));

        record.markCompleted(input.completedAt());

        record = this.settlementRecordRepository.save(record);

        var output = new Output(record.getId());

        LOGGER.info("CompleteSettlementCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}
