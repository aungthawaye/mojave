package org.mojave.core.settlement.domain.command.record;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.settlement.contract.command.definition.FindSettlementProviderCommand;
import org.mojave.core.settlement.contract.command.record.SendSettlementRequestCommand;
import org.mojave.core.settlement.contract.exception.SettlementProviderIdNotFoundException;
import org.mojave.core.settlement.domain.command.definition.FindSettlementProviderCommandHandler;
import org.mojave.core.settlement.domain.model.SettlementRecord;
import org.mojave.core.settlement.domain.repository.SettlementRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class SendSettlementRequestCommandHandler implements SendSettlementRequestCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        SendSettlementRequestCommandHandler.class);

    private final SettlementRecordRepository settlementRecordRepository;

    private final FindSettlementProviderCommandHandler findSettlementProviderCommandHandler;

    public SendSettlementRequestCommandHandler(final SettlementRecordRepository settlementRecordRepository,
                                               final FindSettlementProviderCommandHandler findSettlementProviderCommandHandler) {

        Objects.requireNonNull(settlementRecordRepository);
        Objects.requireNonNull(findSettlementProviderCommandHandler);

        this.settlementRecordRepository = settlementRecordRepository;
        this.findSettlementProviderCommandHandler = findSettlementProviderCommandHandler;
    }

    @Override
    @Transactional
    @Write
    public Output execute(final Input input) {

        LOGGER.info("SendSettlementRequestCommand : input: ({})", ObjectLogger.log(input));

        var providerOutput = this.findSettlementProviderCommandHandler.execute(
            new FindSettlementProviderCommand.Input(
                input.currency(), input.payerFspId(),
                input.payeeFspId()));

        if (providerOutput.settlementProviderId() == null) {
            throw new SettlementProviderIdNotFoundException(null);
        }

        var record = new SettlementRecord(
            input.settlementType(), input.payerFspId(), input.payeeFspId(), input.currency(),
            input.amount(), input.transferId(), input.transactionId(), input.transactionAt(),
            providerOutput.settlementProviderId());

        record = this.settlementRecordRepository.save(record);

        var output = new Output(record.getId(), record.getSspId());

        LOGGER.info("SendSettlementRequestCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}
