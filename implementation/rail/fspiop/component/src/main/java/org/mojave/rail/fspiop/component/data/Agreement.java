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
package org.mojave.rail.fspiop.component.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mojave.scheme.fspiop.core.AmountType;
import org.mojave.scheme.fspiop.core.Money;
import org.mojave.scheme.fspiop.core.PartyIdInfo;
import org.mojave.scheme.fspiop.core.TransactionScenario;

public record Agreement(@JsonProperty(required = true) String quoteId,
                        @JsonProperty(required = true) PartyIdInfo payer,
                        @JsonProperty(required = true) PartyIdInfo payee,
                        @JsonProperty(required = true) AmountType amountType,
                        @JsonProperty(required = true) TransactionScenario scenario,
                        @JsonProperty String subScenario,
                        @JsonProperty(required = true) Money originalAmount,
                        @JsonProperty(required = true) Money payeeFspFee,
                        @JsonProperty(required = true) Money payeeFspCommission,
                        @JsonProperty(required = true) Money payeeReceiveAmount,
                        @JsonProperty(required = true) Money transferAmount,
                        @JsonProperty(required = true) Long expireAt) { }
