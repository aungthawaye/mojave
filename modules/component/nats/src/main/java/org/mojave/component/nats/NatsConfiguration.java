package org.mojave.component.nats;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.time.Duration;

public class NatsConfiguration {

    @Bean(destroyMethod = "close")
    public Connection natsConnection(NatsSettings settings)
        throws IOException, InterruptedException {

        var options = new Options.Builder()
                          .servers(settings.servers)
                          .connectionName(settings.connectionName())
                          .connectionTimeout(Duration.ofMillis(settings.connectionTimeoutMs()))
                          .maxReconnects(settings.maxReconnects())
                          .reconnectWait(Duration.ofMillis(settings.reconnectWaitMs));

        if (settings.noEcho()) {
            options.noEcho();
        }

        if (settings.username() != null && settings.password() != null) {
            options.userInfo(settings.username(), settings.password());
        }

        if (settings.token() != null) {
            options.token(settings.token().toCharArray());
        }

        return Nats.connect(options.build());
    }

    public interface RequiredSettings {

        NatsSettings natsSettings();

    }

    public record NatsSettings(String[] servers,
                               String connectionName,
                               String username,
                               String password,
                               String token,
                               int connectionTimeoutMs,
                               int maxReconnects,
                               int reconnectWaitMs,
                               boolean noEcho) { }

}
