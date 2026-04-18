package org.mojave.mono.core.admin.api.command.accounting.account;

import org.mojave.core.accounting.contract.command.account.ActivateAccountCommand;
import org.mojave.core.accounting.contract.command.account.ChangeAccountPropertiesCommand;
import org.mojave.core.accounting.contract.command.account.CreateAccountByCategoryCommand;
import org.mojave.core.accounting.contract.command.account.CreateAccountCommand;
import org.mojave.core.accounting.contract.command.account.DeactivateAccountCommand;
import org.mojave.core.accounting.contract.command.account.TerminateAccountCommand;
import org.mojave.mono.core.admin.api.BaseApiController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class AccountCommandController extends BaseApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountCommandController.class);

    private final ActivateAccountCommand activateAccountCommand;

    private final ChangeAccountPropertiesCommand changeAccountPropertiesCommand;

    private final CreateAccountByCategoryCommand createAccountByCategoryCommand;

    private final CreateAccountCommand createAccountCommand;

    private final DeactivateAccountCommand deactivateAccountCommand;

    private final TerminateAccountCommand terminateAccountCommand;

    public AccountCommandController(final ActivateAccountCommand activateAccountCommand,
                                    final ChangeAccountPropertiesCommand changeAccountPropertiesCommand,
                                    final CreateAccountByCategoryCommand createAccountByCategoryCommand,
                                    final CreateAccountCommand createAccountCommand,
                                    final DeactivateAccountCommand deactivateAccountCommand,
                                    final TerminateAccountCommand terminateAccountCommand) {

        Objects.requireNonNull(activateAccountCommand);
        Objects.requireNonNull(changeAccountPropertiesCommand);
        Objects.requireNonNull(createAccountByCategoryCommand);
        Objects.requireNonNull(createAccountCommand);
        Objects.requireNonNull(deactivateAccountCommand);
        Objects.requireNonNull(terminateAccountCommand);

        this.activateAccountCommand = activateAccountCommand;
        this.changeAccountPropertiesCommand = changeAccountPropertiesCommand;
        this.createAccountByCategoryCommand = createAccountByCategoryCommand;
        this.createAccountCommand = createAccountCommand;
        this.deactivateAccountCommand = deactivateAccountCommand;
        this.terminateAccountCommand = terminateAccountCommand;
    }

    @PostMapping("/accounting/account/activate-account")
    public ResponseEntity<ActivateAccountCommand.Output> activateAccount(
        @RequestBody final ActivateAccountCommand.Input input) {

        return this.respond(
            LOGGER,
            "ActivateAccountCommand",
            input,
            () -> this.activateAccountCommand.execute(input));
    }

    @PostMapping("/accounting/account/change-account-properties")
    public ResponseEntity<ChangeAccountPropertiesCommand.Output> changeAccountProperties(
        @RequestBody final ChangeAccountPropertiesCommand.Input input) {

        return this.respond(
            LOGGER,
            "ChangeAccountPropertiesCommand",
            input,
            () -> this.changeAccountPropertiesCommand.execute(input));
    }

    @PostMapping("/accounting/account/create-account-by-category")
    public ResponseEntity<CreateAccountByCategoryCommand.Output> createAccountByCategory(
        @RequestBody final CreateAccountByCategoryCommand.Input input) {

        return this.respond(
            LOGGER,
            "CreateAccountByCategoryCommand",
            input,
            () -> this.createAccountByCategoryCommand.execute(input));
    }

    @PostMapping("/accounting/account/create-account")
    public ResponseEntity<CreateAccountCommand.Output> createAccount(
        @RequestBody final CreateAccountCommand.Input input) {

        return this.respond(
            LOGGER,
            "CreateAccountCommand",
            input,
            () -> this.createAccountCommand.execute(input));
    }

    @PostMapping("/accounting/account/deactivate-account")
    public ResponseEntity<DeactivateAccountCommand.Output> deactivateAccount(
        @RequestBody final DeactivateAccountCommand.Input input) {

        return this.respond(
            LOGGER,
            "DeactivateAccountCommand",
            input,
            () -> this.deactivateAccountCommand.execute(input));
    }

    @PostMapping("/accounting/account/terminate-account")
    public ResponseEntity<TerminateAccountCommand.Output> terminateAccount(
        @RequestBody final TerminateAccountCommand.Input input) {

        return this.respond(
            LOGGER,
            "TerminateAccountCommand",
            input,
            () -> this.terminateAccountCommand.execute(input));
    }

}
