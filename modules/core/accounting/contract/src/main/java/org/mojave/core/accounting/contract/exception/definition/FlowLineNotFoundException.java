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
import org.mojave.common.datatype.identifier.accounting.FlowLineId;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;

import java.util.HashMap;
import java.util.Map;

@Getter
public class FlowLineNotFoundException extends UncheckedDomainException {

    public static final String CODE = "FLOW_LINE_NOT_FOUND";

    private static final String TEMPLATE = "Flow Line Id ({0}) cannot be found.";

    private final FlowLineId flowLineId;

    public FlowLineNotFoundException(final FlowLineId flowLineId) {

        super(new ErrorTemplate(
            CODE, TEMPLATE,
            new String[]{flowLineId.getId().toString()}));

        this.flowLineId = flowLineId;
    }

    public static FlowLineNotFoundException from(final Map<String, String> extras) {

        final var id = new FlowLineId(
            Long.valueOf(extras.get(Keys.FLOW_LINE_ID)));

        return new FlowLineNotFoundException(id);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.FLOW_LINE_ID, this.flowLineId.getId().toString());

        return extras;
    }

    public static class Keys {

        public static final String FLOW_LINE_ID = "flowLineId";

    }

}
