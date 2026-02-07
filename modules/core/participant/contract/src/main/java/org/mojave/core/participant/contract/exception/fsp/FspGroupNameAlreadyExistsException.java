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

import java.util.HashMap;
import java.util.Map;

@Getter
public class FspGroupNameAlreadyExistsException extends UncheckedDomainException {

    public static final String CODE = "FSP_GROUP_NAME_ALREADY_EXISTS";

    private static final String TEMPLATE = "FSP Group name ({0}) already exists.";

    private final String name;

    public FspGroupNameAlreadyExistsException(final String name) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{name}));

        this.name = name;
    }

    public static FspGroupNameAlreadyExistsException from(final Map<String, String> extras) {

        return new FspGroupNameAlreadyExistsException(extras.get(Keys.NAME));
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
