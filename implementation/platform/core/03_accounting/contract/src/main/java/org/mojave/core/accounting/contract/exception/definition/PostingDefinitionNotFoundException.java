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

package org.mojave.core.accounting.contract.exception.definition;

import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.core.common.datatype.identifier.accounting.PostingDefinitionId;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class PostingDefinitionNotFoundException extends UncheckedDomainException {

    public static final String CODE = "POSTING_DEFINITION_NOT_FOUND";

    private static final String TEMPLATE = "Posting Definition Id ({0}) cannot be found.";

    private final PostingDefinitionId postingDefinitionId;

    public PostingDefinitionNotFoundException(final PostingDefinitionId postingDefinitionId) {

        super(new ErrorTemplate(
            CODE, TEMPLATE,
            new String[]{postingDefinitionId.getId().toString()}));

        this.postingDefinitionId = postingDefinitionId;
    }

    public static PostingDefinitionNotFoundException from(final Map<String, String> extras) {

        final var id = new PostingDefinitionId(
            Long.valueOf(extras.get(Keys.POSTING_DEFINITION_ID)));

        return new PostingDefinitionNotFoundException(id);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.POSTING_DEFINITION_ID, this.postingDefinitionId.getId().toString());

        return extras;
    }

    public static class Keys {

        public static final String POSTING_DEFINITION_ID = "postingDefinitionId";

    }

}
