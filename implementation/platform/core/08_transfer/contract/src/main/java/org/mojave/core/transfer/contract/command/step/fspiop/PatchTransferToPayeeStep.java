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

package org.mojave.core.transfer.contract.command.step.fspiop;

import org.mojave.core.common.datatype.identifier.transaction.TransactionId;
import org.mojave.core.common.datatype.identifier.transfer.UdfTransferId;
import org.mojave.core.participant.contract.data.FspData;
import org.mojave.fspiop.component.exception.FspiopException;
import org.mojave.specification.fspiop.core.ExtensionList;
import org.mojave.specification.fspiop.core.TransferState;

public interface PatchTransferToPayeeStep {

    Output execute(Input input) throws FspiopException;

    record Input(UdfTransferId udfTransferId,
                 TransactionId transactionId,
                 FspData payeeFsp,
                 TransferState state,
                 ExtensionList extensionList) { }

    record Output() { }

}
