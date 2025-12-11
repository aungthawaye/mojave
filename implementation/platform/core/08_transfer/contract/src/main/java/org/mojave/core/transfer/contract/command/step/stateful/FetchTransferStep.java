/*-
 * ================================================================================
 * Mojave
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
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
 * ================================================================================
 */
package org.mojave.core.transfer.contract.command.step.stateful;

import org.mojave.core.common.datatype.identifier.transaction.TransactionId;
import org.mojave.core.common.datatype.identifier.transfer.TransferId;
import org.mojave.core.common.datatype.identifier.transfer.UdfTransferId;
import org.mojave.core.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.fspiop.common.exception.FspiopException;
import org.mojave.fspiop.spec.core.Currency;

import java.math.BigDecimal;
import java.time.Instant;

public interface FetchTransferStep {

    Output execute(Input input) throws FspiopException;

    record Input(UdfTransferId udfTransferId) { }

    record Output(TransferId transferId,
                  org.mojave.core.common.datatype.enums.transfer.TransferStatus state,
                  PositionUpdateId reservationId,
                  Currency currency,
                  BigDecimal transferAmount,
                  BigDecimal payeeFspFee,
                  BigDecimal payeeFspCommission,
                  TransactionId transactionId,
                  Instant transactionAt) { }

}
