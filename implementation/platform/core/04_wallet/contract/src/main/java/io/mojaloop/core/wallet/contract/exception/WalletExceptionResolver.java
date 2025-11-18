package io.mojaloop.core.wallet.contract.exception;

import io.mojaloop.component.misc.error.RestErrorResponse;
import io.mojaloop.core.wallet.contract.exception.position.CommitFailedInWalletException;
import io.mojaloop.core.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import io.mojaloop.core.wallet.contract.exception.position.PositionAlreadyExistsException;
import io.mojaloop.core.wallet.contract.exception.position.PositionIdNotFoundException;
import io.mojaloop.core.wallet.contract.exception.position.PositionLimitExceededException;
import io.mojaloop.core.wallet.contract.exception.position.RollbackFailedInWalletException;
import io.mojaloop.core.wallet.contract.exception.wallet.BalanceUpdateIdNotFoundException;
import io.mojaloop.core.wallet.contract.exception.wallet.InsufficientBalanceInWalletException;
import io.mojaloop.core.wallet.contract.exception.wallet.NoBalanceUpdateForTransactionException;
import io.mojaloop.core.wallet.contract.exception.wallet.ReversalFailedInWalletException;
import io.mojaloop.core.wallet.contract.exception.wallet.WalletAlreadyExistsException;
import io.mojaloop.core.wallet.contract.exception.wallet.WalletIdNotFoundException;

public class WalletExceptionResolver {

    public static Throwable resolve(final RestErrorResponse error) {

        final var code = error.code();
        final var extras = error.extras();

        return switch (code) {
            // wallet package
            case BalanceUpdateIdNotFoundException.CODE -> BalanceUpdateIdNotFoundException.from(extras);
            case InsufficientBalanceInWalletException.CODE -> InsufficientBalanceInWalletException.from(extras);
            case NoBalanceUpdateForTransactionException.CODE -> NoBalanceUpdateForTransactionException.from(extras);
            case ReversalFailedInWalletException.CODE -> ReversalFailedInWalletException.from(extras);
            case WalletAlreadyExistsException.CODE -> WalletAlreadyExistsException.from(extras);
            case WalletIdNotFoundException.CODE -> WalletIdNotFoundException.from(extras);

            // position package
            case CommitFailedInWalletException.CODE -> CommitFailedInWalletException.from(extras);
            case NoPositionUpdateForTransactionException.CODE -> NoPositionUpdateForTransactionException.from(extras);
            case PositionAlreadyExistsException.CODE -> PositionAlreadyExistsException.from(extras);
            case PositionIdNotFoundException.CODE -> PositionIdNotFoundException.from(extras);
            case PositionLimitExceededException.CODE -> PositionLimitExceededException.from(extras);
            case RollbackFailedInWalletException.CODE -> RollbackFailedInWalletException.from(extras);

            default -> throw new RuntimeException("Unknown exception code: " + code);
        };
    }

}
