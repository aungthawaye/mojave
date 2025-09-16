package io.mojaloop.core.account.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public record DrCr(@Column(name = "debits", precision = 34, scale = 4) BigDecimal debits,
                   @Column(name = "credits", precision = 34, scale = 4) BigDecimal credits) {

}
