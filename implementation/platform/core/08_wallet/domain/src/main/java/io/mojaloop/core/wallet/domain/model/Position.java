package io.mojaloop.core.wallet.domain.model;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.component.jpa.JpaInstantConverter;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.data.DataConversion;
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.common.datatype.converter.identifier.wallet.PositionIdJavaType;
import io.mojaloop.core.common.datatype.converter.identifier.wallet.WalletOwnerIdConverter;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.wallet.contract.data.PositionData;
import io.mojaloop.fspiop.spec.core.Currency;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;

import java.math.BigDecimal;
import java.time.Instant;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(name = "wlt_position", uniqueConstraints = @UniqueConstraint(name = "wlt_wallet_owner_id_currency_UK", columnNames = {"wallet_owner_id", "currency"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Position extends JpaEntity<PositionId> implements DataConversion<PositionData> {

    @Id
    @JavaType(PositionIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "position_id")
    protected PositionId id;

    @Column(name = "wallet_owner_id")
    @Convert(converter = WalletOwnerIdConverter.class)
    protected WalletOwnerId walletOwnerId;

    @Column(name = "currency", length = StringSizeConstraints.MAX_CURRENCY_LENGTH, nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    protected Currency currency;

    @Column(name = "name", length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH, nullable = false, updatable = false)
    protected String name;

    @Column(name = "position", nullable = false, precision = 34, scale = 4)
    protected BigDecimal position = BigDecimal.ZERO;

    @Column(name = "reserved", nullable = false, precision = 34, scale = 4)
    protected BigDecimal reserved = BigDecimal.ZERO;

    @Column(name = "net_debit_cap", nullable = false, precision = 34, scale = 4)
    protected BigDecimal netDebitCap = BigDecimal.ZERO;

    @Column(name = "created_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant createdAt;

    public Position(final WalletOwnerId walletOwnerId, final Currency currency, final String name) {

        assert walletOwnerId != null;
        assert currency != null;
        assert name != null;

        this.id = new PositionId(Snowflake.get().nextId());
        this.walletOwnerId = walletOwnerId;
        this.currency = currency;
        this.name = name;
        this.createdAt = Instant.now();
        this.position = BigDecimal.ZERO;
        this.reserved = BigDecimal.ZERO;
        this.netDebitCap = this.netDebitCap == null ? BigDecimal.ZERO : this.netDebitCap;
    }

    public Position(final WalletOwnerId walletOwnerId, final Currency currency, final String name, final BigDecimal netDebitCap) {

        assert walletOwnerId != null;
        assert currency != null;
        assert name != null;

        this.id = new PositionId(Snowflake.get().nextId());
        this.walletOwnerId = walletOwnerId;
        this.currency = currency;
        this.name = name;
        this.createdAt = Instant.now();
        this.position = BigDecimal.ZERO;
        this.reserved = BigDecimal.ZERO;
        this.netDebitCap = netDebitCap == null ? BigDecimal.ZERO : netDebitCap;
    }

    @Override
    public PositionData convert() {

        return new PositionData(this.id, this.walletOwnerId, this.currency, this.name, this.position, this.reserved, this.netDebitCap, this.createdAt);
    }

    @Override
    public PositionId getId() {

        return this.id;
    }

}
