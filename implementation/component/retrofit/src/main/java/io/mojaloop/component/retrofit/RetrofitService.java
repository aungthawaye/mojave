package io.mojaloop.component.retrofit;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.retrofit.converter.NullOrEmptyConverterFactory;
import lombok.Getter;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.time.Duration;
import java.util.Map;

public class RetrofitService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetrofitService.class);

    public static <RES, E> Response<RES> invoke(Call<RES> invocation, ErrorDecoder<E> errorDecoder) throws InvocationException {

        try {

            Response<RES> response = invocation.execute();

            if (!response.isSuccessful()) {

                try (var responseBody = response.errorBody()) {

                    if (responseBody != null) {

                        var errorResponseBody = responseBody.string();

                        if (errorDecoder != null) {

                            try {

                                E decodedError = errorDecoder.decode(response.code(), errorResponseBody);
                                LOGGER.error("Decoded error response : {}", decodedError);
                                throw new InvocationException(response.code(), decodedError, errorResponseBody);

                            } catch (Exception ignored) {

                                LOGGER.error("Error decoding error response : {}", errorResponseBody);
                            }
                        }

                        LOGGER.error("Error response : {}", responseBody.string());
                        throw new InvocationException(response.code(), null, errorResponseBody);
                    }

                    LOGGER.error("Error response has no body.");
                    throw new InvocationException(response.code(), null, null);
                }
            }

            return response;

        } catch (IOException e) {

            if (e instanceof InvocationException) {

                throw (InvocationException) e;

            } else {

                throw new InvocationException(e);

            }

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
        Call<Void> get(@Url String url, @HeaderMap Map<String, String> headers, @QueryMap Map<String, String> params);

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

        private Builder(Class<S> service, String baseUrl) {

            this.service = service;
            this.retrofitBuilder.baseUrl(baseUrl.endsWith("/") ? baseUrl : baseUrl + "/");
        }

        public S build() {

            if (this.loggingInterceptor != null) {
                this.httpClientBuilder.addInterceptor(this.loggingInterceptor);
            }

            Retrofit retrofit = this.retrofitBuilder.client(this.httpClientBuilder.build()).build();

            return retrofit.create(this.service);
        }

        public Builder<S> withConverterFactories(Converter.Factory... factories) {

            if (factories != null) {

                for (Converter.Factory factory : factories) {

                    this.retrofitBuilder.addConverterFactory(factory);
                }
            }

            return this;
        }

        public Builder<S> withCustomHeaders(Map<String, String> headers) {

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

            this.retrofitBuilder.addConverterFactory(new NullOrEmptyConverterFactory());
            this.retrofitBuilder.addConverterFactory(ScalarsConverterFactory.create());
            this.retrofitBuilder.addConverterFactory(JacksonConverterFactory.create(objectMapper));

            return this;
        }

        public Builder<S> withDisableSSLVerification() {

            final TrustManager[] trustManager = new TrustManager[]{
                new X509TrustManager() {

                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                        throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                        throws CertificateException {

                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {

                        return new java.security.cert.X509Certificate[]{};

                    }

                }};

            final SSLContext sslContext;

            try {

                sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustManager, new java.security.SecureRandom());

                final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                this.httpClientBuilder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustManager[0]);
                this.httpClientBuilder.hostnameVerifier(new HostnameVerifier() {

                    @Override
                    public boolean verify(String hostname, SSLSession session) {

                        return true;

                    }

                });

            } catch (KeyManagementException | NoSuchAlgorithmException e) {

                throw new RuntimeException(e);

            }

            return this;
        }

        public Builder<S> withHttpLogging(HttpLoggingInterceptor.Level level, boolean info) {

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

            if (interceptors != null) {

                for (Interceptor interceptor : interceptors) {

                    this.httpClientBuilder.addInterceptor(interceptor);
                }
            }

            return this;
        }

        public Builder<S> withMutualTLS(InputStream clientCertInputStream,
                                        String clientCertPassword,
                                        InputStream trustStoreInputStream,
                                        String trustStorePassword) {

            try {
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                keyStore.load(clientCertInputStream, clientCertPassword.toCharArray());

                KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                kmf.init(keyStore, clientCertPassword.toCharArray());

                KeyStore trustStore = KeyStore.getInstance("PKCS12");
                trustStore.load(trustStoreInputStream, trustStorePassword.toCharArray());

                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(trustStore);
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

                SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
                X509TrustManager trustManager = (X509TrustManager) tmf.getTrustManagers()[0];

                this.httpClientBuilder.sslSocketFactory(sslSocketFactory, trustManager);

            } catch (Exception e) {
                throw new RuntimeException("Failed to configure mTLS", e);
            }

            return this;
        }

        public Builder<S> withTimeouts(int connectTimeout, int callTimeout, int readTimeout) {

            this.httpClientBuilder.connectTimeout(Duration.ofSeconds(connectTimeout <= 0 ? 60 : connectTimeout));
            this.httpClientBuilder.callTimeout(Duration.ofSeconds(callTimeout <= 0 ? 60 : callTimeout));
            this.httpClientBuilder.readTimeout(Duration.ofSeconds(readTimeout <= 0 ? 60 : readTimeout));

            return this;
        }

    }

    @Getter
    public static class InvocationException extends IOException {

        private final int responseStatusCode;

        private final Object decodedErrorResponse;

        private final String originalErrorMessage;

        public InvocationException(int responseStatusCode, Object decodedErrorResponse, String originalErrorMessage) {

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
