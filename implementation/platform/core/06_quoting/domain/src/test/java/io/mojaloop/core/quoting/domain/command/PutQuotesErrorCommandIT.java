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
import io.mojaloop.core.quoting.contract.command.PutQuotesErrorCommand;
import io.mojaloop.fspiop.common.type.Payee;
import io.mojaloop.fspiop.common.type.Payer;
import io.mojaloop.fspiop.service.api.forwarder.ForwardRequest;
import io.mojaloop.fspiop.service.component.FspiopHttpRequest;
import io.mojaloop.fspiop.spec.core.AmountType;
import io.mojaloop.fspiop.spec.core.Currency;
import io.mojaloop.fspiop.spec.core.ErrorInformation;
import io.mojaloop.fspiop.spec.core.ErrorInformationObject;
import io.mojaloop.fspiop.spec.core.Money;
import io.mojaloop.fspiop.spec.core.Party;
import io.mojaloop.fspiop.spec.core.PartyIdInfo;
import io.mojaloop.fspiop.spec.core.PartyIdType;
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

class PutQuotesErrorCommandIT extends BaseDomainIT {

    @Autowired
    private io.mojaloop.core.participant.store.ParticipantStore participantStore;

    @Autowired
    private ForwardRequest forwardRequest;

    @Autowired
    private PostQuotesCommand postQuotesCommand;

    @Autowired
    private PutQuotesErrorCommand putQuotesErrorCommand;

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

    @Test
    void should_set_error_and_forward_to_payer() throws Exception {

        // Arrange
        reset(this.participantStore, this.forwardRequest);

        var payer = mockFsp("DFSP51", 51L, "http://payer.example");
        var payee = mockFsp("DFSP52", 52L, "http://payee.example");

        when(this.participantStore.getFspData(new FspCode("DFSP51"))).thenReturn(payer);
        when(this.participantStore.getFspData(new FspCode("DFSP52"))).thenReturn(payee);
        doNothing().when(this.forwardRequest).forward(ArgumentMatchers.anyString(), ArgumentMatchers.any());

        var quoteId = java.util.UUID.randomUUID().toString();
        var postInput = buildPostInput(quoteId, "DFSP51", "DFSP52", Currency.USD, Currency.USD, Instant.now().plusSeconds(3600).toString());
        this.postQuotesCommand.execute(postInput);

        reset(this.forwardRequest);
        when(this.participantStore.getFspData(new FspCode("DFSP51"))).thenReturn(payer);
        when(this.participantStore.getFspData(new FspCode("DFSP52"))).thenReturn(payee);
        doNothing().when(this.forwardRequest).forward(ArgumentMatchers.eq("http://payer.example"), ArgumentMatchers.any());

        var error = new ErrorInformationObject(new ErrorInformation("2001", "Validation error"));
        var httpReq = new FspiopHttpRequest(new Payer("DFSP51"), new Payee("DFSP52"), "PUT", "/quotes/" + quoteId + "/error", "application/json",
            Map.of(), Map.of(), "{}");

        var input = new PutQuotesErrorCommand.Input(httpReq, new UdfQuoteId(quoteId), error);

        // Act
        var output = this.putQuotesErrorCommand.execute(input);

        // Assert
        Assertions.assertNotNull(output);

        var dbError = this.jdbc().queryForObject("select error from qot_quote where udf_quote_id = ?", String.class, quoteId);
        Assertions.assertNotNull(dbError);

        verify(this.forwardRequest, times(1)).forward(ArgumentMatchers.eq("http://payer.example"), ArgumentMatchers.any());
    }

    @Test
    void should_ignore_when_quote_not_found() throws Exception {

        // Arrange
        reset(this.participantStore, this.forwardRequest);

        var payer = mockFsp("DFSP61", 61L, "http://payer.example");
        var payee = mockFsp("DFSP62", 62L, "http://payee.example");

        when(this.participantStore.getFspData(new FspCode("DFSP61"))).thenReturn(payer);
        when(this.participantStore.getFspData(new FspCode("DFSP62"))).thenReturn(payee);

        var error = new ErrorInformationObject(new ErrorInformation("2001", "Validation error"));
        var httpReq = new FspiopHttpRequest(new Payer("DFSP61"), new Payee("DFSP62"), "PUT", "/quotes/" + java.util.UUID.randomUUID() + "/error", "application/json",
            Map.of(), Map.of(), "{}");

        var input = new PutQuotesErrorCommand.Input(httpReq, new UdfQuoteId(java.util.UUID.randomUUID().toString()), error);

        // Act
        var output = this.putQuotesErrorCommand.execute(input);

        // Assert
        Assertions.assertNotNull(output);
        verify(this.forwardRequest, times(0)).forward(ArgumentMatchers.anyString(), ArgumentMatchers.any());
    }
}
