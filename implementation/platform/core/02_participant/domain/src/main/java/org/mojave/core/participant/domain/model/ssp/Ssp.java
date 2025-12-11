/*-
 * ================================================================================
 * Mojave
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

package org.mojave.core.participant.domain.model.ssp;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
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
import org.mojave.component.jpa.JpaEntity;
import org.mojave.component.jpa.JpaInstantConverter;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.component.misc.data.DataConversion;
import org.mojave.component.misc.handy.Snowflake;
import org.mojave.core.common.datatype.converter.identifier.participant.SspIdJavaType;
import org.mojave.core.common.datatype.converter.type.fspiop.SspCodeConverter;
import org.mojave.core.common.datatype.enums.ActivationStatus;
import org.mojave.core.common.datatype.enums.TerminationStatus;
import org.mojave.core.common.datatype.identifier.participant.SspId;
import org.mojave.core.common.datatype.type.participant.SspCode;
import org.mojave.core.participant.contract.data.SspCurrencyData;
import org.mojave.core.participant.contract.data.SspData;
import org.mojave.core.participant.contract.exception.ssp.SspCodeRequiredException;
import org.mojave.core.participant.contract.exception.ssp.SspEndpointBaseUrlRequiredException;
import org.mojave.core.participant.contract.exception.ssp.SspEndpointBaseUrlTooLongException;
import org.mojave.core.participant.contract.exception.ssp.SspNameRequiredException;
import org.mojave.core.participant.contract.exception.ssp.SspNameTooLongException;
import org.mojave.core.participant.domain.model.hub.Hub;
import org.mojave.fspiop.spec.core.Currency;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(
    name = "pcp_ssp",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "pcp_ssp_code_UK",
            columnNames = {"code"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ssp extends JpaEntity<SspId> implements DataConversion<SspData> {

    @Id
    @JavaType(SspIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "ssp_id",
        nullable = false,
        updatable = false)
    protected SspId id;

    @Basic
    @Column(
        name = "code",
        nullable = false,
        length = StringSizeConstraints.MAX_CODE_LENGTH)
    @Convert(converter = SspCodeConverter.class)
    protected SspCode code;

    @Column(
        name = "name",
        nullable = false,
        length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String name;

    @Column(
        name = "base_url",
        nullable = false,
        length = StringSizeConstraints.MAX_HTTP_URL_LENGTH)
    protected String baseUrl;

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

    @Column(
        name = "created_at",
        nullable = false)
    @Convert(converter = JpaInstantConverter.class)
    protected Instant createdAt;

    @Getter(AccessLevel.NONE)
    @OneToMany(
        mappedBy = "ssp",
        cascade = {CascadeType.ALL},
        orphanRemoval = true,
        targetEntity = SspCurrency.class,
        fetch = FetchType.EAGER)
    protected Set<SspCurrency> currencies = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
        name = "hub_id",
        nullable = false,
        updatable = false,
        foreignKey = @ForeignKey(name = "fsp_hub_FK"))
    protected Hub hub;

    public Ssp(final Hub hub, final SspCode code, final String name, final String baseUrl) {

        assert hub != null;
        assert code != null;
        assert name != null;
        assert baseUrl != null;

        this.id = new SspId(Snowflake.get().nextId());
        this.hub = hub;
        this.sspCode(code).name(name).endpoint(baseUrl);
        this.activationStatus = ActivationStatus.ACTIVE;
        this.createdAt = Instant.now();
    }

    public void activate() {

        this.activationStatus = ActivationStatus.ACTIVE;
        this.currencies.forEach(SspCurrency::activate);
    }

    public Optional<SspCurrency> activate(final Currency currency) {

        final var sspCurrency = this.currencies
                                    .stream()
                                    .filter(f -> f.getCurrency().equals(currency))
                                    .findFirst();

        if (sspCurrency.isPresent()) {

            sspCurrency.get().activate();

            return sspCurrency;
        }

        return Optional.empty();
    }

    public SspCurrency addCurrency(final Currency currency) {

        assert currency != null;

        final var supportedCurrency = new SspCurrency(this, currency);

        this.currencies.add(supportedCurrency);

        return supportedCurrency;
    }

    @Override
    public SspData convert() {

        return new SspData(
            this.getId(), this.getCode(), this.getName(), this.baseUrl,
            this.currencies.stream().map(SspCurrency::convert).toArray(SspCurrencyData[]::new),
            this.activationStatus, this.terminationStatus);
    }

    public void deactivate() {

        this.activationStatus = ActivationStatus.INACTIVE;
        this.currencies.forEach(SspCurrency::deactivate);
    }

    public Optional<SspCurrency> deactivate(final Currency currency) {

        assert currency != null;

        final var opt = this.currencies
                            .stream()
                            .filter(c -> c.getCurrency() == currency)
                            .findFirst();

        if (opt.isEmpty()) {
            return opt;
        }

        opt.get().deactivate();
        return opt;
    }

    public Ssp endpoint(final String baseUrl) {

        if (baseUrl == null || baseUrl.isBlank()) {

            throw new SspEndpointBaseUrlRequiredException();
        }

        var value = baseUrl.trim();

        if (value.length() > StringSizeConstraints.MAX_HTTP_URL_LENGTH) {
            throw new SspEndpointBaseUrlTooLongException();
        }

        this.baseUrl = value;

        return this;
    }

    public Set<SspCurrency> getCurrencies() {

        return Collections.unmodifiableSet(this.currencies);
    }

    public Optional<SspCurrency> getCurrency(final Currency currency) {

        return this.currencies.stream().filter(c -> c.getCurrency().equals(currency)).findFirst();
    }

    public boolean isActive() {

        return this.activationStatus == ActivationStatus.ACTIVE;
    }

    public boolean isCurrencySupported(final Currency currency) {

        assert currency != null;

        return this.currencies
                   .stream()
                   .anyMatch(sc -> sc.getCurrency() == currency && sc.isActive());
    }

    public Ssp name(final String name) {

        if (name == null) {

            throw new SspNameRequiredException();
        }

        if (name.length() > StringSizeConstraints.MAX_NAME_TITLE_LENGTH) {

            throw new SspNameTooLongException();
        }

        this.name = name;

        return this;
    }

    public Ssp sspCode(final SspCode sspCode) {

        if (sspCode == null) {

            throw new SspCodeRequiredException();
        }

        this.code = sspCode;

        return this;
    }

    public void terminate() {

        this.terminationStatus = TerminationStatus.TERMINATED;
    }

}
