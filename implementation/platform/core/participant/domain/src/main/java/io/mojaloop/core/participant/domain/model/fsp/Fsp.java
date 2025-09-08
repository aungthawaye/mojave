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

package io.mojaloop.core.participant.domain.model.fsp;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.component.jpa.JpaInstantConverter;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.data.DataConversion;
import io.mojaloop.component.misc.exception.input.BlankOrEmptyInputException;
import io.mojaloop.component.misc.exception.input.TextTooLargeException;
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.common.datatype.converter.identifier.participant.FspIdJavaType;
import io.mojaloop.core.common.datatype.converter.type.fspiop.FspCodeConverter;
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
import io.mojaloop.core.participant.domain.cache.redis.updater.FspCacheUpdater;
import io.mojaloop.fspiop.spec.core.Currency;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@EntityListeners(value = {FspCacheUpdater.class})
@Table(name = "pcp_fsp", uniqueConstraints = {
    @UniqueConstraint(name = "uk_fsp_code", columnNames = {"fsp_code"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Fsp extends JpaEntity<FspId> implements DataConversion<FspData> {

    @Id
    @JavaType(FspIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "fsp_id", nullable = false, updatable = false)
    protected FspId id;

    @Basic
    @Column(name = "fsp_code", nullable = false)
    @Convert(converter = FspCodeConverter.class)
    protected FspCode fspCode;

    @Column(name = "name", nullable = false, length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String name;

    @Getter(AccessLevel.NONE)
    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true, mappedBy = "fsp", targetEntity = FspCurrency.class, fetch = FetchType.EAGER)
    protected Set<FspCurrency> currencies = new HashSet<>();

    @Getter(AccessLevel.NONE)
    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true, mappedBy = "fsp", targetEntity = FspEndpoint.class, fetch = FetchType.EAGER)
    protected Set<FspEndpoint> endpoints = new HashSet<>();

    @Column(name = "activation_status", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected ActivationStatus activationStatus = ActivationStatus.ACTIVE;

    @Column(name = "termination_status", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected TerminationStatus terminationStatus = TerminationStatus.ALIVE;

    @Column(name = "created_at", nullable = false)
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

        this.currencies.forEach(sc -> {
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

    public boolean activateCurrency(Currency currency) throws CannotActivateSupportedCurrencyException {

        assert currency != null;

        var optSupportedCurrency = this.currencies.stream().filter(sc -> sc.getCurrency() == currency).findFirst();

        if (optSupportedCurrency.isEmpty()) {
            return false;
        }

        optSupportedCurrency.get().activate();

        return true;
    }

    public boolean activateEndpoint(EndpointType type) throws CannotActivateEndpointException {

        assert type != null;

        var optEndpoint = this.endpoints.stream().filter(fspEndpoint -> fspEndpoint.getType() == type).findFirst();

        if (optEndpoint.isEmpty()) {
            return false;
        }

        optEndpoint.get().activate();

        return true;
    }

    public FspCurrency addCurrency(Currency currency) throws CurrencyAlreadySupportedException {

        assert currency != null;

        if (this.isCurrencySupported(currency)) {
            throw new CurrencyAlreadySupportedException(currency);
        }

        var supportedCurrency = new FspCurrency(this, currency);

        this.currencies.add(supportedCurrency);

        return supportedCurrency;
    }

    public FspEndpoint addEndpoint(EndpointType type, String host) throws EndpointAlreadyConfiguredException {

        assert type != null;
        assert host != null;

        if (this.hasEndpoint(type)) {

            throw new EndpointAlreadyConfiguredException(type);
        }

        var endpoint = new FspEndpoint(this, type, host);

        this.endpoints.add(endpoint);

        return endpoint;
    }

    public boolean changeEndpoint(EndpointType type, String baseUrl) {

        assert type != null;
        assert baseUrl != null;

        var optEndpoint = this.endpoints.stream().filter(fspEndpoint -> fspEndpoint.getType() == type).findFirst();

        if (optEndpoint.isEmpty()) {
            return false;
        }

        var endpoint = optEndpoint.get();

        endpoint.baseUrl(baseUrl);

        return true;
    }

    @Override
    public FspData convert() {

        return new FspData(this.getId(), this.getFspCode(), this.getName(),
                           this.getCurrencies().stream().map(FspCurrency::convert).toArray(FspData.FspCurrencyData[]::new),
                           this.getEndpoints().stream().map(FspEndpoint::convert)
                               .collect(Collectors.toMap(FspData.EndpointData::type, Function.identity(), (existing, replacement) -> replacement)),
                           this.activationStatus, this.terminationStatus);
    }

    public void deactivate() {

        this.activationStatus = ActivationStatus.INACTIVE;
        this.currencies.forEach(FspCurrency::deactivate);
        this.endpoints.forEach(FspEndpoint::deactivate);
    }

    public boolean deactivateCurrency(Currency currency) {

        assert currency != null;

        var optSupportedCurrency = this.currencies.stream().filter(sc -> sc.getCurrency() == currency).findFirst();

        if (optSupportedCurrency.isEmpty()) {
            return false;
        }

        optSupportedCurrency.get().deactivate();

        return true;
    }

    public boolean deactivateEndpoint(EndpointType type) {

        assert type != null;

        var optEndpoint = this.endpoints.stream().filter(fspEndpoint -> fspEndpoint.getType() == type).findFirst();

        if (optEndpoint.isEmpty()) {
            return false;
        }

        optEndpoint.get().deactivate();

        return true;
    }

    public Fsp fspCode(FspCode fspCode) {

        assert fspCode != null;

        this.fspCode = fspCode;

        return this;
    }

    public Set<FspCurrency> getCurrencies() {

        return Collections.unmodifiableSet(this.currencies);
    }

    public Set<FspEndpoint> getEndpoints() {

        return Collections.unmodifiableSet(this.endpoints);
    }

    @Override
    public FspId getId() {

        return this.id;
    }

    public boolean hasEndpoint(EndpointType type) {

        assert type != null;

        return this.endpoints.stream().anyMatch(fspEndpoint -> fspEndpoint.getType() == type);
    }

    public boolean isActive() {

        return this.activationStatus == ActivationStatus.ACTIVE;
    }

    public boolean isCurrencySupported(Currency currency) {

        assert currency != null;

        return this.currencies.stream().anyMatch(sc -> sc.getCurrency() == currency);
    }

    public Fsp name(String name) {

        assert name != null;

        var value = name.trim();

        if (value.isEmpty()) {
            throw new BlankOrEmptyInputException("FSP Name");
        }

        if (value.length() > StringSizeConstraints.MAX_NAME_TITLE_LENGTH) {
            throw new TextTooLargeException("FSP Name", StringSizeConstraints.MAX_NAME_TITLE_LENGTH);
        }

        this.name = value;

        return this;
    }

}
