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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
public class FailedToUpdateNdcException extends CheckedDomainException {

    public static final String CODE = "FAILED_TO_UPDATE_NDC";

    private static final String TEMPLATE = "Failed to update NDC. New NDC ({0}) cannot be lower than the total of position ({1}) and reserved ({2}) amounts.";

    private final BigDecimal ndc;

    private final BigDecimal position;

    private final BigDecimal reserved;

    public FailedToUpdateNdcException(BigDecimal ndc, BigDecimal position, BigDecimal reserved) {

        super(new ErrorTemplate(
            CODE, TEMPLATE, new String[]{
            ndc.stripTrailingZeros().toPlainString(),
            position.stripTrailingZeros().toPlainString(),
            reserved.stripTrailingZeros().toPlainString()}));

        this.ndc = ndc;
        this.position = position;
        this.reserved = reserved;
    }

    public static FailedToUpdateNdcException from(Map<String, String> extras) {

        return new FailedToUpdateNdcException(
            new BigDecimal(extras.get(Keys.NDC)), new BigDecimal(extras.get(Keys.POSITION)),
            new BigDecimal(extras.get(Keys.RESERVED)));
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.NDC, this.ndc.stripTrailingZeros().toPlainString());
        extras.put(Keys.POSITION, this.position.stripTrailingZeros().toPlainString());
        extras.put(Keys.RESERVED, this.reserved.stripTrailingZeros().toPlainString());

        return extras;
    }

    public static class Keys {

        public static final String NDC = "ndc";

        public static final String POSITION = "position";

        public static final String RESERVED = "reserved";

    }

}
