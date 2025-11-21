package io.mojaloop.core.transfer.domain.command.step.stateful;

import io.mojaloop.component.jpa.routing.annotation.Read;
import io.mojaloop.core.common.datatype.identifier.transfer.UdfTransferId;
import io.mojaloop.core.transfer.domain.model.Transfer;
import io.mojaloop.core.transfer.domain.repository.TransferRepository;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FetchTransfer {

    private static final Logger LOGGER = LoggerFactory.getLogger(FetchTransfer.class);

    private final TransferRepository transferRepository;

    public FetchTransfer(TransferRepository transferRepository) {

        assert transferRepository != null;

        this.transferRepository = transferRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Read
    public Output execute(Input input) throws FspiopException {

        try {

            LOGGER.info("Fetching transfer with transferId: [{}]", input.udfTransferId);

            var optTransfer = this.transferRepository.findOne(TransferRepository.Filters.withUdfTransferId(input.udfTransferId));

            if (optTransfer.isEmpty()) {

                LOGGER.info("Transfer not found for udfTransferId : [{}]", input.udfTransferId.getId());
                return new Output(null);
            }

            var transfer = optTransfer.get();

            LOGGER.info("Transfer found for udfTransferId : [{}]", input.udfTransferId.getId());

            return new Output(transfer);

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, e.getMessage());
        }
    }

    public record Input(UdfTransferId udfTransferId) { }

    public record Output(Transfer transfer) { }

}
