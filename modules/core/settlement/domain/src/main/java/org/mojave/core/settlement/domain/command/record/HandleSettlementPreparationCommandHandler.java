package org.mojave.core.settlement.domain.command.record;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.settlement.contract.command.record.HandleSettlementPreparationCommand;
import org.mojave.core.settlement.contract.exception.SettlementRecordNotFoundException;
import org.mojave.core.settlement.domain.repository.SettlementRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class HandleSettlementPreparationCommandHandler
    implements HandleSettlementPreparationCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        HandleSettlementPreparationCommandHandler.class);

    private final SettlementRecordRepository settlementRecordRepository;

    public HandleSettlementPreparationCommandHandler(final SettlementRecordRepository settlementRecordRepository) {

        Objects.requireNonNull(settlementRecordRepository);
        this.settlementRecordRepository = settlementRecordRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(final Input input) {

        LOGGER.info("HandleSettlementPreparationCommand : input: ({})", ObjectLogger.log(input));

        var record = this.settlementRecordRepository
                         .findById(input.settlementRecordId())
                         .orElseThrow(() -> new SettlementRecordNotFoundException(
                             input.settlementRecordId()));

        record.markPrepared(input.settlementId(), input.settlementBatchId(), input.preparedAt());

        record = this.settlementRecordRepository.save(record);

        var output = new Output(record.getId());

        LOGGER.info("HandleSettlementPreparationCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}
