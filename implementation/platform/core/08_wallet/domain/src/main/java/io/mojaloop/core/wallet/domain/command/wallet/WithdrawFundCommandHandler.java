package io.mojaloop.core.wallet.domain.command.wallet;

import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.common.datatype.identifier.wallet.BalanceUpdateId;
import io.mojaloop.core.wallet.contract.command.wallet.WithdrawFundCommand;
import io.mojaloop.core.wallet.contract.exception.wallet.InsufficientBalanceInWalletException;
import io.mojaloop.core.wallet.contract.exception.wallet.NoBalanceUpdateForTransactionException;
import io.mojaloop.core.wallet.domain.component.BalanceUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WithdrawFundCommandHandler implements WithdrawFundCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(WithdrawFundCommandHandler.class);

    private final BalanceUpdater balanceUpdater;

    public WithdrawFundCommandHandler(final BalanceUpdater balanceUpdater) {

        assert balanceUpdater != null;
        this.balanceUpdater = balanceUpdater;
    }

    @Override
    public Output execute(final Input input)
        throws NoBalanceUpdateForTransactionException, InsufficientBalanceInWalletException {

        LOGGER.info("Executing WithdrawFundCommand with input: {}", input);

        final var balanceUpdateId = new BalanceUpdateId(Snowflake.get().nextId());

        try {
            final var history = this.balanceUpdater.withdraw(input.transactionId(), input.transactionAt(),
                                                             balanceUpdateId, input.walletId(), input.amount(),
                                                             "Withdraw funds");

            var output = new Output(history.balanceUpdateId(), history.walletId(), history.action(),
                                    history.transactionId(), history.currency(), history.amount(), history.oldBalance(),
                                    history.newBalance(), history.transactionAt());

            LOGGER.info("WithdrawFundCommand executed successfully with output: {}", output);

            return output;

        } catch (final BalanceUpdater.NoBalanceUpdateException e) {

            LOGGER.error("Failed to withdraw funds for transaction: {}", input.transactionId());
            throw new NoBalanceUpdateForTransactionException(input.transactionId());

        } catch (final BalanceUpdater.InsufficientBalanceException e) {

            LOGGER.error("Failed to withdraw funds for transaction (insufficient balance): {}", input.transactionId());
            throw new InsufficientBalanceInWalletException(e.getWalletId(), e.getAmount(), e.getOldBalance(),
                                                           e.getTransactionId());
        }
    }

}
