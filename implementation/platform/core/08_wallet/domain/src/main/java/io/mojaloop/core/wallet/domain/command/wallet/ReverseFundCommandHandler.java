package io.mojaloop.core.wallet.domain.command.wallet;

import io.mojaloop.core.wallet.contract.command.wallet.ReverseFundCommand;
import io.mojaloop.core.wallet.contract.exception.wallet.BalanceUpdateIdNotFoundException;
import io.mojaloop.core.wallet.contract.exception.wallet.ReversalFailedInWalletException;
import io.mojaloop.core.wallet.domain.component.BalanceUpdater;
import io.mojaloop.core.wallet.domain.repository.BalanceUpdateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ReverseFundCommandHandler implements ReverseFundCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReverseFundCommandHandler.class);

    private final BalanceUpdateRepository balanceUpdateRepository;

    private final BalanceUpdater balanceUpdater;

    public ReverseFundCommandHandler(BalanceUpdateRepository balanceUpdateRepository, BalanceUpdater balanceUpdater) {

        assert balanceUpdateRepository != null;
        assert balanceUpdater != null;

        this.balanceUpdateRepository = balanceUpdateRepository;
        this.balanceUpdater = balanceUpdater;
    }

    @Override
    public Output execute(final Input input) throws ReversalFailedInWalletException {

        LOGGER.info("Executing ReverseFundCommand with input: {}", input);

        this.balanceUpdateRepository.findById(input.reversalId()).orElseThrow(() -> new BalanceUpdateIdNotFoundException(input.reversalId()));

        var alreadyReversed = this.balanceUpdateRepository.findOne(BalanceUpdateRepository.Filters.withReversalId(input.reversalId())).isPresent();

        if (alreadyReversed) {

            LOGGER.info("Balance update id: {} has already been reversed.", input.reversalId());
            throw new ReversalFailedInWalletException(input.reversalId());
        }

        try {

            final var history = this.balanceUpdater.reverse(input.reversalId(), input.balanceUpdateId());

            var output = new Output(history.balanceUpdateId(),
                                    history.walletId(),
                                    history.action(),
                                    history.transactionId(),
                                    history.currency(),
                                    history.amount(),
                                    history.oldBalance(),
                                    history.newBalance(),
                                    history.transactionAt(),
                                    history.reversalId());

            LOGGER.info("ReverseFundCommand executed successfully with output: {}", output);

            return output;

        } catch (BalanceUpdater.ReversalFailedException e) {

            LOGGER.error("Failed to reverse funds for balanceUpdateId: {}", input.balanceUpdateId());
            throw new ReversalFailedInWalletException(e.getReversalId());
        }
    }

}
