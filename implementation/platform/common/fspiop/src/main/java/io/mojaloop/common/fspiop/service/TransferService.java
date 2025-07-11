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
import io.mojaloop.common.fspiop.model.core.TransfersIDPatchResponse;
import io.mojaloop.common.fspiop.model.core.TransfersIDPutResponse;
import io.mojaloop.common.fspiop.model.core.TransfersPostRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import java.util.Map;

public interface TransferService {

    @GET("transfers/{transferId}")
    Call<String> getTransfers(@HeaderMap Map<String, String> fspiopHeaders, @Path("transferId") String transferId);

    @PATCH("transfers/{transferId}")
    Call<String> patchTransfers(@HeaderMap Map<String, String> fspiopHeaders,
                                @Path("transferId") String transferId,
                                @Body TransfersIDPatchResponse transfersIDPatchResponse);

    @POST("transfers")
    Call<String> postTransfers(@HeaderMap Map<String, String> fspiopHeaders, @Body TransfersPostRequest transfersPostRequest);

    @PUT("transfers/{transferId}")
    Call<String> putTransfers(@HeaderMap Map<String, String> fspiopHeaders,
                              @Path("transferId") String transferId,
                              @Body TransfersIDPutResponse transfersIDPutResponse);

    @PUT("transfers/{transferId}/error")
    Call<String> putTransfersError(@HeaderMap Map<String, String> fspiopHeaders,
                                   @Path("transferId") String transferId,
                                   @Body ErrorInformationObject errorInformationObject);

    record Settings(String baseUrl) { }

}
