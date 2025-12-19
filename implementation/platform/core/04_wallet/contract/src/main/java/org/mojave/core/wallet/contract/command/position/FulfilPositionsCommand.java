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
package org.mojave.core.wallet.contract.command.position;

import org.mojave.core.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.core.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.core.wallet.contract.exception.position.FailedToFulfilPositionsException;
import org.mojave.fspiop.spec.core.Currency;

public interface FulfilPositionsCommand {

    Output execute(Input input) throws FailedToFulfilPositionsException;

    record Input(PositionUpdateId reservationId,
                 WalletOwnerId payeeWalletOwnerId,
                 Currency currency,
                 String description) { }

    record Output(PositionUpdateId payerCommitId, PositionUpdateId payeeCommitId) { }

}
