package io.mojaloop.core.participant.contract.data;

import io.mojaloop.common.datatype.identifier.participant.FxRatePairId;
import io.mojaloop.common.datatype.identifier.participant.FxpId;
import io.mojaloop.common.fspiop.model.core.Currency;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

public record FxpData(FxpId fxpId, String name, String host, Map<String, FxRatePairData> fxRatePairs) {

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof FxpData fxpData)) {
            return false;
        }
        return Objects.equals(fxpId, fxpData.fxpId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(fxpId);
    }

    public record FxRatePairData(FxRatePairId fxRatePairId,
                                 Currency sourceCurrency,
                                 Currency targetCurrency,
                                 BigDecimal sourceAmount,
                                 BigDecimal targetAmount) {

        @Override
        public boolean equals(Object o) {

            if (!(o instanceof FxRatePairData that)) {
                return false;
            }
            return Objects.equals(fxRatePairId, that.fxRatePairId);
        }

        @Override
        public int hashCode() {

            return Objects.hashCode(fxRatePairId);
        }

    }

}
