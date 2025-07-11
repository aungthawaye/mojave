package io.mojaloop.common.fspiop.support;

import io.mojaloop.common.fspiop.model.core.Money;
import io.mojaloop.common.fspiop.model.core.PartyIdInfo;

public record TransferParam(String quoteId, PartyIdInfo payer, PartyIdInfo payee, Money amount) { }
