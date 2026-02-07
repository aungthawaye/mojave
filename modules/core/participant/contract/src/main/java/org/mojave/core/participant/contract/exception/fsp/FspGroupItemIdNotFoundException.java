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
import org.mojave.common.datatype.identifier.participant.FspGroupItemId;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;

import java.util.HashMap;
import java.util.Map;

@Getter
public class FspGroupItemIdNotFoundException extends UncheckedDomainException {

    public static final String CODE = "FSP_GROUP_ITEM_ID_NOT_FOUND";

    private static final String TEMPLATE = "FSP Group item id ({0}) not found.";

    private final FspGroupItemId fspGroupItemId;

    public FspGroupItemIdNotFoundException(final FspGroupItemId fspGroupItemId) {

        super(new ErrorTemplate(
            CODE, TEMPLATE,
            new String[]{fspGroupItemId == null ? null : fspGroupItemId.getId().toString()}));

        this.fspGroupItemId = fspGroupItemId;
    }

    public static FspGroupItemIdNotFoundException from(final Map<String, String> extras) {

        final var raw = extras.get(Keys.FSP_GROUP_ITEM_ID);
        final var id = raw == null ? null : new FspGroupItemId(Long.parseLong(raw));

        return new FspGroupItemIdNotFoundException(id);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(
            Keys.FSP_GROUP_ITEM_ID,
            this.fspGroupItemId == null ? null : this.fspGroupItemId.getId().toString());

        return extras;
    }

    public static class Keys {

        public static final String FSP_GROUP_ITEM_ID = "fspGroupItemId";

    }

}
