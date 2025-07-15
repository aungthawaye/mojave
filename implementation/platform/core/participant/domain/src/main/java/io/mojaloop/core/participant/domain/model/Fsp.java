/*-
 * ================================================================================
 * Mojaloop OSS
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
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
 * ================================================================================
 */

package io.mojaloop.core.participant.domain.model;

import io.mojaloop.common.component.constraint.StringSizeConstraints;
import io.mojaloop.common.component.exception.input.BlankOrEmptyInputException;
import io.mojaloop.common.component.exception.input.TextTooLargeException;
import io.mojaloop.common.component.handy.Snowflake;
import io.mojaloop.common.component.persistence.JpaEntity;
import io.mojaloop.common.component.persistence.JpaInstantConverter;
import io.mojaloop.common.datatype.enumeration.ActivationStatus;
import io.mojaloop.common.datatype.enumeration.TerminationStatus;
import io.mojaloop.common.datatype.identifier.participant.FspId;
import io.mojaloop.common.datatype.type.fspiop.FspCode;
import io.mojaloop.common.fspiop.model.core.Currency;
import io.mojaloop.core.participant.contract.exception.CannotActivateSupportedCurrencyException;
import io.mojaloop.core.participant.contract.exception.CurrencyAlreadySupportedException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;

@Getter
@Entity
@Table(name = "pcp_fsp")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Fsp extends JpaEntity<FspId> {

    @EmbeddedId
    protected FspId id;

    @Embedded
    protected FspCode fspCode;

    @Column(name = "name")
    protected String name;

    @Getter(AccessLevel.NONE)
    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true, mappedBy = "fsp", targetEntity = SupportedCurrency.class,
               fetch = FetchType.EAGER)
    protected Set<SupportedCurrency> supportedCurrencies = Set.of();

    @Getter(AccessLevel.NONE)
    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true, mappedBy = "fsp", targetEntity = Endpoint.class, fetch = FetchType.EAGER)
    protected Set<Endpoint> endpoints = Set.of();

    @Column(name = "activation_status")
    @Enumerated(EnumType.STRING)
    protected ActivationStatus activationStatus;

    @Column(name = "termination_status")
    @Enumerated(EnumType.STRING)
    protected TerminationStatus terminationStatus;

    @Column(name = "created_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant createdAt;

    public Fsp(FspCode fspCode, String name) {

        assert fspCode != null;
        assert name != null;

        this.id = new FspId(Snowflake.get().nextId());
        this.fspCode(fspCode).name(name);
        this.activationStatus = ActivationStatus.ACTIVE;
        this.createdAt = Instant.now();
    }

    public void activate() {

        this.activationStatus = ActivationStatus.ACTIVE;

        this.supportedCurrencies.forEach(sc -> {
            try {
                sc.activate();
            } catch (CannotActivateSupportedCurrencyException ignored) { }
        });
    }

    public SupportedCurrency addSupportedCurrency(Currency currency) throws CurrencyAlreadySupportedException {

        if (this.hasSupportedCurrency(currency)) {
            throw new CurrencyAlreadySupportedException(currency.name());
        }

        var supportedCurrency = new SupportedCurrency(this, currency);

        this.supportedCurrencies.add(supportedCurrency);

        return supportedCurrency;
    }

    public void deactivate() {

        this.activationStatus = ActivationStatus.INACTIVE;
        this.supportedCurrencies.forEach(SupportedCurrency::deactivate);
    }

    public Fsp fspCode(FspCode fspCode) {

        assert fspCode != null;

        this.fspCode = fspCode;

        return this;
    }

    public Set<Endpoint> getEndpoints() {

        return Collections.unmodifiableSet(this.endpoints);
    }

    @Override
    public FspId getId() {

        return this.id;
    }

    public Set<SupportedCurrency> getSupportedCurrencies() {

        return Collections.unmodifiableSet(this.supportedCurrencies);
    }

    public boolean hasSupportedCurrency(Currency currency) {

        return this.supportedCurrencies.stream().anyMatch(sc -> sc.currency == currency);
    }

    public boolean isActive() {

        return this.activationStatus == ActivationStatus.ACTIVE;
    }

    public Fsp name(String name) {

        assert name != null;

        var value = name.trim();

        if (value.isEmpty()) {
            throw new BlankOrEmptyInputException("FSP Name");
        }

        if (value.length() > StringSizeConstraints.SHORT_STRING) {
            throw new TextTooLargeException("FSP Name", StringSizeConstraints.SHORT_STRING);
        }

        this.name = value;

        return this;
    }

}
