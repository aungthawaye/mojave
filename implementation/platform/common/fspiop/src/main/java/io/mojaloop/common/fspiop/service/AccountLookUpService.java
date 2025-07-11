/*-
 * ================================================================================
 * Mojaloop OSS
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
package io.mojaloop.common.fspiop.service;

import io.mojaloop.common.fspiop.model.core.ErrorInformationObject;
import io.mojaloop.common.fspiop.model.core.PartiesTypeIDPutResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import java.util.Map;

public interface AccountLookUpService {

    @GET("parties/{partyIdType}/{partyId}")
    Call<String> getParty(@HeaderMap Map<String, String> fspiopHeaders,
                          @Path("partyIdType") String partyIdType,
                          @Path("partyId") String partyId);

    @GET("parties/{partyIdType}/{partyId}/{subId}")
    Call<String> getParty(@HeaderMap Map<String, String> fspiopHeaders,
                          @Path("partyIdType") String partyIdType,
                          @Path("partyId") String partyId,
                          @Path("subId") String subId);

    @PUT("parties/{partyIdType}/{partyId}")
    Call<String> putParty(@HeaderMap Map<String, String> fspiopHeaders,
                          @Path("partyIdType") String partyIdType,
                          @Path("partyId") String partyId,
                          @Body PartiesTypeIDPutResponse partiesTypeIDPutResponse);

    @PUT("parties/{partyIdType}/{partyId}/{subId}")
    Call<String> putParty(@HeaderMap Map<String, String> fspiopHeaders,
                          @Path("partyIdType") String partyIdType,
                          @Path("partyId") String partyId,
                          @Path("subId") String subId,
                          @Body PartiesTypeIDPutResponse partiesTypeIDPutResponse);

    @PUT("parties/{partyIdType}/{partyId}/error")
    Call<String> putPartyError(@HeaderMap Map<String, String> fspiopHeaders,
                               @Path("partyIdType") String partyIdType,
                               @Path("partyId") String partyId,
                               ErrorInformationObject errorInformationObject);

    @PUT("parties/{partyIdType}/{partyId}/{subId}/error")
    Call<String> putPartyError(@HeaderMap Map<String, String> fspiopHeaders,
                               @Path("partyIdType") String partyIdType,
                               @Path("partyId") String partyId,
                               @Path("subId") String subId,
                               ErrorInformationObject errorInformationObject);

    record Settings(String baseUrl) { }

}
