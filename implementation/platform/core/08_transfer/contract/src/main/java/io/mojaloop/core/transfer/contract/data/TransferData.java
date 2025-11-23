package io.mojaloop.core.transfer.contract.data;

import io.mojaloop.core.common.datatype.enums.Direction;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.transfer.TransferId;
import io.mojaloop.core.common.datatype.identifier.transfer.UdfTransferId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.fspiop.spec.core.Currency;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import io.mojaloop.fspiop.spec.core.TransferState;

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
                           TransferState state,
                           Instant receivedAt,
                           Instant reservedAt,
                           Instant committedAt,
                           Instant abortedAt,
                           String error,
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
