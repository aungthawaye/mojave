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
package org.mojave.core.participant.contract.exception.fsp;

import lombok.Getter;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.common.datatype.type.participant.FspCode;

import java.util.HashMap;
import java.util.Map;

@Getter
public class FspCodeNotFoundException extends UncheckedDomainException {

    public static final String CODE = "FSP_CODE_NOT_FOUND";

    private static final String TEMPLATE = "FSP Code ({0}) cannot be not found.";

    private final FspCode fspCode;

    public FspCodeNotFoundException(final FspCode fspCode) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{fspCode.value()}));

        this.fspCode = fspCode;

    }

    public static FspCodeNotFoundException from(final Map<String, String> extras) {

        final var fspCode = new FspCode(extras.get(Keys.FSP_CODE));

        return new FspCodeNotFoundException(fspCode);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.FSP_CODE, this.fspCode.value());

        return extras;
    }

    public static class Keys {

        public static final String FSP_CODE = "code";

    }

}
