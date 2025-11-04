package io.mojaloop.core.transfer.domain.model;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.core.common.datatype.converter.identifier.transfer.TransferExtensionIdJavaType;
import io.mojaloop.core.common.datatype.converter.identifier.transfer.UdfTransferIdJavaType;
import io.mojaloop.core.common.datatype.converter.type.fspiop.FspCodeConverter;
import io.mojaloop.core.common.datatype.identifier.transfer.TransferId;
import io.mojaloop.core.common.datatype.identifier.transfer.UdfTransferId;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.fspiop.spec.core.Currency;
import io.mojaloop.fspiop.spec.core.TransferState;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;

import java.math.BigDecimal;
import java.sql.Types;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Transfer extends JpaEntity<TransferId> {

    @Id
    @JavaType(TransferExtensionIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "transfer_id")
    protected TransferId id;

    @Basic
    @JavaType(UdfTransferIdJavaType.class)
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "udf_transfer_id", nullable = false, updatable = false, length = StringSizeConstraints.MAX_UDF_TRANSFER_ID_LENGTH)
    protected UdfTransferId udfTransferId;

    @Column(name = "payer_fsp", nullable = false, updatable = false, length = StringSizeConstraints.MAX_CODE_LENGTH)
    @Convert(converter = FspCodeConverter.class)
    protected FspCode payerFsp;

    @Column(name = "payee_fsp", nullable = false, updatable = false, length = StringSizeConstraints.MAX_CODE_LENGTH)
    @Convert(converter = FspCodeConverter.class)
    protected FspCode payeeFsp;

    @Column(name = "currency", nullable = false, updatable = false, length = StringSizeConstraints.MAX_CURRENCY_LENGTH)
    @Enumerated(EnumType.STRING)
    protected Currency currency;

    @Column(name = "transfer_amount", nullable = false, updatable = false, precision = 34, scale = 4)
    protected BigDecimal transferAmount;

    protected TransferState state;

}
