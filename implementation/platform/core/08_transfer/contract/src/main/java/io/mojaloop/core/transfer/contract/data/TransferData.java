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

package io.mojaloop.core.transfer.contract.data;

import io.mojaloop.core.common.datatype.enums.Direction;
import io.mojaloop.core.common.datatype.enums.transfer.AbortReason;
import io.mojaloop.core.common.datatype.enums.transfer.DisputeReason;
import io.mojaloop.core.common.datatype.enums.transfer.TransferStatus;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.transfer.TransferId;
import io.mojaloop.core.common.datatype.identifier.transfer.UdfTransferId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.fspiop.spec.core.Currency;
import io.mojaloop.fspiop.spec.core.PartyIdType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public record TransferData(TransferId transferId,
                           TransactionId transactionId,
                           Instant transactionAt,
                           UdfTransferId udfTransferId,
                           FspCode payerFsp,
                           PartyData payer,
                           FspCode payeeFsp,
                           PartyData payee,
                           Currency currency,
                           BigDecimal transferAmount,
                           Instant requestExpiration,
                           PositionUpdateId reservationId,
                           PositionUpdateId payerCommitId,
                           PositionUpdateId payeeCommitId,
                           PositionUpdateId rollbackId,
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
