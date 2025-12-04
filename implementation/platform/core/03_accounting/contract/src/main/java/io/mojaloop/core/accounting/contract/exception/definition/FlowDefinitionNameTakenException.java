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
 * ==============================================================================
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
 * ==============================================================================
 */

package io.mojaloop.core.accounting.contract.exception.definition;

import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.component.misc.exception.UncheckedDomainException;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class FlowDefinitionNameTakenException extends UncheckedDomainException {

    public static final String CODE = "FUND_IN_DEFINITION_NAME_TAKEN";

    private static final String TEMPLATE = "Fund In Definition name ({0}) is already taken.";

    private final String name;

    public FlowDefinitionNameTakenException(final String name) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{name}));

        this.name = name;
    }

    public static FlowDefinitionNameTakenException from(final Map<String, String> extras) {

        final var name = extras.get(Keys.NAME);

        return new FlowDefinitionNameTakenException(name);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.NAME, this.name);

        return extras;
    }

    public static class Keys {

        public static final String NAME = "name";

    }

}
