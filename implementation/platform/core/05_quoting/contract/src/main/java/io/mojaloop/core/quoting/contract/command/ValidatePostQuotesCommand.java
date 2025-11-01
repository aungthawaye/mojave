package io.mojaloop.core.quoting.contract.command;

import io.mojaloop.fspiop.service.component.FspiopHttpRequest;
import io.mojaloop.fspiop.spec.core.QuotesPostRequest;

public interface ValidatePostQuotesCommand {

    Output execute(Input input);

    record Input(FspiopHttpRequest request, QuotesPostRequest quotesPostRequest) { }

    record Output() { }
}
