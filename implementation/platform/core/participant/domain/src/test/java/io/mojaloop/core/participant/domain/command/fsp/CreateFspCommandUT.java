package io.mojaloop.core.participant.domain.command.fsp;

import io.mojaloop.common.component.flyway.FlywayMigration;
import io.mojaloop.common.component.vault.Vault;
import io.mojaloop.core.participant.contract.command.fsp.CreateFspCommand;
import io.mojaloop.core.participant.domain.LocalVaultSettings;
import io.mojaloop.core.participant.domain.ParticipantDomainConfiguration;
import io.mojaloop.core.participant.domain.TestConfiguration;
import io.mojaloop.core.participant.domain.settings.ParticipantVaultBasedSettings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class CreateFspCommandUT {

    @Autowired
    private CreateFspCommand createFspCommand;

    @BeforeAll
    public static void setup() {

        var vault = new Vault(LocalVaultSettings.VAULT_ADDR, LocalVaultSettings.VAULT_TOKEN, LocalVaultSettings.ENGINE_PATH);

        FlywayMigration.migrate(vault.get(ParticipantVaultBasedSettings.VaultPaths.FLYWAY_PATH, FlywayMigration.Settings.class));
    }

    @Test
    public void test() {

    }

}
