package io.mojaloop.core.transaction.domain.model.definition;

import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.core.common.datatype.converter.identifier.transaction.DefinitionIdJavaType;
import io.mojaloop.core.common.datatype.enums.ActivationStatus;
import io.mojaloop.core.common.datatype.enums.TerminationStatus;
import io.mojaloop.core.common.datatype.identifier.transaction.DefinitionId;
import io.mojaloop.fspiop.spec.core.Currency;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
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
@Table(name = "txn_fund_transfer_definition", uniqueConstraints = {@UniqueConstraint(name = "trn_fund_out_definition_currency_UK", columnNames = {"currency"}),
                                                                   @UniqueConstraint(name = "trn_fund_out_definition_name_UK", columnNames = {"name"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FundTransferDefinition {

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

    @OneToMany(mappedBy = "definition", orphanRemoval = true, cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    protected List<FundOutDefinition.Posting> postings = new ArrayList<>();

}
