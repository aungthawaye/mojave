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

package org.mojave.rail.fspiop.transfer.contract.command.step.financial;

import org.mojave.scheme.common.datatype.identifier.transaction.TransactionId;
import org.mojave.scheme.common.datatype.identifier.transfer.UdfTransferId;
import org.mojave.scheme.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.core.participant.contract.data.FspData;
import org.mojave.core.wallet.contract.exception.position.FailedToCommitReservationException;
import org.mojave.rail.fspiop.component.exception.FspiopException;
import org.mojave.scheme.fspiop.core.Currency;

import java.time.Instant;

public interface FulfilPositionsStep {

    Output execute(Input input) throws FailedToCommitReservationException, FspiopException;

    record Input(UdfTransferId udfTransferId,
                 TransactionId transactionId,
                 Instant transactionAt,
                 FspData payerFsp,
                 FspData payeeFsp,
                 PositionUpdateId positionReservationId,
                 Currency currency,
                 String description) { }

    record Output(PositionUpdateId payerCommitId, PositionUpdateId payeeCommitId) { }

}
