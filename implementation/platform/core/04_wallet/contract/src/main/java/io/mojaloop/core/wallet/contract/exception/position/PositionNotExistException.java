package io.mojaloop.core.wallet.contract.exception.position;

import io.mojaloop.component.misc.exception.CheckedDomainException;
import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.fspiop.spec.core.Currency;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class PositionNotExistException extends CheckedDomainException {

    public static final String CODE = "POSITION_NOT_EXIST";

    private static final String TEMPLATE = "Position does not exist : walletOwnerId ({0}) | currency ({1}).";

    private final WalletOwnerId walletOwnerId;

    private final Currency currency;

    public PositionNotExistException(final WalletOwnerId walletOwnerId,
                                     final Currency currency) {

        super(new ErrorTemplate(CODE, TEMPLATE,
            new String[]{walletOwnerId.getId().toString(), currency.name()}));

        this.walletOwnerId = walletOwnerId;
        this.currency = currency;
    }

    public static PositionNotExistException from(final Map<String, String> extras) {

        final var walletOwnerId = new WalletOwnerId(Long.valueOf(extras.get(Keys.WALLET_OWNER_ID)));
        final var currency = Currency.valueOf(extras.get(Keys.CURRENCY));

        return new PositionNotExistException(walletOwnerId, currency);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.WALLET_OWNER_ID, this.walletOwnerId.getId().toString());
        extras.put(Keys.CURRENCY, this.currency.name());

        return extras;
    }

    public static class Keys {

        public static final String WALLET_OWNER_ID = "walletOwnerId";

        public static final String CURRENCY = "currency";

    }

}
