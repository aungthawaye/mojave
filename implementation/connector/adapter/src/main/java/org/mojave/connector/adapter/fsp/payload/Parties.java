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
package org.mojave.connector.adapter.fsp.payload;

import org.mojave.fspiop.spec.core.Currency;
import org.mojave.fspiop.spec.core.PartyIdType;
import org.mojave.fspiop.spec.core.PartyPersonalInfo;

import java.util.List;

public class Parties {

    public static class Get {

        public record Request(PartyIdType partyIdType, String partyId, String subId) { }

        public record Response(List<Currency> supportedCurrencies,
                               String name,
                               PartyPersonalInfo personalInfo) { }

    }

}
