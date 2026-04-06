package org.mojave.wallet.domain.model;

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
import org.mojave.common.datatype.converter.identifier.wallet.WalletIdJavaType;
import org.mojave.common.datatype.converter.identifier.wallet.WalletOwnerIdJavaType;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.wallet.WalletId;
import org.mojave.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.component.jpa.JpaEntity;
import org.mojave.component.jpa.JpaInstantConverter;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.component.misc.data.DataConversion;
import org.mojave.component.misc.handy.Snowflake;
import org.mojave.wallet.contract.data.WalletData;

import java.time.Instant;
import java.util.Objects;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(
    name = "wlt_wallet",
    uniqueConstraints = @UniqueConstraint(
        name = "wlt_wallet_01_UK",
        columnNames = {
            "wallet_owner_id",
            "currency",
            "scenario"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wallet extends JpaEntity<WalletId> implements DataConversion<WalletData> {

    public static final String DEFAULT_SCENARIO = "DEFAULT";

    @Id
    @JavaType(WalletIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "wallet_id")
    protected WalletId id;

    @JavaType(WalletOwnerIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "wallet_owner_id")
    protected WalletOwnerId walletOwnerId;

    @Column(
        name = "currency",
        length = StringSizeConstraints.MAX_CURRENCY_LENGTH,
        nullable = false,
        updatable = false)
    @Enumerated(EnumType.STRING)
    protected Currency currency;

    @Column(
        name = "scenario",
        length = StringSizeConstraints.MAX_ENUM_LENGTH,
        nullable = false,
        updatable = false)
    protected String scenario;

    @Column(
        name = "name",
        length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH,
        nullable = false,
        updatable = false)
    protected String name;

    @Column(name = "created_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant createdAt;

    public Wallet(final WalletOwnerId walletOwnerId,
                  final Currency currency,
                  final String scenario,
                  final String name) {

        Objects.requireNonNull(walletOwnerId);
        Objects.requireNonNull(currency);
        Objects.requireNonNull(scenario);
        Objects.requireNonNull(name);

        this.id = new WalletId(Snowflake.get().nextId());
        this.walletOwnerId = walletOwnerId;
        this.currency = currency;
        this.scenario = scenario;
        this.name = name;
        this.createdAt = Instant.now();

    }

    @Override
    public WalletData convert() {

        return new WalletData(
            this.id, this.walletOwnerId, this.currency, this.scenario, this.name, this.createdAt);
    }

    @Override
    public WalletId getId() {

        return this.id;
    }

}
