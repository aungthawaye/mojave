package io.mojaloop.core.account.domain.model;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.component.jpa.JpaInstantConverter;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.data.DataConversion;
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.account.contract.data.AccountData;
import io.mojaloop.core.account.contract.exception.account.AccountCodeRequiredException;
import io.mojaloop.core.account.contract.exception.account.AccountDescriptionTooLongException;
import io.mojaloop.core.account.contract.exception.account.AccountNameRequiredException;
import io.mojaloop.core.account.contract.exception.account.AccountNameTooLongException;
import io.mojaloop.core.account.contract.exception.account.RequireFspChartTypeException;
import io.mojaloop.core.account.contract.exception.account.RequireFxpChartTypeException;
import io.mojaloop.core.account.contract.exception.account.RequireHubChartTypeException;
import io.mojaloop.core.common.datatype.converter.identifier.account.AccountIdJavaType;
import io.mojaloop.core.common.datatype.converter.identifier.account.ChartEntryIdConverter;
import io.mojaloop.core.common.datatype.converter.identifier.account.OwnerIdJavaType;
import io.mojaloop.core.common.datatype.converter.type.account.AccountCodeConverter;
import io.mojaloop.core.common.datatype.enums.ActivationStatus;
import io.mojaloop.core.common.datatype.enums.TerminationStatus;
import io.mojaloop.core.common.datatype.enums.account.AccountType;
import io.mojaloop.core.common.datatype.enums.account.ChartType;
import io.mojaloop.core.common.datatype.enums.account.OverdraftMode;
import io.mojaloop.core.common.datatype.identifier.account.AccountId;
import io.mojaloop.core.common.datatype.identifier.account.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.account.OwnerId;
import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.common.datatype.identifier.participant.FxpId;
import io.mojaloop.core.common.datatype.identifier.participant.HubId;
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
import jakarta.persistence.Index;
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
@Table(name = "acc_account", uniqueConstraints = {
    @UniqueConstraint(name = "acc_account_owner_id_currency_chart_entry_id_UK", columnNames = {"owner_id", "currency", "chart_entry_id"})},
       indexes = {@Index(name = "acc_account_owner_id_IDX", columnList = "owner_id"), @Index(name = "acc_account_currency_IDX", columnList = "currency")})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends JpaEntity<AccountId> implements DataConversion<AccountData> {

    @Id
    @JavaType(AccountIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "account_id", nullable = false, updatable = false)
    protected AccountId id;

    @Basic
    @JavaType(OwnerIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "owner_id", nullable = false, updatable = false)
    protected OwnerId ownerId;

    @Column(name = "type", nullable = false, updatable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected AccountType type;

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

    @Column(name = "activation_status", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected ActivationStatus activationStatus = ActivationStatus.ACTIVE;

    @Column(name = "termination_status", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected TerminationStatus terminationStatus = TerminationStatus.ALIVE;

    @Column(name = "chart_entry_id", nullable = false)
    @Convert(converter = ChartEntryIdConverter.class)
    protected ChartEntryId chartEntryId;

    @OneToOne(mappedBy = "account", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER, optional = false)
    protected LedgerBalance ledgerBalance;

    public Account(ChartEntry chartEntry,
                   FspId fspId,
                   Currency currency,
                   AccountCode code,
                   String name,
                   String description,
                   OverdraftMode overdraftMode,
                   BigDecimal overdraftLimit) {

        this(chartEntry, new OwnerId(fspId.getId()), currency, code, name, description, overdraftMode, overdraftLimit);

        if (chartEntry.chart.type != ChartType.FSP) {
            throw new RequireFspChartTypeException(chartEntry.chart.type);
        }
    }

    public Account(ChartEntry chartEntry,
                   FxpId fxpId,
                   Currency currency,
                   AccountCode code,
                   String name,
                   String description,
                   OverdraftMode overdraftMode,
                   BigDecimal overdraftLimit) {

        this(chartEntry, new OwnerId(fxpId.getId()), currency, code, name, description, overdraftMode, overdraftLimit);

        if (chartEntry.chart.type != ChartType.FXP) {
            throw new RequireFxpChartTypeException(chartEntry.chart.type);
        }
    }

    public Account(ChartEntry chartEntry,
                   HubId hubId,
                   Currency currency,
                   AccountCode code,
                   String name,
                   String description,
                   OverdraftMode overdraftMode,
                   BigDecimal overdraftLimit) {

        this(chartEntry, new OwnerId(hubId.getId()), currency, code, name, description, overdraftMode, overdraftLimit);

        if (chartEntry.chart.type != ChartType.HUB) {
            throw new RequireHubChartTypeException(chartEntry.chart.type);
        }
    }

    private Account(ChartEntry chartEntry,
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
        assert overdraftMode != OverdraftMode.LIMITED || overdraftLimit != null;

        this.id = new AccountId(Snowflake.get().nextId());
        this.chartEntryId = chartEntry.getId();
        this.ownerId = ownerId;
        this.type = chartEntry.getAccountType();
        this.currency = currency;
        this.code(code).name(name).description(description);
        this.createdAt = Instant.now();

        this.ledgerBalance = new LedgerBalance(this, this.type.getSide(), overdraftMode, overdraftLimit);
    }

    public void activate() {

        this.activationStatus = ActivationStatus.ACTIVE;
    }

    public Account code(AccountCode code) {

        if (code == null) {

            throw new AccountCodeRequiredException();
        }

        this.code = code;

        return this;

    }

    @Override
    public AccountData convert() {

        return new AccountData(this.getId(), this.ownerId, this.type, this.currency, this.code, this.name, this.description, this.createdAt,
                               this.activationStatus, this.terminationStatus, this.chartEntryId, this.ledgerBalance.convert());
    }

    public void deactivate() {

        this.activationStatus = ActivationStatus.INACTIVE;
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

    public void terminate() {

        this.terminationStatus = TerminationStatus.TERMINATED;
    }

}
