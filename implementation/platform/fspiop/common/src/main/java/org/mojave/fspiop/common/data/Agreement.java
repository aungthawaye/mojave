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

package org.mojave.fspiop.common.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mojave.fspiop.spec.core.AmountType;
import org.mojave.fspiop.spec.core.Money;
import org.mojave.fspiop.spec.core.PartyIdInfo;

public record Agreement(@JsonProperty(required = true) String quoteId,
                        @JsonProperty(required = true) PartyIdInfo payer,
                        @JsonProperty(required = true) PartyIdInfo payee,
                        @JsonProperty(required = true) AmountType amountType,
                        @JsonProperty(required = true) Money originalAmount,
                        @JsonProperty(required = true) Money payeeFspFee,
                        @JsonProperty(required = true) Money payeeFspCommission,
                        @JsonProperty(required = true) Money payeeReceiveAmount,
                        @JsonProperty(required = true) Money transferAmount,
                        @JsonProperty(required = true) String expiration) { }
