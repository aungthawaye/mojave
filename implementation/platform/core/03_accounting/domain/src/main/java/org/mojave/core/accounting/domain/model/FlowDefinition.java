/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===
 */
package org.mojave.core.accounting.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
import org.mojave.component.jpa.JpaEntity;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.component.misc.data.DataConversion;
import org.mojave.component.misc.handy.Snowflake;
import org.mojave.core.accounting.contract.data.FlowDefinitionData;
import org.mojave.core.accounting.contract.exception.definition.DefinitionDescriptionTooLongException;
import org.mojave.core.accounting.contract.exception.definition.DefinitionNameTooLongException;
import org.mojave.core.accounting.contract.exception.definition.PostingDefinitionNotFoundException;
import org.mojave.core.accounting.domain.cache.AccountCache;
import org.mojave.core.accounting.domain.cache.ChartEntryCache;
import org.mojave.core.accounting.domain.cache.updater.FlowDefinitionCacheUpdater;
import org.mojave.core.common.datatype.converter.identifier.accounting.FlowDefinitionIdJavaType;
import org.mojave.core.common.datatype.enums.ActivationStatus;
import org.mojave.core.common.datatype.enums.TerminationStatus;
import org.mojave.core.common.datatype.enums.accounting.ReceiveIn;
import org.mojave.core.common.datatype.enums.accounting.Side;
import org.mojave.core.common.datatype.enums.trasaction.TransactionType;
import org.mojave.core.common.datatype.identifier.accounting.FlowDefinitionId;
import org.mojave.core.common.datatype.identifier.accounting.PostingDefinitionId;
import org.mojave.fspiop.spec.core.Currency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@EntityListeners(value = {FlowDefinitionCacheUpdater.class})
@Table(
    name = "acc_flow_definition",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "acc_flow_definition_01_UK",
            columnNames = {
                "transaction_type",
                "currency"}),
        @UniqueConstraint(
            name = "acc_flow_definition_02_UK",
            columnNames = {"name"})})

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FlowDefinition extends JpaEntity<FlowDefinitionId>
    implements DataConversion<FlowDefinitionData> {

    @Id
    @JavaType(FlowDefinitionIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "flow_definition_id",
        nullable = false,
        updatable = false)
    protected FlowDefinitionId id;

    @Column(
        name = "transaction_type",
        nullable = false,
        length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected TransactionType transactionType;

    @Column(
        name = "currency",
        nullable = false,
        length = StringSizeConstraints.MAX_CURRENCY_LENGTH)
    @Enumerated(EnumType.STRING)
    protected Currency currency;

    @Column(
        name = "name",
        nullable = false,
        length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String name;

    @Column(
        name = "description",
        length = StringSizeConstraints.MAX_DESCRIPTION_LENGTH)
    protected String description;

    @Column(
        name = "activation_status",
        nullable = false,
        length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected ActivationStatus activationStatus = ActivationStatus.ACTIVE;

    @Column(
        name = "termination_status",
        nullable = false,
        length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected TerminationStatus terminationStatus = TerminationStatus.ALIVE;

    @OneToMany(
        mappedBy = "definition",
        orphanRemoval = true,
        cascade = {jakarta.persistence.CascadeType.ALL},
        fetch = FetchType.EAGER)
    protected List<PostingDefinition> postings = new ArrayList<>();

    public FlowDefinition(TransactionType transactionType,
                          Currency currency,
                          String name,
                          String description) {

        assert transactionType != null;
        assert currency != null;
        assert name != null;

        this.id = new FlowDefinitionId(Snowflake.get().nextId());
        this.transactionType = transactionType;
        this.name(name).currency(currency).description(description);
    }

    public void activate() {

        this.activationStatus = ActivationStatus.ACTIVE;
    }

    public PostingDefinition addPosting(Integer index,
                                        ReceiveIn receiveIn,
                                        Long receiveInId,
                                        String participant,
                                        String amountName,
                                        Side side,
                                        String description,
                                        AccountCache accountCache,
                                        ChartEntryCache chartEntryCache) {

        var posting = new PostingDefinition(
            this, index, receiveIn, receiveInId, participant, amountName, side, description,
            accountCache, chartEntryCache);

        this.postings.add(posting);

        return posting;

    }

    @Override
    public FlowDefinitionData convert() {

        var postingData = this.postings
                              .stream()
                              .map(p -> new FlowDefinitionData.PostingDefinitionData(
                                  p.id, p.step, p.receiveIn, p.receiveInId, p.participant,
                                  p.amountName, p.side, p.description))
                              .toList();

        return new FlowDefinitionData(
            this.getId(), this.getTransactionType(), this.getCurrency(), this.getName(),
            this.getDescription(), this.getActivationStatus(), this.getTerminationStatus(),
            postingData);
    }

    public FlowDefinition currency(Currency currency) {

        assert currency != null;

        this.currency = currency;

        return this;
    }

    public void deactivate() {

        this.activationStatus = ActivationStatus.INACTIVE;
    }

    public FlowDefinition description(String description) {

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

    @Override
    public FlowDefinitionId getId() {

        return this.id;
    }

    public List<PostingDefinition> getPostings() {

        return Collections.unmodifiableList(this.postings);
    }

    public FlowDefinition name(String name) {

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

    public void removePosting(PostingDefinitionId postingDefinitionId) {

        assert postingDefinitionId != null;

        if (this.postings.stream().noneMatch(p -> p.getId().equals(postingDefinitionId))) {

            throw new PostingDefinitionNotFoundException(postingDefinitionId);
        }

        this.postings.removeIf(p -> p.getId().equals(postingDefinitionId));

    }

    public void terminate() {

        this.activationStatus = ActivationStatus.INACTIVE;
        this.terminationStatus = TerminationStatus.TERMINATED;
    }

}
