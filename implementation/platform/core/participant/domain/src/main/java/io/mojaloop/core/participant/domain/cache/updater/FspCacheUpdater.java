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

package io.mojaloop.core.participant.domain.cache.updater;

import io.mojaloop.common.component.spring.SpringContext;
import io.mojaloop.core.participant.contract.cache.ParticipantCache;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.domain.model.Endpoint;
import io.mojaloop.core.participant.domain.model.Fsp;
import io.mojaloop.core.participant.domain.model.SupportedCurrency;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

import java.util.function.Function;
import java.util.stream.Collectors;

public class FspCacheUpdater {

    @PostPersist
    @PostUpdate
    public void persistOrUpdate(Fsp fsp) {

        var participantCache = SpringContext.getBean(ParticipantCache.class, ParticipantCache.Qualifiers.DEFAULT);

        Function<SupportedCurrency, FspData.SupportedCurrencyData> toSupportedCurrencyData = o -> new FspData.SupportedCurrencyData(o.getId(),
                                                                                                                                    o.getCurrency(),
                                                                                                                                    o.getActivationStatus(),
                                                                                                                                    o.getTerminationStatus());

        Function<Endpoint, FspData.EndpointData> toEndpointData = o -> new FspData.EndpointData(o.getId(), o.getType(), o.getHost());

        participantCache.save(new FspData(fsp.getId(),
                                          fsp.getFspCode(),
                                          fsp.getName(),
                                          fsp
                                              .getSupportedCurrencies()
                                              .stream()
                                              .map(toSupportedCurrencyData)
                                              .toArray(FspData.SupportedCurrencyData[]::new),
                                          fsp
                                              .getEndpoints()
                                              .stream()
                                              .map(toEndpointData)
                                              .collect(Collectors.toMap(FspData.EndpointData::type,
                                                                        Function.identity(),
                                                                        (existing, replacement) -> replacement)),
                                          fsp.getActivationStatus(),
                                          fsp.getTerminationStatus()));
    }

    @PostRemove
    public void postRemove(Fsp fsp) {

        var participantCache = SpringContext.getBean(ParticipantCache.class, ParticipantCache.Qualifiers.DEFAULT);

        participantCache.delete(fsp.getId());
    }

}
