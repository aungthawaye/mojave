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

package io.mojaloop.core.lookup.domain;

import io.mojaloop.component.misc.MiscConfiguration;
import io.mojaloop.core.participant.store.ParticipantStoreConfiguration;
import io.mojaloop.fspiop.common.FspiopCommonConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan(basePackages = {"io.mojaloop.core.lookup.domain"})
@Import(value = {MiscConfiguration.class, FspiopCommonConfiguration.class, ParticipantStoreConfiguration.class})
public class LookUpDomainConfiguration {

    public interface RequiredBeans extends MiscConfiguration.RequiredBeans, FspiopCommonConfiguration.RequiredBeans, ParticipantStoreConfiguration.RequiredBeans { }

    public interface RequiredSettings extends ParticipantStoreConfiguration.RequiredSettings, MiscConfiguration.RequiredSettings, FspiopCommonConfiguration.RequiredSettings { }

}
