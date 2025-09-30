package io.mojaloop.core.quoting.domain;

import io.mojaloop.core.participant.store.ParticipantStore;
import io.mojaloop.fspiop.service.api.forwarder.ForwardRequest;
import io.mojaloop.fspiop.service.api.quotes.RespondQuotes;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MocksConfiguration {

    @Bean
    public ForwardRequest forwardRequest() {
        return Mockito.mock(ForwardRequest.class);
    }

    @Bean
    public RespondQuotes respondQuotes() {
        return Mockito.mock(RespondQuotes.class);
    }

    @Bean
    public ParticipantStore participantStore() {
        return Mockito.mock(ParticipantStore.class);
    }
}
