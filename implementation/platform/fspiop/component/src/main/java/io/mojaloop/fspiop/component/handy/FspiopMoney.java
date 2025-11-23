package io.mojaloop.fspiop.component.handy;

import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.spec.core.Money;

import java.math.BigDecimal;

public class FspiopMoney {

    public static void validate(Money money) throws FspiopException {

        if (money == null) {
            return;
        }

        var scale = FspiopCurrencies.get(money.getCurrency()).scale();
        var submittedScale = new BigDecimal(money.getAmount()).scale();

        if (submittedScale != scale) {

            throw new FspiopException(
                FspiopErrors.GENERIC_VALIDATION_ERROR,
                "Currency scale does not match. " + money.getCurrency() + " supports only " +
                    scale + " decimal places.");
        }

    }

}
