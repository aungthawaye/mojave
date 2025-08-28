package io.mojaloop.core.participant.admin.controller.fsp;

import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.participant.contract.command.fsp.AddSupportedCurrencyCommand;
import io.mojaloop.core.participant.contract.exception.CurrencyAlreadySupportedException;
import io.mojaloop.core.participant.contract.exception.FspIdNotFoundException;
import io.mojaloop.fspiop.spec.core.Currency;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AddSupportedCurrencyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddSupportedCurrencyController.class);

    private final AddSupportedCurrencyCommand addSupportedCurrencyCommand;

    public AddSupportedCurrencyController(AddSupportedCurrencyCommand addSupportedCurrencyCommand) {

        assert addSupportedCurrencyCommand != null;

        this.addSupportedCurrencyCommand = addSupportedCurrencyCommand;
    }

    @PostMapping("/fsps/add-supported-currency")
    public ResponseEntity<?> execute(@Valid @RequestBody Request request)
        throws FspIdNotFoundException, CurrencyAlreadySupportedException {

        var input = new AddSupportedCurrencyCommand.Input(new FspId(request.fspId()), request.currency());

        var output = this.addSupportedCurrencyCommand.execute(input);

        return ResponseEntity.ok(new Response(output.supportedCurrencyId().getId()));
    }

    public record Request(@NotNull Long fspId, @NotNull Currency currency) { }

    public record Response(Long supportedCurrencyId) { }

}
