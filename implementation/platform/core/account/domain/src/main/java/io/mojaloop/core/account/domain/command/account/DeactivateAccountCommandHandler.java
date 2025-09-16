package io.mojaloop.core.account.domain.command.account;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.core.account.contract.command.account.DeactivateAccountCommand;
import io.mojaloop.core.account.contract.exception.account.AccountIdNotFoundException;
import io.mojaloop.core.account.domain.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeactivateAccountCommandHandler implements DeactivateAccountCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeactivateAccountCommandHandler.class);

    private final AccountRepository accountRepository;

    public DeactivateAccountCommandHandler(AccountRepository accountRepository) {

        assert accountRepository != null;
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) throws AccountIdNotFoundException {

        LOGGER.info("Executing DeactivateAccountCommand with input: {}", input);

        var account = this.accountRepository.findById(input.accountId())
                                            .orElseThrow(() -> new AccountIdNotFoundException(input.accountId()));
        LOGGER.info("Found Account with id: {}", input.accountId());

        account.deactivate();
        LOGGER.info("Deactivated Account with id: {}", account.getId());

        this.accountRepository.save(account);
        LOGGER.info("Saved Account with id: {}", account.getId());

        LOGGER.info("Completed DeactivateAccountCommand with input: {}", input);

        return new Output(account.getId());
    }

}
