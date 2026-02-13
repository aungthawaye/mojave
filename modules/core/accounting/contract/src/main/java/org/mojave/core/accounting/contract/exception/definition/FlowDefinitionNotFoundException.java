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

package org.mojave.core.accounting.contract.exception.definition;

import lombok.Getter;
import org.mojave.common.datatype.identifier.accounting.FlowDefinitionId;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;

import java.util.HashMap;
import java.util.Map;

@Getter
public class FlowDefinitionNotFoundException extends UncheckedDomainException {

    public static final String CODE = "FLOW_DEFINITION_NOT_FOUND";

    private static final String TEMPLATE = "Flow Definition Id ({0}) cannot be not found.";

    private final FlowDefinitionId flowDefinitionId;

    public FlowDefinitionNotFoundException(final FlowDefinitionId flowDefinitionId) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{flowDefinitionId.getId().toString()}));

        this.flowDefinitionId = flowDefinitionId;
    }

    public static FlowDefinitionNotFoundException from(final Map<String, String> extras) {

        final var id = new FlowDefinitionId(Long.valueOf(extras.get(Keys.FLOW_DEFINITION_ID)));

        return new FlowDefinitionNotFoundException(id);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.FLOW_DEFINITION_ID, this.flowDefinitionId.getId().toString());

        return extras;
    }

    public static class Keys {

        public static final String FLOW_DEFINITION_ID = "flowDefinitionId";

    }

}
