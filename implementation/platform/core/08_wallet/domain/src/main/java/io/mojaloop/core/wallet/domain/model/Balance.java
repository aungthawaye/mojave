package io.mojaloop.core.wallet.domain.model;

import java.math.BigDecimal;

public record Balance(BigDecimal available, BigDecimal reserved) { }
