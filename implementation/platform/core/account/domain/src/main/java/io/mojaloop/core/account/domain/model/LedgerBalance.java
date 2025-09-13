package io.mojaloop.core.account.domain.model;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.component.jpa.JpaInstantConverter;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.core.common.datatype.converter.identifier.account.LedgerBalanceIdJavaType;
import io.mojaloop.core.common.datatype.enums.account.NormalSide;
import io.mojaloop.core.common.datatype.enums.account.OverdraftMode;
import io.mojaloop.core.common.datatype.identifier.account.LedgerBalanceId;
import io.mojaloop.fspiop.component.handy.FspiopCurrencies;
import io.mojaloop.fspiop.spec.core.Currency;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

import static java.sql.Types.BIGINT;

@Getter
@Table(name = "acc_ledger_balance")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LedgerBalance extends JpaEntity<LedgerBalanceId> {

    @Id
    @JavaType(LedgerBalanceIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    protected LedgerBalanceId id;

    @Column(name = "currency", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected Currency currency;

    @Column(name = "scale", nullable = false, updatable = false)
    protected int scale;

    @Column(name = "nature", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected NormalSide nature;

    @Column(name = "posted_debits", precision = 20, scale = 4)
    protected BigDecimal postedDebits;

    @Column(name = "posted_credits", precision = 20, scale = 4)
    protected BigDecimal postedCredits;

    @Column(name = "overdraft_mode", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected OverdraftMode overdraftMode;

    @Column(name = "overdraft_limit", precision = 20, scale = 4)
    protected BigDecimal overdraftLimit;

    @Column(name = "created_at", nullable = false)
    @Convert(converter = JpaInstantConverter.class)
    protected Instant createdAt;

    @MapsId
    @OneToOne(optional = false)
    @JoinColumn(name = "id", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_ledger_balance_account"))
    protected Account account;

    public LedgerBalance(Account account, NormalSide nature, OverdraftMode overdraftMode, BigDecimal overdraftLimit) {

        assert account != null;
        assert nature != null;
        assert overdraftMode != null;
        assert overdraftLimit != null;

        this.account = account;

        this.id = new LedgerBalanceId(this.account.id.getId());

        this.currency = account.currency;
        this.scale = FspiopCurrencies.get(account.currency).scale();
        this.nature = nature;
        this.postedDebits = BigDecimal.ZERO;
        this.postedCredits = BigDecimal.ZERO;
        this.overdraftMode = overdraftMode;
        this.overdraftLimit = overdraftLimit;
        this.createdAt = Instant.now();
    }

    @Override
    public LedgerBalanceId getId() {

        return this.id;
    }

    private BigDecimal norm(BigDecimal amount) {

        assert amount != null;

        var n = amount.setScale(this.scale, RoundingMode.UNNECESSARY);

        if (n.signum() < 0) {
            throw new IllegalArgumentException("Amount must be >= 0");
        }

        return n;
    }

}
