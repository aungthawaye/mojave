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

import java.util.Map;

@Getter
public class DuplicatePostingDefinitionIndexException extends UncheckedDomainException {

    public static final String CODE = "DUPLICATE_POSTING_DEFINITION_INDEX";

    private static final String TEMPLATE = "Posting Definition has duplicate index ({0})";

    private final Integer index;

    public DuplicatePostingDefinitionIndexException(Integer index) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{index.toString()}));

        this.index = index;
    }

    public static DuplicatePostingDefinitionIndexException from(final Map<String, String> extras) {

        return new DuplicatePostingDefinitionIndexException(
            Integer.parseInt(extras.get(Keys.INDEX)));
    }

    @Override
    public Map<String, String> extras() {

        return Map.of(Keys.INDEX, index.toString());
    }

    public static class Keys {

        public static final String INDEX = "index";

    }

}
