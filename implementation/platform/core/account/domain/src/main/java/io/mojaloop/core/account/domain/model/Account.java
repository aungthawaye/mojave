package io.mojaloop.core.account.domain.model;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.component.jpa.JpaInstantConverter;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.account.contract.exception.account.AccountCodeRequiredException;
import io.mojaloop.core.account.contract.exception.account.AccountDescriptionTooLongException;
import io.mojaloop.core.account.contract.exception.account.AccountNameRequiredException;
import io.mojaloop.core.account.contract.exception.account.AccountNameTooLongException;
import io.mojaloop.core.common.datatype.converter.identifier.account.AccountIdJavaType;
import io.mojaloop.core.common.datatype.converter.identifier.account.OwnerIdJavaType;
import io.mojaloop.core.common.datatype.converter.type.account.AccountCodeConverter;
import io.mojaloop.core.common.datatype.enums.TerminationStatus;
import io.mojaloop.core.common.datatype.enums.account.OverdraftMode;
import io.mojaloop.core.common.datatype.identifier.account.AccountId;
import io.mojaloop.core.common.datatype.identifier.account.OwnerId;
import io.mojaloop.core.common.datatype.type.account.AccountCode;
import io.mojaloop.fspiop.spec.core.Currency;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
@Table(name = "acc_account",
       uniqueConstraints = @UniqueConstraint(name = "uk_owner_id_currency_chart_entry_id", columnNames = {"owner_id", "currency", "chart_entry_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends JpaEntity<AccountId> {

    @Id
    @JavaType(AccountIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "account_id", nullable = false, updatable = false)
    protected AccountId id;

    @Basic
    @JavaType(OwnerIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    protected OwnerId ownerId;

    @Column(name = "currency", nullable = false, updatable = false, length = StringSizeConstraints.MAX_CURRENCY_LENGTH)
    @Enumerated(EnumType.STRING)
    protected Currency currency;

    @Basic
    @Column(name = "code", nullable = false, length = StringSizeConstraints.MAX_CODE_LENGTH)
    @Convert(converter = AccountCodeConverter.class)
    protected AccountCode code;

    @Column(name = "name", nullable = false, length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String name;

    @Column(name = "description", length = StringSizeConstraints.MAX_DESCRIPTION_LENGTH)
    protected String description;

    @Column(name = "created_at", nullable = false)
    @Convert(converter = JpaInstantConverter.class)
    protected Instant createdAt;

    @Column(name = "termination_status", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected TerminationStatus terminationStatus = TerminationStatus.ALIVE;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chart_entry_id", nullable = false)
    protected ChartEntry chartEntry;

    @OneToOne(mappedBy = "account", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ledger_balance_id", nullable = false)
    protected LedgerBalance ledgerBalance;

    public Account(ChartEntry chartEntry,
                   OwnerId ownerId,
                   Currency currency,
                   AccountCode code,
                   String name,
                   String description,
                   OverdraftMode overdraftMode,
                   BigDecimal overdraftLimit) {

        assert chartEntry != null;
        assert ownerId != null;
        assert currency != null;
        assert code != null;
        assert name != null;
        assert description != null;
        assert overdraftMode != null;
        assert overdraftMode != OverdraftMode.LIMIT || overdraftLimit != null;

        this.id = new AccountId(Snowflake.get().nextId());
        this.chartEntry = chartEntry;
        this.ownerId = ownerId;
        this.currency = currency;
        this.code(code).name(name).description(description);
        this.createdAt = Instant.now();

        this.ledgerBalance = new LedgerBalance(this, this.chartEntry.getAccountType().getNormalSide(), overdraftMode, overdraftLimit);
    }

    public Account code(AccountCode code) {

        if (code == null) {

            throw new AccountCodeRequiredException();
        }

        this.code = code;

        return this;

    }

    public Account description(String description) {

        if (description == null) {
            return this;
        }

        var value = description.trim();

        if (value.length() > StringSizeConstraints.MAX_DESCRIPTION_LENGTH) {
            throw new AccountDescriptionTooLongException();
        }

        this.description = description;

        return this;
    }

    @Override
    public AccountId getId() {

        return this.id;
    }

    public Account name(String name) {

        if (name == null || name.isBlank()) {
            throw new AccountNameRequiredException();
        }

        var value = name.trim();

        if (value.length() > StringSizeConstraints.MAX_NAME_TITLE_LENGTH) {
            throw new AccountNameTooLongException();
        }

        this.name = name;

        return this;
    }

}
