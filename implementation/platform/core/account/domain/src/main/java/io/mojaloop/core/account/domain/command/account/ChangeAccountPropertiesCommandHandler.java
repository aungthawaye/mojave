package io.mojaloop.core.account.domain.command.account;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.core.account.contract.command.account.ChangeAccountPropertiesCommand;
import io.mojaloop.core.account.contract.exception.account.AccountIdNotFoundException;
import io.mojaloop.core.account.domain.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChangeAccountPropertiesCommandHandler implements ChangeAccountPropertiesCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeAccountPropertiesCommandHandler.class);

    private final AccountRepository accountRepository;

    public ChangeAccountPropertiesCommandHandler(AccountRepository accountRepository) {

        assert accountRepository != null;
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) throws AccountIdNotFoundException {

        LOGGER.info("Executing ChangeAccountPropertiesCommand with input: {}", input);

        var account = this.accountRepository.findById(input.accountId())
                                            .orElseThrow(() -> new AccountIdNotFoundException(input.accountId()));
        LOGGER.info("Found Account with id: {}", input.accountId());

        if (input.code() != null) {
            LOGGER.info("Updating code for Account id: {}", account.getId());
            account.code(input.code());
        }

        if (input.name() != null) {
            LOGGER.info("Updating name for Account id: {}", account.getId());
            account.name(input.name());
        }

        if (input.description() != null) {
            LOGGER.info("Updating description for Account id: {}", account.getId());
            account.description(input.description());
        }

        this.accountRepository.save(account);
        LOGGER.info("Saved Account with id: {}", account.getId());

        LOGGER.info("Completed ChangeAccountPropertiesCommand with input: {}", input);

        return new Output(account.getId());
    }

}
