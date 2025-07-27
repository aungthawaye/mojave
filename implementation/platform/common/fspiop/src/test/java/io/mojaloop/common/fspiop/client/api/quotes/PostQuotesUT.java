package io.mojaloop.common.fspiop.client.api.quotes;

import io.mojaloop.common.datatype.type.fspiop.FspCode;
import io.mojaloop.common.fspiop.client.FspiopClientConfiguration;
import io.mojaloop.common.fspiop.client.api.TestSettings;
import io.mojaloop.common.fspiop.component.FspiopDates;
import io.mojaloop.common.fspiop.model.core.AmountType;
import io.mojaloop.common.fspiop.model.core.Currency;
import io.mojaloop.common.fspiop.model.core.Money;
import io.mojaloop.common.fspiop.model.core.Party;
import io.mojaloop.common.fspiop.model.core.PartyIdInfo;
import io.mojaloop.common.fspiop.model.core.PartyIdType;
import io.mojaloop.common.fspiop.model.core.QuotesPostRequest;
import io.mojaloop.common.fspiop.model.core.TransactionInitiator;
import io.mojaloop.common.fspiop.model.core.TransactionInitiatorType;
import io.mojaloop.common.fspiop.model.core.TransactionScenario;
import io.mojaloop.common.fspiop.model.core.TransactionType;
import io.mojaloop.common.fspiop.support.Destination;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {FspiopClientConfiguration.class, TestSettings.class})
public class PostQuotesUT {

    @Autowired
    PostQuotes postQuotes;

    @Test
    public void test() {

        var quote = new QuotesPostRequest();

        var _15minsLater = new Date(Instant.now().plus(15, ChronoUnit.MINUTES).toEpochMilli());

        var quoteId = UUID.randomUUID().toString();

        quote
            .quoteId(UUID.randomUUID().toString())
            .transactionId(quoteId)
            .transactionRequestId(quoteId)
            .amountType(AmountType.SEND)
            .amount(new Money(Currency.USD, "100"))
            .payer(new Party().partyIdInfo(new PartyIdInfo(PartyIdType.MSISDN, "987654321")))
            .payee(new Party().partyIdInfo(new PartyIdInfo(PartyIdType.MSISDN, "123456789")))
            .transactionType(new TransactionType()
                                 .scenario(TransactionScenario.TRANSFER)
                                 .initiator(TransactionInitiator.PAYER)
                                 .initiatorType(TransactionInitiatorType.CONSUMER))
            .expiration(FspiopDates.forRequestBody(_15minsLater));

        this.postQuotes.postQuotes(new Destination(new FspCode("fsp2")), quote);
    }

}
