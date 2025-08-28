package io.mojaloop.core.participant.admin.controller.fsp;

import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.participant.contract.command.fsp.DeactivateSupportedCurrencyCommand;
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
public class DeactivateSupportedCurrencyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeactivateSupportedCurrencyController.class);

    private final DeactivateSupportedCurrencyCommand deactivateSupportedCurrencyCommand;

    public DeactivateSupportedCurrencyController(DeactivateSupportedCurrencyCommand deactivateSupportedCurrencyCommand) {

        assert deactivateSupportedCurrencyCommand != null;

        this.deactivateSupportedCurrencyCommand = deactivateSupportedCurrencyCommand;
    }

    @PostMapping("/fsps/deactivate-supported-currency")
    public ResponseEntity<?> execute(@Valid @RequestBody Request request) throws FspIdNotFoundException {

        var input = new DeactivateSupportedCurrencyCommand.Input(new FspId(request.fspId()), request.currency());

        var output = this.deactivateSupportedCurrencyCommand.execute(input);

        return ResponseEntity.ok(new Response(output.deactivated()));
    }

    public record Request(@NotNull Long fspId, @NotNull Currency currency) { }

    public record Response(boolean deactivated) { }

}
