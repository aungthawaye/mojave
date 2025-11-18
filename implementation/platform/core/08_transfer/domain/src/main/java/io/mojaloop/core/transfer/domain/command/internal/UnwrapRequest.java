package io.mojaloop.core.transfer.domain.command.internal;

import io.mojaloop.core.common.datatype.identifier.transfer.UdfTransferId;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.transfer.contract.component.interledger.PartyUnwrapper;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.component.handy.FspiopCurrencies;
import io.mojaloop.fspiop.component.handy.FspiopDates;
import io.mojaloop.fspiop.component.interledger.Interledger;
import io.mojaloop.fspiop.spec.core.Currency;
import io.mojaloop.fspiop.spec.core.PartyIdInfo;
import io.mojaloop.fspiop.spec.core.TransfersPostRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class UnwrapRequest {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnwrapRequest.class);

    private final PartyUnwrapper partyUnwrapper;

    public UnwrapRequest(PartyUnwrapper partyUnwrapper) {

        assert partyUnwrapper != null;

        this.partyUnwrapper = partyUnwrapper;
    }

    public Output execute(Input input) throws FspiopException {

        var payeeFspCode = input.payeeFsp().fspCode();

        var request = input.request();

        if (!request.getPayerFsp().equals(payeeFspCode.value()) || !request.getPayeeFsp().equals(payeeFspCode.value())) {

            LOGGER.error("FSPs information in the request body and request header must be the same.");

            throw new FspiopException(FspiopErrors.GENERIC_VALIDATION_ERROR, "FSPs information in the request body and request header must be the same.");
        }

        var currency = request.getAmount().getCurrency();
        var transferAmount = new BigDecimal(request.getAmount().getAmount());

        var ilpPacket = Interledger.unwrap(request.getIlpPacket());
        var ilpTransferAmount = Interledger.Amount.deserialize(ilpPacket.getAmount(), FspiopCurrencies.get(currency).scale());

        if (ilpTransferAmount.subtract(transferAmount).signum() != 0) {

            throw new FspiopException(FspiopErrors.GENERIC_VALIDATION_ERROR, "The amount from ILP packet must be equal to the transfer amount.");
        }

        var expiration = request.getExpiration();
        Instant requestExpiration = null;

        if (expiration != null) {

            requestExpiration = FspiopDates.fromRequestBody(expiration);

            if (requestExpiration.isBefore(Instant.now())) {

                throw new FspiopException(FspiopErrors.TRANSFER_EXPIRED, "The transfer request from Payer FSP has expired. The expiration is : " + expiration);
            }

        }

        var parties = this.partyUnwrapper.unwrap(ilpPacket.getData());

        var payerPartyIdInfo = parties.payer().orElseThrow(() -> new FspiopException(FspiopErrors.GENERIC_VALIDATION_ERROR, "Payer information is missing in the ILP packet."));
        var payeePartyIdInfo = parties.payee().orElseThrow(() -> new FspiopException(FspiopErrors.GENERIC_VALIDATION_ERROR, "Payee information is missing in the ILP packet."));

        return new Output(payerPartyIdInfo, payeePartyIdInfo, currency, transferAmount, requestExpiration);
    }

    public record Input(UdfTransferId udfTransferId, FspData payerFsp, FspData payeeFsp, TransfersPostRequest request) { }

    public record Output(PartyIdInfo payerPartyIdInfo, PartyIdInfo payeePartyIdInfo, Currency currency, BigDecimal transferAmount, Instant requestExpiration) { }

}
