package io.mojaloop.core.transaction.domain.model.definition;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.data.DataConversion;
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.common.datatype.converter.identifier.accounting.ChartEntryIdJavaType;
import io.mojaloop.core.common.datatype.converter.identifier.transaction.DefinitionIdJavaType;
import io.mojaloop.core.common.datatype.converter.identifier.transaction.PostingIdJavaType;
import io.mojaloop.core.common.datatype.enums.ActivationStatus;
import io.mojaloop.core.common.datatype.enums.TerminationStatus;
import io.mojaloop.core.common.datatype.enums.accounting.Side;
import io.mojaloop.core.common.datatype.enums.trasaction.definition.fundtransfer.PostingAmountType;
import io.mojaloop.core.common.datatype.enums.trasaction.definition.fundtransfer.PostingOwnerType;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.transaction.DefinitionId;
import io.mojaloop.core.common.datatype.identifier.transaction.PostingId;
import io.mojaloop.core.transaction.contract.data.definition.FundTransferDefinitionData;
import io.mojaloop.core.transaction.contract.exception.DefinitionDescriptionTooLongException;
import io.mojaloop.core.transaction.contract.exception.DefinitionNameTooLongException;
import io.mojaloop.core.transaction.contract.exception.PostingAlreadyExistsException;
import io.mojaloop.fspiop.spec.core.Currency;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
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

import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(name = "txn_fund_transfer_definition", uniqueConstraints = {@UniqueConstraint(name = "trn_fund_transfer_definition_currency_UK", columnNames = {"currency"}),
                                                                   @UniqueConstraint(name = "trn_fund_transfer_definition_name_UK", columnNames = {"name"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FundTransferDefinition extends JpaEntity<DefinitionId> implements DataConversion<FundTransferDefinitionData> {

    @Id
    @JavaType(DefinitionIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "definition_id", nullable = false, updatable = false)
    protected DefinitionId id;

    @Column(name = "currency", nullable = false, length = StringSizeConstraints.MAX_CURRENCY_LENGTH)
    @Enumerated(EnumType.STRING)
    protected Currency currency;

    @Column(name = "name", nullable = false, length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String name;

    @Column(name = "description", length = StringSizeConstraints.MAX_DESCRIPTION_LENGTH)
    protected String description;

    @Column(name = "activation_status", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected ActivationStatus activationStatus = ActivationStatus.ACTIVE;

    @Column(name = "termination_status", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected TerminationStatus terminationStatus = TerminationStatus.ALIVE;

    @OneToMany(mappedBy = "definition", orphanRemoval = true, cascade = {jakarta.persistence.CascadeType.ALL}, fetch = FetchType.EAGER)
    protected List<Posting> postings = new ArrayList<>();

    public FundTransferDefinition(Currency currency, String name, String description) {

        assert currency != null;
        assert name != null;

        this.id = new DefinitionId(Snowflake.get().nextId());
        this.name(name).currency(currency).description(description);
    }

    public FundTransferDefinition.Posting addPosting(PostingOwnerType forOwner, PostingAmountType forAmount, Side side, ChartEntryId chartEntryId, String description)
        throws PostingAlreadyExistsException {

        var posting = new FundTransferDefinition.Posting(this, side, forOwner, forAmount, chartEntryId, description);

        this.postings.add(posting);

        return posting;
    }

    @Override
    public FundTransferDefinitionData convert() {

        var postingData = this.postings.stream()
                                       .map(p -> new FundTransferDefinitionData.Posting(p.getId(),
                                                                                        p.getForOwner(),
                                                                                        p.getForAmount(),
                                                                                        p.getSide(),
                                                                                        p.getChartEntryId(),
                                                                                        p.getDescription()))
                                       .toList();

        return new FundTransferDefinitionData(this.getId(),
                                              this.getCurrency(),
                                              this.getName(),
                                              this.getDescription(),
                                              this.getActivationStatus(),
                                              this.getTerminationStatus(),
                                              postingData);
    }

    public FundTransferDefinition currency(Currency currency) {

        assert currency != null;

        this.currency = currency;

        return this;
    }

    public FundTransferDefinition description(String description) {

        if (description == null) {
            return this;
        }

        var value = description.trim();

        if (value.length() > StringSizeConstraints.MAX_DESCRIPTION_LENGTH) {
            throw new DefinitionDescriptionTooLongException();
        }

        this.description = description;

        return this;
    }

    public FundTransferDefinition name(String name) {

        if (name == null) {
            return this;
        }

        var value = name.trim();

        if (value.length() > StringSizeConstraints.MAX_NAME_TITLE_LENGTH) {
            throw new DefinitionNameTooLongException();
        }

        this.name = name;

        return this;
    }

    public void activate() {

        this.activationStatus = ActivationStatus.ACTIVE;
    }

    public void deactivate() {

        this.activationStatus = ActivationStatus.INACTIVE;
    }

    public void terminate() {

        this.activationStatus = ActivationStatus.INACTIVE;
        this.terminationStatus = TerminationStatus.TERMINATED;
    }

    public void removePosting(PostingId postingId) throws io.mojaloop.core.transaction.contract.exception.fundtransfer.FundTransferDefinitionPostingNotFoundException {

        assert postingId != null;

        if (this.postings.stream().noneMatch(p -> p.getId().equals(postingId))) {
            throw new io.mojaloop.core.transaction.contract.exception.fundtransfer.FundTransferDefinitionPostingNotFoundException(postingId);
        }

        this.postings.removeIf(p -> p.getId().equals(postingId));
    }

    @Getter
    @Entity
    @Table(name = "txn_fund_transfer_posting")
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Posting extends JpaEntity<PostingId> {

        @Id
        @JavaType(PostingIdJavaType.class)
        @JdbcTypeCode(BIGINT)
        protected PostingId id;

        @Column(name = "for_owner", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
        @Enumerated(EnumType.STRING)
        protected PostingOwnerType forOwner;

        @Column(name = "for_amount", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
        @Enumerated(EnumType.STRING)
        protected PostingAmountType forAmount;

        @Column(name = "side", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
        @Enumerated(EnumType.STRING)
        protected Side side;

        @Basic
        @JavaType(ChartEntryIdJavaType.class)
        @JdbcTypeCode(BIGINT)
        @Column(name = "chart_entry_id", nullable = false, updatable = false)
        protected ChartEntryId chartEntryId;

        @Column(name = "description", length = StringSizeConstraints.MAX_DESCRIPTION_LENGTH)
        protected String description;

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "definition_id", nullable = false)
        protected FundTransferDefinition definition;

        public Posting(FundTransferDefinition definition, Side side, PostingOwnerType forOwner, PostingAmountType forAmount, ChartEntryId chartEntryId, String description)
            throws PostingAlreadyExistsException {

            assert definition != null;
            assert side != null;
            assert forOwner != null;
            assert forAmount != null;
            assert chartEntryId != null;

            this.id = new PostingId(Snowflake.get().nextId());
            this.definition = definition;
            this.side(side).forPosting(forOwner, forAmount, chartEntryId).description(description);
        }

        public Posting description(String description) {

            if (description == null) {
                return this;
            }

            var value = description.trim();

            if (value.length() > StringSizeConstraints.MAX_DESCRIPTION_LENGTH) {
                throw new DefinitionDescriptionTooLongException();
            }

            this.description = description;

            return this;
        }

        public Posting forPosting(PostingOwnerType forOwner, PostingAmountType forAmount, ChartEntryId chartEntryId) throws PostingAlreadyExistsException {

            assert forOwner != null;
            assert forAmount != null;
            assert chartEntryId != null;

            try {

                this.definition.postings.stream()
                                        .filter(p -> p.getForOwner().equals(forOwner) && p.getForAmount().equals(forAmount) && p.getChartEntryId().equals(chartEntryId))
                                        .findFirst()
                                        .ifPresent(p -> {
                                            try {
                                                throw new PostingAlreadyExistsException();
                                            } catch (PostingAlreadyExistsException e) {
                                                throw new RuntimeException(e);
                                            }
                                        });
            } catch (RuntimeException e) {

                if (e.getCause() instanceof PostingAlreadyExistsException e1) {
                    throw e1;
                }
            }

            this.forOwner = forOwner;
            this.forAmount = forAmount;
            this.chartEntryId = chartEntryId;

            return this;
        }

        public Posting side(Side side) {

            assert side != null;

            this.side = side;

            return this;
        }

    }

}
