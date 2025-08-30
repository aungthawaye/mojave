package io.mojaloop.connector.sample.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.UnsignedLong;
import io.mojaloop.connector.adapter.fsp.FspAdapter;
import io.mojaloop.fspiop.common.data.Agreement;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.participant.ParticipantContext;
import io.mojaloop.fspiop.common.type.Source;
import io.mojaloop.fspiop.component.handy.FspiopDates;
import io.mojaloop.fspiop.component.interledger.Interledger;
import io.mojaloop.fspiop.spec.core.AmountType;
import io.mojaloop.fspiop.spec.core.Currency;
import io.mojaloop.fspiop.spec.core.Extension;
import io.mojaloop.fspiop.spec.core.ExtensionList;
import io.mojaloop.fspiop.spec.core.Money;
import io.mojaloop.fspiop.spec.core.PartiesTypeIDPutResponse;
import io.mojaloop.fspiop.spec.core.Party;
import io.mojaloop.fspiop.spec.core.PartyIdInfo;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import io.mojaloop.fspiop.spec.core.PartyPersonalInfo;
import io.mojaloop.fspiop.spec.core.QuotesIDPutResponse;
import io.mojaloop.fspiop.spec.core.QuotesPostRequest;
import io.mojaloop.fspiop.spec.core.TransferState;
import io.mojaloop.fspiop.spec.core.TransfersIDPatchResponse;
import io.mojaloop.fspiop.spec.core.TransfersIDPutResponse;
import io.mojaloop.fspiop.spec.core.TransfersPostRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public class SampleFspAdapter implements FspAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SampleFspAdapter.class);

    private final ParticipantContext participantContext;

    private final ObjectMapper objectMapper;

    public SampleFspAdapter(ParticipantContext participantContext, ObjectMapper objectMapper) {

        assert participantContext != null;
        assert objectMapper != null;

        this.participantContext = participantContext;
        this.objectMapper = objectMapper;
    }

    @Override
    public void confirmTransfer(Source source, TransfersIDPatchResponse response) {

        LOGGER.info("Confirming transfer: {}", response);
    }

    @Override
    public PartiesTypeIDPutResponse getParties(Source source, PartyIdType partyIdType, String partyId, String subId) {

        var party = new Party();
        party.setName("ATA");
        party.setSupportedCurrencies(List.of(Currency.USD, Currency.EUR, Currency.GBP, Currency.MMK));

        var partyPersonalInfo = new PartyPersonalInfo();
        partyPersonalInfo.setDateOfBirth("1990-01-01");
        partyPersonalInfo.setKycInformation("12/TaMaNa(N)123456");
        party.setPersonalInfo(partyPersonalInfo);

        var partyIdInfo = new PartyIdInfo();
        partyIdInfo.partyIdType(partyIdType);
        partyIdInfo.partyIdentifier(partyId);
        partyIdInfo.partySubIdOrType(subId);
        party.setPartyIdInfo(partyIdInfo);

        var response = new PartiesTypeIDPutResponse(party);

        LOGGER.info("Returning parties: {}", response);

        return response;
    }

    @Override
    public TransfersIDPutResponse initiateTransfer(Source source, TransfersPostRequest request) {

        var transferId = request.getTransferId();
        var condition = request.getCondition();
        var requestIlpPacket = request.getIlpPacket();
        var preparedIlpPacket = Interledger.unwrap(requestIlpPacket);

        LOGGER.info("Transfer Id : [{}]", transferId);
        LOGGER.info("Request ILP Packet : [{}]", requestIlpPacket);
        LOGGER.info("Prepare ILP Packet : [{}]", preparedIlpPacket);

        var transferParam = new String(preparedIlpPacket.getData(), StandardCharsets.UTF_8);
        LOGGER.info("Transfer Param : [{}]", transferParam);

        // I generated this ILP Packet at the quote stage. And now, I am verifying and fulfilling.
        var fulfillment = Interledger.fulfill(this.participantContext.ilpSecret(), Interledger.address(source.sourceFspCode()),
                                              UnsignedLong.valueOf(request.getAmount().getAmount()), transferParam, condition,
                                              requestIlpPacket);

        LOGGER.info("Fulfillment : [{}]", fulfillment);

        var transfer = new TransfersIDPutResponse();
        var extensionList = new ExtensionList();

        extensionList.addExtensionItem(new Extension("homeTransactionId", UUID.randomUUID().toString()));
        extensionList.addExtensionItem(new Extension("transferId", transferId));

        transfer
            .fulfilment(fulfillment.base64Fulfillment())
            .transferState(TransferState.RESERVED)
            .completedTimestamp(FspiopDates.forRequestBody())
            .extensionList(extensionList);

        return null;
    }

    @Override
    public QuotesIDPutResponse quote(Source source, QuotesPostRequest request) throws FspiopException {

        try {

            // This is how a DFSP will calculate the quote and send it back to Hub through PutQuote

            var quoteId = request.getQuoteId();
            var extensionList = new ExtensionList();

            var currency = request.getAmount().getCurrency();

            var originalAmount = new BigDecimal(request.getAmount().getAmount());

            var payeeFspFee = new BigDecimal("0.5");
            var payeeFspCommission = new BigDecimal("1");
            var payeeReceiveAmount = new BigDecimal("0");
            var transferAmount = new BigDecimal("0");

            if (request.getAmountType() == AmountType.SEND) {
                payeeReceiveAmount = originalAmount.subtract(payeeFspFee).add(payeeFspCommission);
                transferAmount = originalAmount;
            } else {
                payeeReceiveAmount = originalAmount;
                transferAmount = originalAmount.add(payeeFspFee).subtract(payeeFspCommission);
            }

            var agreement = new Agreement(quoteId, request.getPayer().getPartyIdInfo(), request.getPayee().getPartyIdInfo(),
                                          request.getExpiration(), new Money(currency, payeeFspFee.toPlainString()),
                                          new Money(currency, payeeFspCommission.toPlainString()),
                                          new Money(currency, payeeReceiveAmount.toPlainString()),
                                          new Money(currency, transferAmount.toPlainString()));

            var payload = this.objectMapper.writeValueAsString(agreement);
            var preparePacket = Interledger.prepare(this.participantContext.ilpSecret(), Interledger.address(source.sourceFspCode()),
                                                    UnsignedLong.valueOf(request.getAmount().getAmount()), payload, 900);

            extensionList.addExtensionItem(new Extension("quoteId", quoteId));
            // Payer related
            extensionList.addExtensionItem(new Extension("payerFspId", source.sourceFspCode()));
            extensionList.addExtensionItem(
                new Extension("payerPartyIdType", request.getPayer().getPartyIdInfo().getPartyIdType().toString()));
            extensionList.addExtensionItem(new Extension("payerPartyId", request.getPayer().getPartyIdInfo().getPartyIdentifier()));

            // Payee related
            extensionList.addExtensionItem(new Extension("payeeFspId", this.participantContext.fspCode()));
            extensionList.addExtensionItem(
                new Extension("payeePartyIdType", request.getPayee().getPartyIdInfo().getPartyIdType().toString()));
            extensionList.addExtensionItem(new Extension("payeePartyId", request.getPayee().getPartyIdInfo().getPartyIdentifier()));

            var quote = new QuotesIDPutResponse();

            quote
                .condition(preparePacket.base64Condition())
                .ilpPacket(preparePacket.base64PreparePacket())
                .expiration(request.getExpiration())
                .payeeFspCommission(new Money(currency, payeeFspCommission.toPlainString()))
                .payeeFspFee(new Money(currency, payeeFspFee.toPlainString()))
                .payeeReceiveAmount(new Money(currency, payeeReceiveAmount.toPlainString()))
                .transferAmount(new Money(currency, transferAmount.toPlainString()))
                .extensionList(extensionList);

            return quote;

        } catch (Exception e) {

            LOGGER.error("Error while quoting", e);
            throw new FspiopException(FspiopErrors.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
