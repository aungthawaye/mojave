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

package io.mojaloop.core.participant.domain;

import io.mojaloop.component.jpa.routing.RoutingJpaConfiguration;
import io.mojaloop.component.misc.MiscConfiguration;
import io.mojaloop.core.participant.domain.cache.ParticipantCache;
import io.mojaloop.core.participant.domain.cache.local.ParticipantLocalCache;
import io.mojaloop.core.participant.domain.repository.FspRepository;
import io.mojaloop.core.participant.domain.repository.OracleRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan(basePackages = {"io.mojaloop.core.participant.domain"})
@Import(value = {MiscConfiguration.class, RoutingJpaConfiguration.class})
public class ParticipantDomainConfiguration implements MiscConfiguration.RequiredBeans, RoutingJpaConfiguration.RequiredBeans {

    @Bean
    public ParticipantCache participantCache(FspRepository fspRepository, OracleRepository oracleRepository) {

        return new ParticipantLocalCache(fspRepository, oracleRepository);
    }

    public interface RequiredBeans extends RoutingJpaConfiguration.RequiredBeans { }

    public interface RequiredSettings extends MiscConfiguration.RequiredSettings, RoutingJpaConfiguration.RequiredSettings { }

}
