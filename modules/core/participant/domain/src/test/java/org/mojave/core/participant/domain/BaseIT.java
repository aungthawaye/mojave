package org.mojave.core.participant.domain;

import org.junit.jupiter.api.BeforeAll;

public class BaseIT {

    private static final String WRITE_DB_URL = "jdbc:mysql://localhost:3306/mv_mojave?createDatabaseIfNotExist=true";

    private static final String WRITE_DB_USER = "root";

    private static final String WRITE_DB_PASSWORD = "password";

    @BeforeAll
    public static void beforeAll() {

        ParticipantFlyway.migrate(WRITE_DB_URL, WRITE_DB_USER, WRITE_DB_PASSWORD);

    }

}
