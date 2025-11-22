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
/*-
 * =============================================================================
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
 * =============================================================================
 */

package io.mojaloop.core.wallet.domain.command;

import io.mojaloop.component.jpa.routing.RoutingDataSource;
import io.mojaloop.core.wallet.domain.TestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@SpringBootTest(classes = TestConfiguration.class)
public abstract class BaseDomainIT {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(final DataSource dataSource) {

        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @BeforeEach
    void cleanupDb() {

        RoutingDataSource.Context.set(RoutingDataSource.Keys.WRITE);

        // Disable FK checks to truncate in any order
        this.jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=0");

        // Order doesn't matter with FK checks disabled
        this.jdbcTemplate.execute("TRUNCATE TABLE wlt_balance_update");
        this.jdbcTemplate.execute("TRUNCATE TABLE wlt_wallet");
        this.jdbcTemplate.execute("TRUNCATE TABLE wlt_position_update");
        this.jdbcTemplate.execute("TRUNCATE TABLE wlt_position");

        // Re-enable FK checks
        this.jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=1");
    }

}
