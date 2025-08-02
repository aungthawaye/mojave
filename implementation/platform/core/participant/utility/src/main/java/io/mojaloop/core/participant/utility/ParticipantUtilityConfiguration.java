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

package io.mojaloop.core.participant.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.jackson.ComponentJacksonConfiguration;
import io.mojaloop.core.participant.utility.client.ParticipantClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan(basePackages = "io.mojaloop.core.participant.utility")
@Import(value = {ComponentJacksonConfiguration.class})
public class ParticipantUtilityConfiguration {

    @Bean
    public ParticipantClient participantClient(ParticipantClient.Settings settings, ObjectMapper objectMapper) {

        return new ParticipantClient(settings, objectMapper);
    }

    public interface RequiredSettings extends ComponentJacksonConfiguration.RequiredSettings {

        ParticipantClient.Settings participantClientSettings();

        ParticipantUtilityConfiguration.Settings utilitySettings();

    }

    public record Settings(int refreshIntervalMs) { }

}
