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

package org.mojave.operation.usecase;

import org.mojave.core.accounting.admin.client.AccountingAdminClientConfiguration;
import org.mojave.core.participant.admin.client.ParticipantAdminClientConfiguration;
import org.mojave.core.wallet.admin.client.WalletAdminClientConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan(basePackages = {"org.mojave.operation.usecase"})
@Import(
    value = {
        ParticipantAdminClientConfiguration.class,
        AccountingAdminClientConfiguration.class,
        WalletAdminClientConfiguration.class})
public class OperationUseCaseConfiguration
    implements ParticipantAdminClientConfiguration.RequiredBeans,
               AccountingAdminClientConfiguration.RequiredBeans,
               WalletAdminClientConfiguration.RequiredBeans {

    public interface RequiredSettings extends ParticipantAdminClientConfiguration.RequiredSettings,
                                              AccountingAdminClientConfiguration.RequiredSettings,
                                              WalletAdminClientConfiguration.RequiredSettings { }

}
