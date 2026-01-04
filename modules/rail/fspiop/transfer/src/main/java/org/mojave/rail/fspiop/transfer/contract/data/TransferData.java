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

package org.mojave.rail.fspiop.transfer.contract.data;

import org.mojave.scheme.common.datatype.enums.Direction;
import org.mojave.scheme.common.datatype.enums.transfer.AbortReason;
import org.mojave.scheme.common.datatype.enums.transfer.DisputeReason;
import org.mojave.scheme.common.datatype.enums.transfer.TransferStatus;
import org.mojave.scheme.common.datatype.identifier.participant.FspId;
import org.mojave.scheme.common.datatype.identifier.transaction.TransactionId;
import org.mojave.scheme.common.datatype.identifier.transfer.TransferId;
import org.mojave.scheme.common.datatype.identifier.transfer.UdfTransferId;
import org.mojave.scheme.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.scheme.fspiop.core.Currency;
import org.mojave.scheme.fspiop.core.PartyIdType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public record TransferData(TransferId transferId,
                           TransactionId transactionId,
                           Instant transactionAt,
                           UdfTransferId udfTransferId,
                           FspId payerFspId,
                           PartyData payer,
                           FspId payeeFspId,
                           PartyData payee,
                           Currency currency,
                           BigDecimal transferAmount,
                           Instant requestExpiration,
                           PositionUpdateId reservationId,
                           TransferStatus status,
                           Instant receivedAt,
                           Instant reservedAt,
                           Instant committedAt,
                           Instant abortedAt,
                           AbortReason abortReason,
                           Instant disputeAt,
                           DisputeReason disputeReason,
                           Instant reservationTimeoutAt,
                           Instant payeeCompletedAt,
                           List<TransferExtensionData> extensions,
                           String ilpFulfilment,
                           TransferIlpPacketData ilpPacket) {

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof TransferData transferData)) {
            return false;
        }
        return Objects.equals(transferId, transferData.transferId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(transferId);
    }

    public record PartyData(PartyIdType partyIdType, String partyId, String subId) { }

    public record TransferExtensionData(Direction direction, String key, String value) { }

    public record TransferIlpPacketData(String ilpPacket, String condition) { }

}
