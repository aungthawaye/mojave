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
import org.mojave.common.datatype.type.participant.FspCode;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;

import java.util.Map;

@Getter
public class FspAlreadyAddedIntoFspGroupException extends UncheckedDomainException {

    public static final String CODE = "FSP_ALREADY_ADDED_INTO_FSP_GROUP";

    private static final String TEMPLATE = "FSP ({0}) already added into FSP group ({1}).";

    private final FspCode fspCode;

    private final String fspGroupName;

    public FspAlreadyAddedIntoFspGroupException(FspCode fspCode, String fspGroupName) {

        super(new ErrorTemplate(
            CODE, TEMPLATE, new String[]{
            fspCode.value(),
            fspGroupName}));

        this.fspCode = fspCode;
        this.fspGroupName = fspGroupName;
    }

    public static FspAlreadyAddedIntoFspGroupException from(final Map<String, String> extras) {

        final var fspCode = new FspCode(extras.get(Keys.FSP_CODE));
        final var fspGroupName = extras.get(Keys.FSP_GROUP_NAME);

        return new FspAlreadyAddedIntoFspGroupException(fspCode, fspGroupName);
    }

    @Override
    public Map<String, String> extras() {

        return Map.of(Keys.FSP_CODE, fspCode.value(), Keys.FSP_GROUP_NAME, fspGroupName);
    }

    public static class Keys {

        public static final String FSP_CODE = "code";

        public static final String FSP_GROUP_NAME = "name";

    }

}
