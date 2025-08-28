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

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.component.jpa.JpaInstantConverter;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.data.DataConversion;
import io.mojaloop.component.misc.exception.input.BlankOrEmptyInputException;
import io.mojaloop.component.misc.exception.input.TextTooLargeException;
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.common.datatype.enumeration.ActivationStatus;
import io.mojaloop.core.common.datatype.enumeration.TerminationStatus;
import io.mojaloop.core.common.datatype.enumeration.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.common.datatype.type.fspiop.FspCode;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.contract.exception.CannotActivateEndpointException;
import io.mojaloop.core.participant.contract.exception.CannotActivateSupportedCurrencyException;
import io.mojaloop.core.participant.contract.exception.CurrencyAlreadySupportedException;
import io.mojaloop.core.participant.contract.exception.EndpointAlreadyConfiguredException;
import io.mojaloop.core.participant.domain.cache.updater.FspCacheUpdater;
import io.mojaloop.fspiop.spec.core.Currency;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Entity
@EntityListeners(value = {FspCacheUpdater.class})
@Table(name = "pcp_fsp")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Fsp extends JpaEntity<FspId> implements DataConversion<FspData> {

    @EmbeddedId
    protected FspId id;

    @Embedded
    protected FspCode fspCode;

    @Column(name = "name", length = StringSizeConstraints.MAX_FSP_NAME_LENGTH)
    protected String name;

    @Getter(AccessLevel.NONE)
    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true, mappedBy = "fsp", targetEntity = SupportedCurrency.class,
               fetch = FetchType.EAGER)
    protected Set<SupportedCurrency> supportedCurrencies = new HashSet<>();

    @Getter(AccessLevel.NONE)
    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true, mappedBy = "fsp", targetEntity = Endpoint.class, fetch = FetchType.EAGER)
    protected Set<Endpoint> endpoints = new HashSet<>();

    @Column(name = "activation_status", length = StringSizeConstraints.MAX_COMMON_ENUM_LEN)
    @Enumerated(EnumType.STRING)
    protected ActivationStatus activationStatus = ActivationStatus.ACTIVE;

    @Column(name = "termination_status", length = StringSizeConstraints.MAX_COMMON_ENUM_LEN)
    @Enumerated(EnumType.STRING)
    protected TerminationStatus terminationStatus = TerminationStatus.ALIVE;

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

        this.endpoints.forEach(e -> {
            try {
                e.activate();
            } catch (CannotActivateEndpointException ignored) { }
        });
    }

    public boolean activateEndpoint(EndpointType type) throws CannotActivateEndpointException {

        assert type != null;

        var optEndpoint = this.endpoints.stream()
                                        .filter(endpoint -> endpoint.getType() == type)
                                        .findFirst();

        if (optEndpoint.isEmpty()) {
            return false;
        }

        optEndpoint.get().activate();

        return true;
    }

    public boolean activateSupportedCurrency(Currency currency) throws CannotActivateSupportedCurrencyException {

        assert currency != null;

        var optSupportedCurrency = this.supportedCurrencies.stream()
                                                           .filter(sc -> sc.getCurrency() == currency)
                                                           .findFirst();

        if (optSupportedCurrency.isEmpty()) {
            return false;
        }

        optSupportedCurrency.get().activate();

        return true;
    }

    public Endpoint addEndpoint(EndpointType type, String host) throws EndpointAlreadyConfiguredException {

        assert type != null;
        assert host != null;

        if (this.hasEndpoint(type)) {

            throw new EndpointAlreadyConfiguredException(type);
        }

        var endpoint = new Endpoint(this, type, host);

        this.endpoints.add(endpoint);

        return endpoint;
    }

    public SupportedCurrency addSupportedCurrency(Currency currency) throws CurrencyAlreadySupportedException {

        assert currency != null;

        if (this.hasSupportedCurrency(currency)) {
            throw new CurrencyAlreadySupportedException(currency);
        }

        var supportedCurrency = new SupportedCurrency(this, currency);

        this.supportedCurrencies.add(supportedCurrency);

        return supportedCurrency;
    }

    public boolean changeEndpoint(EndpointType type, String baseUrl) {

        assert type != null;
        assert baseUrl != null;

        var optEndpoint = this.endpoints.stream()
                                        .filter(endpoint -> endpoint.getType() == type)
                                        .findFirst();

        if (optEndpoint.isEmpty()) {
            return false;
        }

        var endpoint = optEndpoint.get();

        endpoint.baseUrl(baseUrl);

        return true;
    }

    @Override
    public FspData convert() {

        return new FspData(this.getId(),
                           this.getFspCode(),
                           this.getName(),
                           this
                               .getSupportedCurrencies()
                               .stream()
                               .map(SupportedCurrency::convert)
                               .toArray(FspData.SupportedCurrencyData[]::new),
                           this
                               .getEndpoints()
                               .stream()
                               .map(Endpoint::convert)
                               .collect(Collectors.toMap(FspData.EndpointData::type,
                                                         Function.identity(),
                                                         (existing, replacement) -> replacement)),
                           this.activationStatus,
                           this.terminationStatus);
    }

    public void deactivate() {

        this.activationStatus = ActivationStatus.INACTIVE;
        this.supportedCurrencies.forEach(SupportedCurrency::deactivate);
        this.endpoints.forEach(Endpoint::deactivate);
    }

    public boolean deactivateEndpoint(EndpointType type) {

        assert type != null;

        var optEndpoint = this.endpoints.stream()
                                        .filter(endpoint -> endpoint.getType() == type)
                                        .findFirst();

        if (optEndpoint.isEmpty()) {
            return false;
        }

        optEndpoint.get().deactivate();

        return true;
    }

    public boolean deactivateSupportedCurrency(Currency currency) {

        assert currency != null;

        var optSupportedCurrency = this.supportedCurrencies.stream()
                                                           .filter(sc -> sc.getCurrency() == currency)
                                                           .findFirst();

        if (optSupportedCurrency.isEmpty()) {
            return false;
        }

        optSupportedCurrency.get().deactivate();

        return true;
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

    public boolean hasEndpoint(EndpointType type) {

        assert type != null;

        return this.endpoints.stream().anyMatch(endpoint -> endpoint.getType() == type);
    }

    public boolean hasSupportedCurrency(Currency currency) {

        assert currency != null;

        return this.supportedCurrencies.stream().anyMatch(sc -> sc.getCurrency() == currency);
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

        if (value.length() > StringSizeConstraints.MAX_FSP_NAME_LENGTH) {
            throw new TextTooLargeException("FSP Name", StringSizeConstraints.MAX_FSP_NAME_LENGTH);
        }

        this.name = value;

        return this;
    }

}
