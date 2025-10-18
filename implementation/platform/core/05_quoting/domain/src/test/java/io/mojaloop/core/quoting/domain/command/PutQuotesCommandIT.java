package io.mojaloop.core.quoting.domain.command;

import io.mojaloop.core.common.datatype.enums.ActivationStatus;
import io.mojaloop.core.common.datatype.enums.TerminationStatus;
import io.mojaloop.core.common.datatype.enums.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.identifier.participant.FspEndpointId;
import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.common.datatype.identifier.quoting.UdfQuoteId;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.core.participant.contract.data.FspCurrencyData;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.contract.data.FspEndpointData;
import io.mojaloop.core.quoting.contract.command.PostQuotesCommand;
import io.mojaloop.core.quoting.contract.command.PutQuotesCommand;
import io.mojaloop.fspiop.common.type.Payee;
import io.mojaloop.fspiop.common.type.Payer;
import io.mojaloop.fspiop.service.api.forwarder.ForwardRequest;
import io.mojaloop.fspiop.service.component.FspiopHttpRequest;
import io.mojaloop.fspiop.spec.core.AmountType;
import io.mojaloop.fspiop.spec.core.Currency;
import io.mojaloop.fspiop.spec.core.Money;
import io.mojaloop.fspiop.spec.core.Party;
import io.mojaloop.fspiop.spec.core.PartyIdInfo;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import io.mojaloop.fspiop.spec.core.QuotesIDPutResponse;
import io.mojaloop.fspiop.spec.core.TransactionInitiator;
import io.mojaloop.fspiop.spec.core.TransactionInitiatorType;
import io.mojaloop.fspiop.spec.core.TransactionScenario;
import io.mojaloop.fspiop.spec.core.TransactionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PutQuotesCommandIT extends BaseDomainIT {

    @Autowired
    private io.mojaloop.core.participant.store.ParticipantStore participantStore;

    @Autowired
    private ForwardRequest forwardRequest;

    @Autowired
    private PostQuotesCommand postQuotesCommand;

    @Autowired
    private PutQuotesCommand putQuotesCommand;

    private FspData mockFsp(final String code, final long id, final String baseUrl) {

        var fspId = new FspId(id);
        var fspCode = new FspCode(code);
        var endpoint = new FspEndpointData(new FspEndpointId(id * 10), EndpointType.QUOTES, baseUrl, Instant.now(), fspId);

        var endpoints = new HashMap<EndpointType, FspEndpointData>();
        endpoints.put(EndpointType.QUOTES, endpoint);

        return new FspData(fspId, fspCode, code + " Name", new FspCurrencyData[] {}, endpoints,
            ActivationStatus.ACTIVE, TerminationStatus.ALIVE);
    }

    private PostQuotesCommand.Input buildPostInput(final String quoteId, final String payerFsp, final String payeeFsp,
                                                   final Currency currency, final Currency feesCurrency, final String expiration) {

        var payerInfo = new PartyIdInfo().partyIdType(PartyIdType.MSISDN).partyIdentifier("12345");
        var payeeInfo = new PartyIdInfo().partyIdType(PartyIdType.MSISDN).partyIdentifier("67890");

        var payer = new Party().partyIdInfo(payerInfo);
        var payee = new Party().partyIdInfo(payeeInfo);

        var amount = new Money().currency(currency).amount("100.00");
        var fees = new Money().currency(feesCurrency).amount("1.00");

        var txnType = new TransactionType()
            .scenario(TransactionScenario.TRANSFER)
            .subScenario("BILL")
            .initiator(TransactionInitiator.PAYER)
            .initiatorType(TransactionInitiatorType.CONSUMER);

        var request = new io.mojaloop.fspiop.spec.core.QuotesPostRequest(quoteId, quoteId, payee, payer, AmountType.SEND, amount, txnType)
            .fees(fees)
            .expiration(expiration);

        var httpReq = new FspiopHttpRequest(new Payer(payerFsp), new Payee(payeeFsp), "POST", "/quotes", "application/json",
            Map.of(), Map.of(), "{}");

        return new PostQuotesCommand.Input(httpReq, request);
    }

    private PutQuotesCommand.Input buildPutInput(final String quoteId, final String payerFsp, final String payeeFsp,
                                                 final Currency currency, final String expiration,
                                                 final String transferAmount, final String fee, final String commission, final String receiveAmount) {

        var transfer = new Money().currency(currency).amount(transferAmount);
        var payeeReceive = new Money().currency(currency).amount(receiveAmount);
        var payeeFee = new Money().currency(currency).amount(fee);
        var payeeCommission = new Money().currency(currency).amount(commission);

        var put = new QuotesIDPutResponse(transfer, expiration, "ilpPacket", "condition")
            .payeeReceiveAmount(payeeReceive)
            .payeeFspFee(payeeFee)
            .payeeFspCommission(payeeCommission);

        var httpReq = new FspiopHttpRequest(new Payer(payerFsp), new Payee(payeeFsp), "PUT", "/quotes/" + quoteId, "application/json",
            Map.of(), Map.of(), "{}");

        return new PutQuotesCommand.Input(httpReq, new UdfQuoteId(quoteId), put);
    }

    @Test
    void should_update_quote_and_forward_to_payer_on_success() throws Exception {

        // Arrange
        reset(this.participantStore, this.forwardRequest);

        var payer = mockFsp("DFSP11", 11L, "http://payer.example");
        var payee = mockFsp("DFSP22", 22L, "http://payee.example");

        when(this.participantStore.getFspData(new FspCode("DFSP11"))).thenReturn(payer);
        when(this.participantStore.getFspData(new FspCode("DFSP22"))).thenReturn(payee);
        doNothing().when(this.forwardRequest).forward(ArgumentMatchers.anyString(), ArgumentMatchers.any());

        var quoteId = java.util.UUID.randomUUID().toString();
        var postInput = buildPostInput(quoteId, "DFSP11", "DFSP22", Currency.USD, Currency.USD, Instant.now().plusSeconds(3600).toString());
        this.postQuotesCommand.execute(postInput);

        reset(this.forwardRequest);
        when(this.participantStore.getFspData(new FspCode("DFSP11"))).thenReturn(payer);
        when(this.participantStore.getFspData(new FspCode("DFSP22"))).thenReturn(payee);
        doNothing().when(this.forwardRequest).forward(ArgumentMatchers.eq("http://payer.example"), ArgumentMatchers.any());

        var putInput = buildPutInput(quoteId, "DFSP11", "DFSP22", Currency.USD, Instant.now().plusSeconds(3600).toString(),
            "100.00", "1.00", "0.50", "98.50");

        // Act
        var output = this.putQuotesCommand.execute(putInput);

        // Assert
        Assertions.assertNotNull(output);
        verify(this.forwardRequest, times(1)).forward(ArgumentMatchers.eq("http://payer.example"), ArgumentMatchers.any());
    }

    @Test
    void should_ignore_when_quote_not_found() throws Exception {

        // Arrange
        reset(this.participantStore, this.forwardRequest);

        var payer = mockFsp("DFSP31", 31L, "http://payer.example");
        var payee = mockFsp("DFSP32", 32L, "http://payee.example");

        when(this.participantStore.getFspData(new FspCode("DFSP31"))).thenReturn(payer);
        when(this.participantStore.getFspData(new FspCode("DFSP32"))).thenReturn(payee);

        var quoteId = java.util.UUID.randomUUID().toString();
        var putInput = buildPutInput(quoteId, "DFSP31", "DFSP32", Currency.USD, Instant.now().plusSeconds(3600).toString(),
            "100.00", "1.00", "0.50", "98.50");

        // Act
        var output = this.putQuotesCommand.execute(putInput);

        // Assert
        Assertions.assertNotNull(output);
        verify(this.forwardRequest, times(0)).forward(ArgumentMatchers.anyString(), ArgumentMatchers.any());
    }

    @Test
    void should_set_error_when_currency_mismatch() throws Exception {

        // Arrange
        reset(this.participantStore, this.forwardRequest);

        var payer = mockFsp("DFSP41", 41L, "http://payer.example");
        var payee = mockFsp("DFSP42", 42L, "http://payee.example");

        when(this.participantStore.getFspData(new FspCode("DFSP41"))).thenReturn(payer);
        when(this.participantStore.getFspData(new FspCode("DFSP42"))).thenReturn(payee);
        doNothing().when(this.forwardRequest).forward(ArgumentMatchers.anyString(), ArgumentMatchers.any());

        var quoteId = java.util.UUID.randomUUID().toString();
        var postInput = buildPostInput(quoteId, "DFSP41", "DFSP42", Currency.USD, Currency.USD, Instant.now().plusSeconds(3600).toString());
        this.postQuotesCommand.execute(postInput);

        reset(this.forwardRequest);
        when(this.participantStore.getFspData(new FspCode("DFSP41"))).thenReturn(payer);
        when(this.participantStore.getFspData(new FspCode("DFSP42"))).thenReturn(payee);

        var put = new QuotesIDPutResponse(new Money().currency(Currency.TZS).amount("100.00"), Instant.now().plusSeconds(3600).toString(), "ilp", "cond");
        put.payeeReceiveAmount(new Money().currency(Currency.USD).amount("98.50"));
        put.payeeFspFee(new Money().currency(Currency.USD).amount("1.00"));
        put.payeeFspCommission(new Money().currency(Currency.USD).amount("0.50"));

        var httpReq = new FspiopHttpRequest(new Payer("DFSP41"), new Payee("DFSP42"), "PUT", "/quotes/" + quoteId, "application/json",
            Map.of(), Map.of(), "{}");
        var input = new PutQuotesCommand.Input(httpReq, new UdfQuoteId(quoteId), put);

        // Act
        var output = this.putQuotesCommand.execute(input);

        // Assert
        Assertions.assertNotNull(output);

        var error = this.jdbc().queryForObject("select error from qot_quote where udf_quote_id = ?", String.class, quoteId);
        Assertions.assertNotNull(error);

        verify(this.forwardRequest, times(0)).forward(ArgumentMatchers.anyString(), ArgumentMatchers.any());
    }
}
