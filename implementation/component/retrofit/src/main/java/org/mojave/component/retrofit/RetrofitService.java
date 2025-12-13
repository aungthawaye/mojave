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
package org.mojave.component.retrofit;

import lombok.Getter;
import okhttp3.Dns;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import org.mojave.component.retrofit.converter.Jackson3ConverterFactory;
import org.mojave.component.retrofit.converter.NullOrEmptyConverterFactory;
import org.mojave.component.retrofit.debug.DnsDebug;
import org.mojave.component.retrofit.interceptor.HandshakeLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import tools.jackson.databind.ObjectMapper;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.security.KeyStore;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RetrofitService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetrofitService.class);

    public static <RES, E> Response<RES> invoke(Call<RES> invocation, ErrorDecoder<E> errorDecoder)
        throws InvocationException {

        E decodedErrorResponse = null;
        String errorResponseBody = null;

        try {

            Response<RES> response = invocation.execute();

            if (!response.isSuccessful()) {

                try (var responseBody = response.errorBody()) {

                    if (responseBody != null) {

                        errorResponseBody = responseBody.string();

                        if (errorDecoder != null) {
                            decodedErrorResponse = errorDecoder.decode(
                                response.code(), errorResponseBody);

                            throw new InvocationException(
                                response.code(), decodedErrorResponse, errorResponseBody);
                        }

                        LOGGER.error("Error response : ({})", responseBody.string());
                        throw new InvocationException(response.code(), null, errorResponseBody);
                    }

                    LOGGER.error("Error response has no body.");
                    throw new InvocationException(response.code(), null, null);
                }
            }

            return response;

        } catch (InvocationException e) {

            throw e;

        } catch (Exception e) {

            throw new InvocationException(e);
        }
    }

    public static <S> Builder<S> newBuilder(Class<S> service, String baseUrl) {

        return new Builder<>(service, baseUrl);
    }

    public interface ForwardingService {

        @DELETE
        Call<Void> delete(@Url String url,
                          @HeaderMap Map<String, String> headers,
                          @QueryMap Map<String, String> params,
                          @Body RequestBody body);

        @GET
        Call<Void> get(@Url String url,
                       @HeaderMap Map<String, String> headers,
                       @QueryMap Map<String, String> params);

        @PATCH
        Call<Void> patch(@Url String url,
                         @HeaderMap Map<String, String> headers,
                         @QueryMap Map<String, String> params,
                         @Body RequestBody body);

        @POST
        Call<Void> post(@Url String url,
                        @HeaderMap Map<String, String> headers,
                        @QueryMap Map<String, String> params,
                        @Body RequestBody body);

        @PUT
        Call<Void> put(@Url String url,
                       @HeaderMap Map<String, String> headers,
                       @QueryMap Map<String, String> params,
                       @Body RequestBody body);

    }

    public interface ErrorDecoder<E> {

        E decode(int status, String errorResponseBody) throws IOException;

    }

    public static class Builder<S> {

        private final Class<S> service;

        private final OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        private final Retrofit.Builder retrofitBuilder = new Retrofit.Builder();

        private HttpLoggingInterceptor loggingInterceptor = null;

        private HandshakeLoggingInterceptor handshakeLoggingInterceptor = null;

        private Builder(Class<S> service, String baseUrl) {

            this.service = service;
            this.retrofitBuilder.baseUrl(baseUrl.endsWith("/") ? baseUrl : baseUrl + "/");
        }

        public S build() {

            // They will be very problematic if you have other interceptors that modify the request.
            // So if we want to use them, add last.

            if (this.loggingInterceptor != null) {
                this.httpClientBuilder.addInterceptor(this.loggingInterceptor);
                LOGGER.debug("Built withHttpLogging.");
            }

            if (this.handshakeLoggingInterceptor != null) {
                this.httpClientBuilder.addNetworkInterceptor(this.handshakeLoggingInterceptor);
                LOGGER.debug("Built withHandshakeLogging.");
            }

            Retrofit retrofit = this.retrofitBuilder.client(this.httpClientBuilder.build()).build();

            return retrofit.create(this.service);
        }

        public Builder<S> withConverterFactories(Converter.Factory... factories) {

            LOGGER.debug("Configured withConverterFactories.");

            if (factories != null) {

                for (Converter.Factory factory : factories) {

                    this.retrofitBuilder.addConverterFactory(factory);
                }
            }

            return this;
        }

        public Builder<S> withCustomHeaders(Map<String, String> headers) {

            LOGGER.debug("Configured withCustomHeaders.");

            this.httpClientBuilder.addInterceptor(chain -> {

                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder();

                if (headers != null) {

                    for (String key : headers.keySet()) {
                        requestBuilder.header(key, headers.get(key));
                    }
                }

                Request request = requestBuilder.build();
                return chain.proceed(request);
            });

            return this;
        }

        public Builder<S> withDefaultFactories(ObjectMapper objectMapper) {

            LOGGER.debug("Configured withDefaultFactories.");

            this.retrofitBuilder.addConverterFactory(new NullOrEmptyConverterFactory());
            this.retrofitBuilder.addConverterFactory(ScalarsConverterFactory.create());
            this.retrofitBuilder.addConverterFactory(Jackson3ConverterFactory.create(objectMapper));

            return this;
        }

        public Builder<S> withDnsDebug() {

            LOGGER.debug("Configured withDnsDebug.");

            Dns current = this.httpClientBuilder.build().dns();

            this.httpClientBuilder.dns(new DnsDebug(current));

            return this;
        }

        public Builder<S> withDnsToIpConversion(String dns, String ip) {

            LOGGER.debug("Configured withDnsToIpConversion.");

            this.httpClientBuilder.dns(host -> {

                if (dns.equals(host)) {

                    return Collections.singletonList(InetAddress.getByName(ip));
                }

                return okhttp3.Dns.SYSTEM.lookup(host);

            });

            return this;
        }

        public Builder<S> withHandshakeLogging() {

            LOGGER.debug("Configured withTlsDebug.");

            this.handshakeLoggingInterceptor = new HandshakeLoggingInterceptor();

            return this;
        }

        public Builder<S> withHttpLogging(HttpLoggingInterceptor.Level level, boolean info) {

            LOGGER.debug("Configured withHttpLogging.");

            this.loggingInterceptor = new HttpLoggingInterceptor(message -> {

                if (info) {
                    LOGGER.info("Retrofit : {}", message);
                } else {
                    LOGGER.debug("Retrofit :{}", message);
                }

            });

            this.loggingInterceptor.setLevel(level);

            return this;
        }

        public Builder<S> withInterceptors(Interceptor... interceptors) {

            LOGGER.debug("Configured withInterceptors.");

            if (interceptors != null) {

                for (Interceptor interceptor : interceptors) {

                    this.httpClientBuilder.addInterceptor(interceptor);
                }
            }

            return this;
        }

        public Builder<S> withMutualTLS(InputStream keyStoreInputStream,
                                        String keyStorePassword,
                                        InputStream trustStoreInputStream,
                                        String trustStorePassword,
                                        boolean ignoreHostnameVerification) {

            LOGGER.debug("Configured withMutualTLS.");

            try {

                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                keyStore.load(keyStoreInputStream, keyStorePassword.toCharArray());

                KeyManagerFactory kmf = KeyManagerFactory.getInstance(
                    KeyManagerFactory.getDefaultAlgorithm());
                kmf.init(keyStore, keyStorePassword.toCharArray());

                KeyStore trustStore = KeyStore.getInstance("PKCS12");
                trustStore.load(trustStoreInputStream, trustStorePassword.toCharArray());

                TrustManagerFactory tmf = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(trustStore);

                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

                X509TrustManager tm = (X509TrustManager) tmf.getTrustManagers()[0];

                this.httpClientBuilder.sslSocketFactory(sslContext.getSocketFactory(), tm);

                okhttp3.ConnectionSpec spec = new okhttp3.ConnectionSpec.Builder(
                    okhttp3.ConnectionSpec.MODERN_TLS)
                                                  .tlsVersions(okhttp3.TlsVersion.TLS_1_2)
                                                  .build();

                this.httpClientBuilder.connectionSpecs(List.of(spec));
                this.httpClientBuilder.hostnameVerifier(
                    (hostname, session) -> ignoreHostnameVerification);

            } catch (Exception e) {
                throw new RuntimeException("Failed to configure mTLS", e);
            }

            return this;
        }

        public Builder<S> withTimeouts(int connectTimeout, int callTimeout, int readTimeout) {

            LOGGER.debug("Configured withTimeouts.");

            this.httpClientBuilder.connectTimeout(
                Duration.ofSeconds(connectTimeout <= 0 ? 60 : connectTimeout));
            this.httpClientBuilder.callTimeout(
                Duration.ofSeconds(callTimeout <= 0 ? 60 : callTimeout));
            this.httpClientBuilder.readTimeout(
                Duration.ofSeconds(readTimeout <= 0 ? 60 : readTimeout));

            return this;
        }

    }

    @Getter
    public static class InvocationException extends Exception {

        private final int responseStatusCode;

        private final Object decodedErrorResponse;

        private final String originalErrorMessage;

        public InvocationException(int responseStatusCode,
                                   Object decodedErrorResponse,
                                   String originalErrorMessage) {

            super();

            this.responseStatusCode = responseStatusCode;
            this.decodedErrorResponse = decodedErrorResponse;
            this.originalErrorMessage = originalErrorMessage;

        }

        public InvocationException(Throwable cause) {

            super(cause);

            this.responseStatusCode = 0;
            this.decodedErrorResponse = null;
            this.originalErrorMessage = cause.getMessage();

        }

    }

}
