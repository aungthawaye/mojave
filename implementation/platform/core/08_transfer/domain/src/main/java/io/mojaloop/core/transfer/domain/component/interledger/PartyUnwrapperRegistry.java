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
package io.mojaloop.core.transfer.domain.component.interledger;

import io.mojaloop.core.common.datatype.type.participant.FspCode;

import java.util.HashMap;
import java.util.Map;

public class PartyUnwrapperRegistry {

    private final Map<FspCode, PartyUnwrapper<?>> unwrappers = new HashMap<>();

    public PartyUnwrapper<?> get(FspCode fspCode) {

        return this.unwrappers.get(fspCode);
    }

    public <T> void register(FspCode fspCode, PartyUnwrapper<T> unwrapper) {

        this.unwrappers.put(fspCode, unwrapper);
    }

}
