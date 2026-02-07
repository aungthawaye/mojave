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

package org.mojave.core.participant.intercom.client.service;

import org.mojave.core.participant.contract.data.FspData;
import org.mojave.core.participant.contract.data.OracleData;
import org.mojave.core.participant.contract.data.SspData;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface ParticipantIntercomService {

    String MODULE_PREFIX = "/participant-intercom";

    interface FspQuery {

        @GET(MODULE_PREFIX + "/fsps/get-all-fsps")
        Call<List<FspData>> getAllFsps();

        @GET(MODULE_PREFIX + "/fsps/get-by-fsp-id")
        Call<FspData> getByFspCode(@Query("code") String fspCode);

        @GET(MODULE_PREFIX + "/fsps/get-by-fsp-id")
        Call<FspData> getByFspId(@Query("fspId") String fspId);

    }

    interface OracleQuery {

        @GET(MODULE_PREFIX + "/oracles/get-all-oracles")
        Call<List<OracleData>> getAllOracles();

        @GET(MODULE_PREFIX + "/oracles/get-by-oracle-id")
        Call<OracleData> getByOracleId(@Query("oracleId") String oracleId);

        @GET(MODULE_PREFIX + "/oracles/get-by-party-id-type")
        Call<OracleData> getByPartyIdType(@Query("partyIdType") String partyIdType);

    }

    interface SspQuery {

        @GET(MODULE_PREFIX + "/ssps/get-all-ssps")
        Call<List<SspData>> getAllSsps();

        @GET(MODULE_PREFIX + "/ssps/get-by-ssp-code")
        Call<SspData> getBySspCode(@Query("code") String sspCode);

        @GET(MODULE_PREFIX + "/ssps/get-by-ssp-id")
        Call<SspData> getBySspId(@Query("sspId") String sspId);

    }

    record Settings(String baseUrl) { }

}
