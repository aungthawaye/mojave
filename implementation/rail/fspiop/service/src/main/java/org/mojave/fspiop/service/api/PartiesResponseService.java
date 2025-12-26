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
package org.mojave.fspiop.service.api;

import org.mojave.specification.fspiop.core.ErrorInformationObject;
import org.mojave.specification.fspiop.core.PartiesTypeIDPutResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.PUT;
import retrofit2.http.Url;

import java.util.Map;

public interface PartiesResponseService {

    @PUT
    Call<String> putParties(@Url String url,
                            @HeaderMap Map<String, String> fspiopHeaders,
                            @Body PartiesTypeIDPutResponse partiesTypeIDPutResponse);

    @PUT
    Call<String> putPartiesError(@Url String url,
                                 @HeaderMap Map<String, String> fspiopHeaders,
                                 ErrorInformationObject errorInformationObject);

}
