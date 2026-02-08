/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===
 */

package org.mojave.core.wallet.contract.exception;

import org.mojave.component.misc.error.RestErrorResponse;
import org.mojave.core.wallet.contract.exception.balance.BalanceAlreadyExistsException;
import org.mojave.core.wallet.contract.exception.balance.BalanceIdNotFoundException;
import org.mojave.core.wallet.contract.exception.balance.BalanceUpdateIdNotFoundException;
import org.mojave.core.wallet.contract.exception.balance.InsufficientBalanceException;
import org.mojave.core.wallet.contract.exception.balance.NoBalanceUpdateForTransactionException;
import org.mojave.core.wallet.contract.exception.balance.ReversalFailedInWalletException;
import org.mojave.core.wallet.contract.exception.position.FailedToCommitReservationException;
import org.mojave.core.wallet.contract.exception.position.FailedToRollbackReservationException;
import org.mojave.core.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import org.mojave.core.wallet.contract.exception.position.PositionAlreadyExistsException;
import org.mojave.core.wallet.contract.exception.position.PositionIdNotFoundException;
import org.mojave.core.wallet.contract.exception.position.PositionLimitExceededException;
import org.mojave.core.wallet.contract.exception.position.PositionNotExistException;

public class WalletExceptionResolver {

    public static Throwable resolve(final RestErrorResponse error) {

        final var code = error.code();
        final var extras = error.extras();

        return switch (code) {
            // balance package
            case BalanceUpdateIdNotFoundException.CODE ->
                BalanceUpdateIdNotFoundException.from(extras);
            case InsufficientBalanceException.CODE -> InsufficientBalanceException.from(extras);
            case NoBalanceUpdateForTransactionException.CODE ->
                NoBalanceUpdateForTransactionException.from(extras);
            case ReversalFailedInWalletException.CODE ->
                ReversalFailedInWalletException.from(extras);
            case BalanceAlreadyExistsException.CODE -> BalanceAlreadyExistsException.from(extras);
            case BalanceIdNotFoundException.CODE -> BalanceIdNotFoundException.from(extras);

            // position package
            case FailedToCommitReservationException.CODE ->
                FailedToCommitReservationException.from(extras);
            case NoPositionUpdateForTransactionException.CODE ->
                NoPositionUpdateForTransactionException.from(extras);
            case PositionAlreadyExistsException.CODE -> PositionAlreadyExistsException.from(extras);
            case PositionIdNotFoundException.CODE -> PositionIdNotFoundException.from(extras);
            case PositionLimitExceededException.CODE -> PositionLimitExceededException.from(extras);
            case PositionNotExistException.CODE -> PositionNotExistException.from(extras);
            case FailedToRollbackReservationException.CODE ->
                FailedToRollbackReservationException.from(extras);

            default -> throw new RuntimeException("Unknown exception code: " + code);
        };
    }

}
