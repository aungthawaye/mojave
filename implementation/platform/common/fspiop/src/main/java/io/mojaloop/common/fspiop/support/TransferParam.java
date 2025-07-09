package io.mojaloop.common.fspiop.support;

import io.mojaloop.common.fspiop.core.model.Money;
import io.mojaloop.common.fspiop.core.model.PartyIdInfo;

public record TransferParam(String quoteId, PartyIdInfo payer, PartyIdInfo payee, Money amount) { }
