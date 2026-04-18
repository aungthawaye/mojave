/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
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
 * ===
 */

package org.mojave.mono.core.consumer;

import org.mojave.core.accounting.intercom.consumer.AccountingIntercomConsumerConfiguration;
import org.mojave.core.wallet.intercom.consumer.WalletIntercomConsumerConfiguration;
import org.springframework.context.annotation.Import;

@Import(
    value = {
        AccountingIntercomConsumerConfiguration.class,
        WalletIntercomConsumerConfiguration.class})
public class MonoConsumerConfiguration {

    public interface RequiredDependencies
        extends AccountingIntercomConsumerConfiguration.RequiredDependencies,
                WalletIntercomConsumerConfiguration.RequiredDependencies { }

    public interface RequiredSettings
        extends AccountingIntercomConsumerConfiguration.RequiredSettings,
                WalletIntercomConsumerConfiguration.RequiredSettings { }

}
