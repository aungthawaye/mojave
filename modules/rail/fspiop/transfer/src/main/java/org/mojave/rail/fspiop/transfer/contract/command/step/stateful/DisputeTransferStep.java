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

package org.mojave.rail.fspiop.transfer.contract.command.step.stateful;

import org.mojave.scheme.rule.enums.transfer.DisputeReason;
import org.mojave.scheme.rule.identifier.transaction.TransactionId;
import org.mojave.scheme.rule.identifier.transfer.TransferId;
import org.mojave.scheme.rule.identifier.transfer.UdfTransferId;
import org.mojave.rail.fspiop.component.exception.FspiopException;

public interface DisputeTransferStep {

    String SUBJECT_NAME = "sub-fspiop-transfer.dispute-transfer-step";

    String TOPIC_NAME = "tp-fspiop-transfer.dispute-transfer-step";

    void execute(Input input) throws FspiopException;

    record Input(UdfTransferId udfTransferId,
                 TransactionId transactionId,
                 TransferId transferId,
                 DisputeReason disputeReason) { }

}
