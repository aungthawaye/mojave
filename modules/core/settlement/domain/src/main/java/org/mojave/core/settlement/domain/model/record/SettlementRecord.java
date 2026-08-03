package org.mojave.core.settlement.domain.model.record;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;
import org.mojave.component.jpa.JpaEntity;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.component.misc.data.DataConversion;
import org.mojave.component.misc.handy.Snowflake;
import org.mojave.core.settlement.contract.data.SettlementRecordData;
import org.mojave.core.settlement.domain.model.SettlementModel;
import org.mojave.core.settlement.domain.model.SettlementWindow;
import org.mojave.core.settlement.domain.model.definition.SettlementModelDefinition;
import org.mojave.scheme.rule.converter.identifier.participant.SspIdJavaType;
import org.mojave.scheme.rule.converter.identifier.settlement.SettlementRecordIdJavaType;
import org.mojave.scheme.rule.converter.identifier.transaction.TransactionIdJavaType;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.enums.settlement.LiquidityDirection;
import org.mojave.scheme.rule.enums.settlement.SettlementMethod;
import org.mojave.scheme.rule.enums.settlement.SettlementRecordStatus;
import org.mojave.scheme.rule.identifier.participant.FspId;
import org.mojave.scheme.rule.identifier.participant.SspId;
import org.mojave.scheme.rule.identifier.settlement.SettlementRecordId;
import org.mojave.scheme.rule.identifier.transaction.TransactionId;
import org.mojave.scheme.rule.scenario.ScenarioType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(
    name = "stl_settlement_record",
    indexes = {
        @Index(
            name = "stl_settlement_record_01_IDX",
            columnList = "scenario, currency, status"),
        @Index(
            name = "stl_settlement_record_02_IDX",
            columnList = "settlement_model_id, status"),
        @Index(
            name = "stl_settlement_record_03_IDX",
            columnList = "settlement_window_id, status")},
    uniqueConstraints = {
        @UniqueConstraint(
            name = "stl_settlement_record_01_UK",
            columnNames = {"transaction_id"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SettlementRecord extends JpaEntity<SettlementRecordId>
    implements DataConversion<SettlementRecordData> {

    @Id
    @JavaType(SettlementRecordIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "settlement_record_id",
        nullable = false,
        updatable = false)
    protected SettlementRecordId id;

    @JavaType(TransactionIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "transaction_id",
        nullable = false,
        updatable = false)
    protected TransactionId transactionId;

    @Column(
        name = "scenario",
        nullable = false,
        updatable = false,
        length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected ScenarioType scenario;

    @Column(
        name = "currency",
        nullable = false,
        updatable = false,
        length = StringSizeConstraints.MAX_CURRENCY_LENGTH)
    @Enumerated(EnumType.STRING)
    protected Currency currency;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
        name = "settlement_model_definition_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "stl_settlement_model_definition_stl_settlement_record_FK"))
    protected SettlementModelDefinition settlementModelDefinition;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
        name = "settlement_model_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "stl_settlement_model_stl_settlement_record_FK"))
    protected SettlementModel settlementModel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
        name = "settlement_window_id",
        foreignKey = @ForeignKey(name = "stl_settlement_window_stl_settlement_record_FK"))
    protected SettlementWindow settlementWindow;

    @Column(
        name = "settlement_method",
        nullable = false,
        updatable = false,
        length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected SettlementMethod settlementMethod;

    @JavaType(SspIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "ssp_id",
        nullable = false,
        updatable = false)
    protected SspId sspId;

    @Column(
        name = "status",
        nullable = false,
        length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected SettlementRecordStatus status = SettlementRecordStatus.PENDING;

    @OneToMany(
        mappedBy = "settlementRecord",
        orphanRemoval = true,
        cascade = {CascadeType.ALL},
        fetch = FetchType.EAGER)
    protected List<SettlementAmount> settlementAmounts = new ArrayList<>();

    public SettlementRecord(final TransactionId transactionId, final ScenarioType scenario,
                            final Currency currency,
                            final SettlementModelDefinition settlementModelDefinition,
                            final SettlementWindow settlementWindow) {

        Objects.requireNonNull(settlementModelDefinition);

        this.id = new SettlementRecordId(Snowflake.get().nextId());
        this.transactionId = Objects.requireNonNull(transactionId);
        this.scenario = Objects.requireNonNull(scenario);
        this.currency = Objects.requireNonNull(currency);
        this.settlementModelDefinition = settlementModelDefinition;
        this.settlementModel = settlementModelDefinition.getSettlementModel();
        this.settlementMethod = this.settlementModel.getSettlementMethod();
        this.sspId = this.settlementModel.getSspId();
        this.settlementWindow(settlementWindow);
    }

    public SettlementAmount addSettlementAmount(final String participant, final FspId fspId,
                                                final String amountName, final BigDecimal amount,
                                                final Currency currency,
                                                final LiquidityDirection direction) {

        final var settlementAmount = new SettlementAmount(
            this, participant, fspId, amountName, amount, currency, direction);

        this.settlementAmounts.add(settlementAmount);

        return settlementAmount;
    }

    @Override
    public SettlementRecordData convert() {

        final var amountData = this.settlementAmounts
                                   .stream()
                                   .map(amount -> new SettlementRecordData.SettlementAmountData(
                                       amount.getId(), amount.getParticipant(), amount.getFspId(),
                                       amount.getAmountName(), amount.getAmount(),
                                       amount.getCurrency(), amount.getDirection()))
                                   .toList();

        final var settlementWindowId =
            this.settlementWindow == null ? null : this.settlementWindow.getId();

        return new SettlementRecordData(
            this.id, this.transactionId, this.scenario, this.currency,
            this.settlementModelDefinition.getId(), this.settlementModel.getId(),
            settlementWindowId, this.settlementMethod, this.sspId, this.status, amountData);
    }

    public void fail() {

        this.status = SettlementRecordStatus.FAILED;
    }

    @Override
    public SettlementRecordId getId() {

        return this.id;
    }

    public List<SettlementAmount> getSettlementAmounts() {

        return Collections.unmodifiableList(this.settlementAmounts);
    }

    public void settle() {

        this.status = SettlementRecordStatus.SETTLED;
    }

    public SettlementRecord settlementWindow(final SettlementWindow settlementWindow) {

        if (this.settlementMethod == SettlementMethod.BATCH && settlementWindow == null) {
            throw new IllegalArgumentException(
                "Batch settlement record requires a settlement window.");
        }

        if (this.settlementMethod == SettlementMethod.INSTANT && settlementWindow != null) {
            throw new IllegalArgumentException(
                "Instant settlement record must not have a settlement window.");
        }

        if (settlementWindow != null &&
                !settlementWindow.accepts(this.settlementModel, this.currency)) {
            throw new IllegalArgumentException(
                "Settlement window does not match the settlement record.");
        }

        this.settlementWindow = settlementWindow;

        return this;
    }

    public void settling() {

        this.status = SettlementRecordStatus.SETTLING;
    }

}
