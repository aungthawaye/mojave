package io.mojaloop.core.account.domain.command.account;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.core.account.contract.command.account.CreateHubAccountCommand;
import io.mojaloop.core.account.domain.model.Account;
import io.mojaloop.core.account.domain.repository.AccountRepository;
import io.mojaloop.core.account.domain.repository.ChartEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateHubAccountCommandHandler implements CreateHubAccountCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateHubAccountCommandHandler.class);

    private final AccountRepository accountRepository;

    private final ChartEntryRepository chartEntryRepository;

    public CreateHubAccountCommandHandler(AccountRepository accountRepository, ChartEntryRepository chartEntryRepository) {

        assert accountRepository != null;
        assert chartEntryRepository != null;

        this.accountRepository = accountRepository;
        this.chartEntryRepository = chartEntryRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) {

        LOGGER.info("Executing CreateAccountCommand with input: {}", input);

        var chartEntry = this.chartEntryRepository.findById(input.chartEntryId())
                                                  .orElseThrow(() -> new IllegalArgumentException("ChartEntry not found: " + input.chartEntryId()));
        LOGGER.info("Found ChartEntry with id: {}", input.chartEntryId());

        var account = new Account(chartEntry, input.hubId(), input.currency(), input.code(), input.name(), input.description(), input.overdraftMode(),
                                  input.overdraftLimit());
        LOGGER.info("Created Account: {}", account);

        account = this.accountRepository.save(account);
        LOGGER.info("Saved Account with id: {}", account.getId());

        LOGGER.info("Completed CreateAccountCommand with input: {}", input);

        return new Output(account.getId());
    }

}
