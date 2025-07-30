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

package io.mojaloop.fspiop.service.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.web.security.spring.AuthenticationErrorWriter;
import io.mojaloop.component.web.security.spring.Authenticator;
import io.mojaloop.component.web.security.spring.SpringSecurityConfiguration;
import io.mojaloop.component.web.security.spring.SpringSecurityConfigurer;
import io.mojaloop.fspiop.common.data.ParticipantDetails;
import io.mojaloop.fspiop.component.web.FspiopErrorWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(value = {SpringSecurityConfiguration.class})
public class FspiopSpringSecurityConfiguration implements SpringSecurityConfiguration.RequiredBeans {

    private final SpringSecurityConfigurer.Settings springSecuritySettings;

    private final ParticipantDetails participantDetails;

    private final ParticipantVerifier participantVerifier;

    private final ObjectMapper objectMapper;

    public FspiopSpringSecurityConfiguration(SpringSecurityConfigurer.Settings springSecuritySettings,
                                             ParticipantDetails participantDetails,
                                             ParticipantVerifier participantVerifier,
                                             ObjectMapper objectMapper) {

        assert springSecuritySettings != null;
        assert participantDetails != null;
        assert participantVerifier != null;
        assert objectMapper != null;

        this.springSecuritySettings = springSecuritySettings;
        this.participantDetails = participantDetails;
        this.participantVerifier = participantVerifier;
        this.objectMapper = objectMapper;

    }

    @Bean
    @Override
    public AuthenticationErrorWriter authenticationErrorWriter() {

        return new FspiopGatekeeperErrorWriter(new FspiopErrorWriter(this.objectMapper));
    }

    @Bean
    @Override
    public Authenticator authenticator() {

        return new FspiopServiceGatekeeper(this.springSecuritySettings,
                                           this.participantDetails,
                                           this.participantVerifier,
                                           this.objectMapper);
    }

    public interface RequiredBeans {

        ParticipantVerifier participantVerifier();

    }

    public interface RequiredSettings extends SpringSecurityConfiguration.RequiredSettings { }

}
