package org.mojave.core.wallet.domain.model;

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
import org.mojave.component.jpa.JpaEntity;
import org.mojave.component.jpa.JpaInstantConverter;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.component.misc.data.DataConversion;
import org.mojave.component.misc.handy.Snowflake;
import org.mojave.core.wallet.contract.data.WalletData;
import org.mojave.scheme.rule.converter.identifier.wallet.WalletIdJavaType;
import org.mojave.scheme.rule.converter.identifier.wallet.WalletOwnerIdJavaType;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.identifier.wallet.WalletId;
import org.mojave.scheme.rule.identifier.wallet.WalletOwnerId;

import java.time.Instant;
import java.util.Objects;

import static java.sql.Types.BIGINT;
import static org.mojave.core.wallet.contract.constant.WalletDefaultTag.DEFAULT_TAG;

@Getter
@Entity
@Table(
    name = "wlt_wallet",
    uniqueConstraints = @UniqueConstraint(
        name = "wlt_wallet_01_UK",
        columnNames = {
            "wallet_owner_id",
            "currency",
            "tag"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wallet extends JpaEntity<WalletId> implements DataConversion<WalletData> {

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
        name = "tag",
        length = StringSizeConstraints.MAX_ENUM_LENGTH,
        nullable = false,
        updatable = false)
    protected String tag = DEFAULT_TAG;

    @Column(
        name = "name",
        length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH,
        nullable = false,
        updatable = false)
    protected String name;

    @Column(name = "created_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant createdAt;

    public Wallet(final WalletOwnerId walletOwnerId, final Currency currency, final String tag,
                  final String name) {

        Objects.requireNonNull(walletOwnerId);
        Objects.requireNonNull(currency);
        Objects.requireNonNull(name);

        this.id = new WalletId(Snowflake.get().nextId());
        this.walletOwnerId = walletOwnerId;
        this.currency = currency;
        this.tag = tag == null || tag.isBlank() ? DEFAULT_TAG : tag;
        this.name = name;
        this.createdAt = Instant.now();

    }

    @Override
    public WalletData convert() {

        return new WalletData(
            this.id, this.walletOwnerId, this.currency, this.tag, this.name, this.createdAt);
    }

    @Override
    public WalletId getId() {

        return this.id;
    }

}
