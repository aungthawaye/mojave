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

package io.mojaloop.core.wallet.store;

import io.mojaloop.core.wallet.intercom.client.WalletIntercomClientConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import(value = {WalletIntercomClientConfiguration.class})
@ComponentScan(basePackages = {"io.mojaloop.core.wallet.store"})
public class WalletStoreConfiguration implements WalletIntercomClientConfiguration.RequiredBeans {

    public interface RequiredBeans extends WalletIntercomClientConfiguration.RequiredBeans { }

    public interface RequiredSettings extends WalletIntercomClientConfiguration.RequiredSettings {

        WalletStoreConfiguration.Settings walletStoreSettings();

    }

    public record Settings(int refreshIntervalMs) { }

}
