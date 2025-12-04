package io.mojaloop.core.accounting.domain.command.account;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.component.misc.logger.ObjectLogger;
import io.mojaloop.core.accounting.contract.command.account.CreateAccountCommand;
import io.mojaloop.core.accounting.contract.command.account.CreateAccountInBulkCommand;
import io.mojaloop.core.accounting.domain.repository.ChartEntryRepository;
import io.mojaloop.core.common.datatype.enums.accounting.OverdraftMode;
import io.mojaloop.core.common.datatype.type.accounting.AccountCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class CreateAccountInBulkCommandHandler implements CreateAccountInBulkCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        CreateAccountInBulkCommandHandler.class);

    private final CreateAccountCommand createAccountCommand;

    private final ChartEntryRepository chartEntryRepository;

    public CreateAccountInBulkCommandHandler(CreateAccountCommand createAccountCommand,
                                             ChartEntryRepository chartEntryRepository) {

        assert createAccountCommand != null;
        assert chartEntryRepository != null;

        this.createAccountCommand = createAccountCommand;
        this.chartEntryRepository = chartEntryRepository;
    }

    @Transactional
    @Write
    @Override
    public Output execute(Input input) {

        LOGGER.info("CreateAccountInBulkCommand : input: ({})", ObjectLogger.log(input));

        final var entries = this.chartEntryRepository.findAll(
            ChartEntryRepository.Filters.withCategory(input.category()));

        final var accountIds = entries.stream().map(entry -> {

            var generic =
                input.fspCode().value() + "-" + entry.getCode().value() + "-" + input.currency();

            final var code = new AccountCode(generic);

            final var createInput = new CreateAccountCommand.Input(
                entry.getId(), input.ownerId(), input.currency(), code, generic, generic,
                OverdraftMode.LIMITED, BigDecimal.ZERO);

            final var output = this.createAccountCommand.execute(createInput);

            return output.accountId();

        }).toList();

        final var output = new Output(accountIds);

        LOGGER.info("CreateAccountInBulkCommand : output: ({})", ObjectLogger.log(output));

        return output;
    }

}
