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

package org.mojave.connector.sample;

import org.mojave.component.misc.spring.SpringLauncher;
import org.mojave.connector.gateway.ConnectorGatewayApplication;

public class SampleConnectorApplication {

    public static void main(String[] args) {

        SpringLauncher.launch(
            (String[] params) -> ConnectorGatewayApplication.run(
                args, new Class<?>[]{
                    SampleConnectorSharedConfiguration.class,
                    SampleConnectorSharedSettings.class},
                new Class<?>[]{SampleConnectorInExtraConfiguration.class}, new Class<?>[]{
                    SampleConnectorOutExtraConfiguration.class}), args);
    }

}
