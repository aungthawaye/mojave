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

package org.mojave.core.wallet.domain.command.position;

import org.mojave.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.common.datatype.identifier.wallet.WalletId;
import org.mojave.component.misc.handy.Snowflake;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.wallet.contract.command.position.FulfilPositionsCommand;
import org.mojave.core.wallet.contract.engine.WalletEngine;
import org.mojave.core.wallet.contract.exception.WalletNotFoundException;
import org.mojave.core.wallet.contract.exception.position.FailedToFulfilPositionsException;
import org.mojave.core.wallet.domain.cache.WalletCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FulfilPositionsCommandHandler implements FulfilPositionsCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        FulfilPositionsCommandHandler.class);

    private final WalletEngine walletEngine;

    private final WalletCache walletCache;

    public FulfilPositionsCommandHandler(final WalletEngine walletEngine,
                                         final WalletCache walletCache) {

        Objects.requireNonNull(walletEngine);
        Objects.requireNonNull(walletCache);

        this.walletEngine = walletEngine;
        this.walletCache = walletCache;
    }

    @Override
    public Output execute(Input input) throws FailedToFulfilPositionsException {

        LOGGER.info("FulfilPositionsCommand : input: ({})", ObjectLogger.log(input));

        final var payeeWallet = this.walletCache.get(
            input.payeeWalletOwnerId(), input.currency(), input.purpose());

        if (payeeWallet == null) {

            throw new WalletNotFoundException(
                input.payeeWalletOwnerId(), input.currency(), input.purpose());
        }

        final var payeeWalletId = new WalletId(payeeWallet.walletId().getId());
        final var reservationCommitId = new PositionUpdateId(Snowflake.get().nextId());
        final var payeePositionCommitId = new PositionUpdateId(Snowflake.get().nextId());

        try {
            final var result = this.walletEngine.fulfil(
                input.reservationId(), reservationCommitId, payeePositionCommitId, payeeWalletId,
                input.description());

            final var output = new FulfilPositionsCommand.Output(
                result.payerCommitmentId(), result.payeeCommitmentId());

            LOGGER.info("FulfilPositionsCommand : output: ({})", ObjectLogger.log(output));

            return output;

        } catch (final WalletEngine.NoPositionFulfilmentException e) {

            LOGGER.error("Error:", e);
            throw new FailedToFulfilPositionsException(e.getReservationId());
        }
    }

}
