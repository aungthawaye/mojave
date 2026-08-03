package org.mojave.core.settlement.contract.exception.record;

import lombok.Getter;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.scheme.rule.identifier.transaction.TransactionId;

import java.util.HashMap;
import java.util.Map;

@Getter
public class SettlementRecordAlreadyExistsException extends UncheckedDomainException {

    public static final String CODE = "SETTLEMENT_RECORD_ALREADY_EXISTS";

    private static final String TEMPLATE = "Settlement Record for transaction ({0}) already exists.";

    private final TransactionId transactionId;

    public SettlementRecordAlreadyExistsException(final TransactionId transactionId) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{String.valueOf(transactionId.getId())}));

        this.transactionId = transactionId;
    }

    public static SettlementRecordAlreadyExistsException from(final Map<String, String> extras) {

        final var transactionId = new TransactionId(Long.parseLong(extras.get(Keys.TRANSACTION_ID)));

        return new SettlementRecordAlreadyExistsException(transactionId);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.TRANSACTION_ID, String.valueOf(this.transactionId.getId()));

        return extras;
    }

    public static class Keys {

        public static final String TRANSACTION_ID = "transactionId";

    }

}
