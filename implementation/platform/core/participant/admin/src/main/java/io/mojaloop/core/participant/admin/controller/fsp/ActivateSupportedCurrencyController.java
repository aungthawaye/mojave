package io.mojaloop.core.participant.admin.controller.fsp;

import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.participant.contract.command.fsp.ActivateSupportedCurrencyCommand;
import io.mojaloop.core.participant.contract.exception.CannotActivateSupportedCurrencyException;
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
public class ActivateSupportedCurrencyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivateSupportedCurrencyController.class);

    private final ActivateSupportedCurrencyCommand activateSupportedCurrencyCommand;

    public ActivateSupportedCurrencyController(ActivateSupportedCurrencyCommand activateSupportedCurrencyCommand) {

        assert activateSupportedCurrencyCommand != null;

        this.activateSupportedCurrencyCommand = activateSupportedCurrencyCommand;
    }

    @PostMapping("/fsps/activate-supported-currency")
    public ResponseEntity<?> execute(@Valid @RequestBody Request request)
        throws FspIdNotFoundException, CannotActivateSupportedCurrencyException {

        var input = new ActivateSupportedCurrencyCommand.Input(new FspId(request.fspId()), request.currency());

        var output = this.activateSupportedCurrencyCommand.execute(input);

        return ResponseEntity.ok(new Response(output.activated()));
    }

    public record Request(@NotNull Long fspId, @NotNull Currency currency) { }

    public record Response(boolean activated) { }

}
