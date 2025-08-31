package io.mojaloop.core.participant.admin.client.api;

import io.mojaloop.core.participant.admin.client.TestConfiguration;
import io.mojaloop.core.participant.admin.client.exception.ParticipantCommandClientException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class GetAllFspsIT {

    @Autowired
    private GetAllFsps getAllFsps;

    @Test
    public void test_successfully_get_all() throws ParticipantCommandClientException {
        this.getAllFsps.execute();
    }
}
