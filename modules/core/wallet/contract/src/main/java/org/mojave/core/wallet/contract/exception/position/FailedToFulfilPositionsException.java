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
package org.mojave.core.wallet.contract.exception.position;

import lombok.Getter;
import org.mojave.component.misc.exception.CheckedDomainException;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.common.datatype.identifier.wallet.PositionUpdateId;

import java.util.HashMap;
import java.util.Map;

@Getter
public class FailedToFulfilPositionsException extends CheckedDomainException {

    public static final String CODE = "FAILED_TO_FULFIL_POSITIONS";

    private static final String TEMPLATE = "Fulfil failed : reservationId ({0}).";

    private final PositionUpdateId reservationId;

    public FailedToFulfilPositionsException(final PositionUpdateId reservationId) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{reservationId.getId().toString()}));

        this.reservationId = reservationId;
    }

    public static FailedToFulfilPositionsException from(final Map<String, String> extras) {

        final var reservationId = new PositionUpdateId(
            Long.valueOf(extras.get(Keys.RESERVATION_ID)));

        return new FailedToFulfilPositionsException(reservationId);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.RESERVATION_ID, this.reservationId.getId().toString());

        return extras;
    }

    public static class Keys {

        public static final String RESERVATION_ID = "reservationId";

    }

}
