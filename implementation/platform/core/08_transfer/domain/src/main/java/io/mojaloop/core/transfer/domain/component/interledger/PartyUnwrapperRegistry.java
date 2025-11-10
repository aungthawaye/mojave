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

import io.mojaloop.component.misc.spring.SpringContext;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.core.transfer.contract.component.interledger.PartyUnwrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class PartyUnwrapperRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(PartyUnwrapperRegistry.class);

    private final Map<FspCode, PartyUnwrapper> byBean = new HashMap<>();

    private final Map<FspCode, String> byQualifier = new HashMap<>();

    public PartyUnwrapper get(FspCode fspCode) {

        var unwrapper = this.byBean.get(fspCode);

        if (unwrapper == null) {

            unwrapper = SpringContext.getBean(PartyUnwrapper.class, this.byQualifier.get(fspCode));
        }

        return unwrapper;
    }

    public void register(FspCode fspCode, PartyUnwrapper unwrapper) {

        if (this.byQualifier.containsKey(fspCode)) {

            throw new IllegalStateException(String.format("A unwrapper for FSP: %s has already been registered with qualifier: %s", fspCode, this.byQualifier.get(fspCode)));
        }

        this.byBean.put(fspCode, unwrapper);
    }

    public void register(FspCode fspCode, String qualifier) {

        if (this.byBean.containsKey(fspCode)) {

            throw new IllegalStateException(String.format("A unwrapper for FSP: %s has already been registered with bean: %s", fspCode, this.byBean.get(fspCode)));
        }

        this.byQualifier.put(fspCode, qualifier);
    }

    public void register(FspCode fspCode) {

        this.register(fspCode, fspCode.value());
    }

}
