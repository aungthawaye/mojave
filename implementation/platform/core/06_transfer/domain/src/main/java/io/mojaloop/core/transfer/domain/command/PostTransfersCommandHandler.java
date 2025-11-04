package io.mojaloop.core.transfer.domain.command;

import io.mojaloop.core.common.datatype.enums.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.identifier.transfer.UdfTransferId;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.store.ParticipantStore;
import io.mojaloop.core.transfer.TransferDomainConfiguration;
import io.mojaloop.core.transfer.contract.command.PostTransfersCommand;
import io.mojaloop.core.transfer.domain.repository.TransferRepository;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Payer;
import io.mojaloop.fspiop.component.handy.FspiopCurrencies;
import io.mojaloop.fspiop.component.handy.FspiopDates;
import io.mojaloop.fspiop.component.handy.FspiopErrorResponder;
import io.mojaloop.fspiop.component.handy.FspiopUrls;
import io.mojaloop.fspiop.component.interledger.Interledger;
import io.mojaloop.fspiop.service.api.forwarder.ForwardRequest;
import io.mojaloop.fspiop.service.api.transfers.RespondTransfers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class PostTransfersCommandHandler implements PostTransfersCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostTransfersCommandHandler.class);

    private final ParticipantStore participantStore;

    private final RespondTransfers respondTransfers;

    private final ForwardRequest forwardRequest;

    private final TransferRepository transferRepository;

    private final PlatformTransactionManager transactionManager;

    private final TransferDomainConfiguration.TransferSettings transferSettings;

    public PostTransfersCommandHandler(ParticipantStore participantStore,
                                       RespondTransfers respondTransfers,
                                       ForwardRequest forwardRequest,
                                       TransferRepository transferRepository,
                                       PlatformTransactionManager transactionManager,
                                       TransferDomainConfiguration.TransferSettings transferSettings) {

        assert participantStore != null;
        assert respondTransfers != null;
        assert forwardRequest != null;
        assert transferRepository != null;
        assert transactionManager != null;
        assert transferSettings != null;

        this.participantStore = participantStore;
        this.respondTransfers = respondTransfers;
        this.forwardRequest = forwardRequest;
        this.transferRepository = transferRepository;
        this.transactionManager = transactionManager;
        this.transferSettings = transferSettings;
    }

    @Override
    public Output execute(Input input) {

        var udfTransferId = new UdfTransferId(input.transfersPostRequest().getTransferId());

        LOGGER.info("({}) Executing PostTransfersCommandHandler with input: {}", udfTransferId.getId(), input);

        FspCode payerFspCode = null;
        FspData payerFsp = null;
        FspCode payeeFspCode = null;
        FspData payeeFsp = null;

        try {

            payerFspCode = new FspCode(input.request().payer().fspCode());
            payerFsp = this.participantStore.getFspData(payerFspCode);
            LOGGER.debug("({}) Found payer FSP: [{}]", udfTransferId.getId(), payerFsp);

            payeeFspCode = new FspCode(input.request().payee().fspCode());
            payeeFsp = this.participantStore.getFspData(payeeFspCode);
            LOGGER.debug("({}) Found payee FSP: [{}]", udfTransferId.getId(), payeeFsp);

            var postTransfersRequest = input.transfersPostRequest();

            var payerFspInRequest = postTransfersRequest.getPayerFsp();
            var payeeFspInRequest = postTransfersRequest.getPayeeFsp();

            // Make sure the payer/payee information from the request body and request header are the same.
            if (!payerFspInRequest.equals(payerFspCode.value()) || !payeeFspInRequest.equals(payeeFspCode.value())) {

                throw new FspiopException(FspiopErrors.GENERIC_VALIDATION_ERROR, "FSPs information in the request body and request header must be the same.");
            }

            var currency = postTransfersRequest.getAmount().getCurrency();
            var transferAmount = new BigDecimal(postTransfersRequest.getAmount().getAmount());

            var ilpPacket = Interledger.unwrap(postTransfersRequest.getIlpPacket());
            var ilpTransferAmount = Interledger.Amount.deserialize(ilpPacket.getAmount(), FspiopCurrencies.get(currency).scale());

            if (ilpTransferAmount.subtract(transferAmount).signum() != 0) {

                throw new FspiopException(FspiopErrors.GENERIC_VALIDATION_ERROR, "ILP/Agreement amount must be equal to transfer amount");
            }

            var expiration = postTransfersRequest.getExpiration();
            Instant expireAt = null;

            if (expiration != null) {

                expireAt = FspiopDates.fromRequestBody(expiration);

                if (expireAt.isBefore(Instant.now())) {

                    throw new FspiopException(FspiopErrors.TRANSFER_EXPIRED, "The transfer has expired. The expiration is : " + expiration);
                }

            }

        } catch (Exception e) {

            LOGGER.error("({}) Exception occurred while executing PostTransfersCommandHandler: [{}]", udfTransferId.getId(), e.getMessage());

            if (payerFsp != null) {

                final var sendBackTo = new Payer(payerFspCode.value());
                final var baseUrl = payerFsp.endpoints().get(EndpointType.TRANSFERS).baseUrl();
                final var url = FspiopUrls.newUrl(baseUrl, input.request().uri() + "/error");

                try {

                    FspiopErrorResponder.toPayer(new Payer(payerFspCode.value()), e, (payer, error) -> this.respondTransfers.putTransfersError(sendBackTo, url, error));

                } catch (Throwable ignored) {
                    LOGGER.error("Something went wrong while sending error response to payer FSP: ", e);
                }
            }
        }

        LOGGER.info("({}) Returning from PostTransfersCommandHandler successfully.", udfTransferId.getId());

        return new Output();
    }

}
