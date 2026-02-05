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
package org.mojave.rail.fspiop.invoker.api.quotes;

import org.mojave.rail.fspiop.component.exception.FspiopException;
import org.mojave.rail.fspiop.component.type.Payer;
import org.mojave.scheme.fspiop.core.ErrorInformationObject;
import org.mojave.scheme.fspiop.core.QuotesIDPutResponse;
import retrofit2.http.Body;
import retrofit2.http.Path;

public interface PutQuotes {

    void putQuotes(Payer payer,
                   @Path("quoteId") String quoteId,
                   @Body QuotesIDPutResponse quotesIDPutResponse) throws FspiopException;

    void putQuotesError(Payer payer,
                        @Path("quoteId") String quoteId,
                        @Body ErrorInformationObject errorInformationObject) throws FspiopException;

}
