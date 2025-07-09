package io.mojaloop.common.fspiop.service;

import io.mojaloop.common.fspiop.core.model.ErrorInformationObject;
import io.mojaloop.common.fspiop.core.model.TransfersIDPatchResponse;
import io.mojaloop.common.fspiop.core.model.TransfersIDPutResponse;
import io.mojaloop.common.fspiop.core.model.TransfersPostRequest;
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
