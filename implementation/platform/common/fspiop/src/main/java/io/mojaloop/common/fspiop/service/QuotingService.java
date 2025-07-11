package io.mojaloop.common.fspiop.service;

import io.mojaloop.common.fspiop.model.core.ErrorInformationObject;
import io.mojaloop.common.fspiop.model.core.QuotesIDPutResponse;
import io.mojaloop.common.fspiop.model.core.QuotesPostRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import java.util.Map;

public interface QuotingService {

    @POST("quotes")
    Call<String> postQuotes(@HeaderMap Map<String, String> fspiopHeaders, @Body QuotesPostRequest quotesPostRequest);

    @PUT("quotes/{quoteId}")
    Call<String> putQuotes(@HeaderMap Map<String, String> fspiopHeaders,
                           @Path("quoteId") String quoteId,
                           @Body QuotesIDPutResponse quotesIDPutResponse);

    @PUT("quotes/{quoteId}/error")
    Call<String> putQuotesError(@HeaderMap Map<String, String> fspiopHeaders,
                                @Path("quoteId") String quoteId,
                                @Body ErrorInformationObject errorInformationObject);

    record Settings(String baseUrl) { }

}
