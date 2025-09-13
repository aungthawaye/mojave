package io.mojaloop.core.account.domain.model;

import java.math.BigDecimal;

public record DrCrHistory(BigDecimal amount, BigDecimal oldDebits, BigDecimal oldCredits, BigDecimal newDebits, BigDecimal newCredits) {

}
