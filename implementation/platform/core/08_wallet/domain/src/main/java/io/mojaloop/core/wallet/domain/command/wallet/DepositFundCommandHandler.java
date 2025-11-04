package io.mojaloop.core.wallet.domain.command.wallet;

import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.common.datatype.identifier.wallet.BalanceUpdateId;
import io.mojaloop.core.wallet.contract.command.wallet.DepositFundCommand;
import io.mojaloop.core.wallet.contract.exception.wallet.NoBalanceUpdateForTransactionException;
import io.mojaloop.core.wallet.domain.component.BalanceUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DepositFundCommandHandler implements DepositFundCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(DepositFundCommandHandler.class);

    private final BalanceUpdater balanceUpdater;

    public DepositFundCommandHandler(final BalanceUpdater balanceUpdater) {

        assert balanceUpdater != null;

        this.balanceUpdater = balanceUpdater;
    }

    @Override
    public Output execute(final Input input) throws NoBalanceUpdateForTransactionException {

        LOGGER.info("Executing DepositFundCommand with input: {}", input);

        final var balanceUpdateId = new BalanceUpdateId(Snowflake.get().nextId());

        try {

            final var history = this.balanceUpdater.deposit(input.transactionId(), input.transactionAt(), balanceUpdateId, input.walletId(), input.amount(), input.description());

            var output = new Output(history.balanceUpdateId(),
                                    history.walletId(),
                                    history.action(),
                                    history.transactionId(),
                                    history.currency(),
                                    history.amount(),
                                    history.oldBalance(),
                                    history.newBalance(),
                                    history.transactionAt());

            LOGGER.info("DepositFundCommand executed successfully with output: {}", output);

            return output;

        } catch (final BalanceUpdater.NoBalanceUpdateException e) {

            LOGGER.error("Failed to deposit funds for transaction: {}", input.transactionId());
            throw new NoBalanceUpdateForTransactionException(input.transactionId());
        }
    }

}
