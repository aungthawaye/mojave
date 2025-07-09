package io.mojaloop.common.component.retrofit;

import io.mojaloop.common.component.retrofit.converter.NullOrEmptyConverterFactory;
import lombok.Getter;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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
import retrofit2.http.HeaderMap;
import retrofit2.http.Url;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.time.Duration;
import java.util.Map;

/**
 * A utility class for working with Retrofit services.
 * Provides methods for building, configuring, and invoking Retrofit service calls.
 * Supports features like custom headers, SSL configuration, mTLS, and error handling.
 */
public class RetrofitService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetrofitService.class);

    /**
     * Forwards an HTTP request through a ForwardingService.
     *
     * @param <RES> The type of the expected response
     * @param <E> The type of the error decoder
     * @param forwardingService The service to use for forwarding the request
     * @param url The URL to forward the request to
     * @param headers The HTTP headers to include in the request
     * @param body The request body
     * @param errorDecoder The decoder to use for error responses
     * @return The response from the forwarded request
     * @throws InvocationException If the request fails or returns an error response
     */
    public static <RES, E> Response<RES> forward(ForwardingService forwardingService,
                                                 String url,
                                                 Map<String, String> headers,
                                                 RequestBody body,
                                                 RetrofitService.ErrorDecoder<E> errorDecoder) throws InvocationException {

        return RetrofitService.invoke(forwardingService.forward(url, headers, body), errorDecoder);
    }

    /**
     * Executes a Retrofit call and handles error responses.
     *
     * @param <RES> The type of the expected response
     * @param <E> The type of the error decoder
     * @param invocation The Retrofit call to execute
     * @param errorDecoder The decoder to use for error responses (can be null)
     * @return The successful response from the call
     * @throws InvocationException If the call fails or returns an error response
     */
    public static <RES, E> Response<RES> invoke(Call<RES> invocation, ErrorDecoder<E> errorDecoder) throws InvocationException {

        try {

            Response<RES> response = invocation.execute();

            if (!response.isSuccessful()) {

                if (errorDecoder != null) {

                    throw new InvocationException(errorDecoder.decode(response.code(), response.errorBody()));

                } else {

                    try (var responseBody = response.errorBody()) {

                        if (responseBody != null) {
                            LOGGER.error("Error response : {}", responseBody.string());
                            throw new InvocationException(responseBody.string());
                        }
                    }
                }
            }

            return response;

        } catch (Exception e) {

            if (e instanceof InvocationException) {

                throw (InvocationException) e;

            } else {

                throw new InvocationException(e);

            }

        }
    }

    /**
     * Creates a new Builder for configuring a Retrofit service.
     *
     * @param <S> The type of the service interface
     * @param service The service interface class
     * @param baseUrl The base URL for the service
     * @return A new Builder instance
     */
    public static <S> Builder<S> newBuilder(Class<S> service, String baseUrl) {

        return new Builder<>(service, baseUrl);
    }

    /**
     * Interface for services that can forward HTTP requests.
     * Used in conjunction with the {@link #forward} method.
     */
    public interface ForwardingService {

        /**
         * Forwards an HTTP request to the specified URL with the given headers and body.
         *
         * @param <RES> The type of the expected response
         * @param <E> The type parameter for error handling
         * @param url The URL to forward the request to
         * @param headers The HTTP headers to include in the request
         * @param body The request body
         * @return A Retrofit Call object representing the forwarded request
         */
        <RES, E> Call<RES> forward(@Url String url, @HeaderMap Map<String, String> headers, @Body RequestBody body);

    }

    /**
     * Interface for decoding error responses from Retrofit calls.
     *
     * @param <E> The type of the decoded error
     */
    public interface ErrorDecoder<E> {

        /**
         * Decodes an error response.
         *
         * @param status The HTTP status code
         * @param errorResponseBody The error response body
         * @return The decoded error
         */
        E decode(int status, ResponseBody errorResponseBody);

    }

    /**
     * Builder class for configuring and creating Retrofit service instances.
     *
     * @param <S> The type of the service interface
     */
    public static class Builder<S> {

        private final Class<S> service;

        private final OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        private final Retrofit.Builder retrofitBuilder = new Retrofit.Builder();

        /**
         * Creates a new Builder with the specified service interface and base URL.
         *
         * @param service The service interface class
         * @param baseUrl The base URL for the service
         */
        private Builder(Class<S> service, String baseUrl) {

            this.service = service;
            this.retrofitBuilder.baseUrl(baseUrl);
        }

        /**
         * Builds and creates the service instance with the configured settings.
         *
         * @return The configured service instance
         */
        public S build() {

            Retrofit retrofit = this.retrofitBuilder.client(this.httpClientBuilder.build()).build();

            return retrofit.create(this.service);
        }

        /**
         * Adds converter factories to the Retrofit builder.
         *
         * @param factories The converter factories to add
         * @return This Builder instance for method chaining
         */
        public Builder<S> withConverterFactories(Converter.Factory... factories) {

            if (factories != null) {

                for (Converter.Factory factory : factories) {

                    this.retrofitBuilder.addConverterFactory(factory);
                }
            }

            return this;
        }

        /**
         * Adds custom headers to all requests made by this service.
         *
         * @param headers A map of header names to header values
         * @return This Builder instance for method chaining
         */
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

        /**
         * Adds default converter factories to the Retrofit builder.
         * This includes NullOrEmptyConverterFactory, ScalarsConverterFactory, and JacksonConverterFactory.
         *
         * @return This Builder instance for method chaining
         */
        public Builder<S> withDefaultFactories() {

            this.retrofitBuilder.addConverterFactory(new NullOrEmptyConverterFactory());
            this.retrofitBuilder.addConverterFactory(ScalarsConverterFactory.create());
            this.retrofitBuilder.addConverterFactory(JacksonConverterFactory.create());

            return this;
        }

        /**
         * Disables SSL certificate verification for this service.
         * WARNING: This should only be used for testing purposes as it makes the connection insecure.
         *
         * @return This Builder instance for method chaining
         * @throws RuntimeException If there is an error setting up the SSL context
         */
        public Builder<S> withDisableSSLVerification() {

            // Create a trust manager that does not validate certificate chains
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

            // Install the all-trusting trust manager
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

        /**
         * Adds HTTP request/response logging to this service.
         *
         * @param level The level of logging detail
         * @param info If true, logs at INFO level; otherwise, logs at DEBUG level
         * @return This Builder instance for method chaining
         */
        public Builder<S> withHttpLogging(HttpLoggingInterceptor.Level level, boolean info) {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> {
                if (info) {
                    LOGGER.info("Retrofit : {}", message);
                } else {
                    LOGGER.debug("Retrofit :{}", message);
                }
            });

            logging.setLevel(level);

            this.httpClientBuilder.addInterceptor(logging);

            return this;
        }

        /**
         * Adds custom interceptors to the HTTP client.
         *
         * @param interceptors The interceptors to add
         * @return This Builder instance for method chaining
         */
        public Builder<S> withInterceptors(Interceptor... interceptors) {

            if (interceptors != null) {

                for (Interceptor interceptor : interceptors) {

                    this.httpClientBuilder.addInterceptor(interceptor);
                }
            }

            return this;
        }

        /**
         * Configures mutual TLS (mTLS) authentication for this service.
         *
         * @param clientCertInputStream The input stream for the client certificate (PKCS12 format)
         * @param clientCertPassword The password for the client certificate
         * @param trustStoreInputStream The input stream for the trust store (JKS format)
         * @param trustStorePassword The password for the trust store
         * @return This Builder instance for method chaining
         * @throws RuntimeException If there is an error configuring mTLS
         */
        public Builder<S> withMtls(InputStream clientCertInputStream,
                                   String clientCertPassword,
                                   InputStream trustStoreInputStream,
                                   String trustStorePassword) {

            try {
                // Load client certificate
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                keyStore.load(clientCertInputStream, clientCertPassword.toCharArray());

                KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                kmf.init(keyStore, clientCertPassword.toCharArray());

                // Load trust store
                KeyStore trustStore = KeyStore.getInstance("JKS");
                trustStore.load(trustStoreInputStream, trustStorePassword.toCharArray());

                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(trustStore);

                // Setup SSL context
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

        /**
         * Sets the timeouts for HTTP connections.
         *
         * @param connectTimeout The connection timeout in seconds (default 60 if <= 0)
         * @param callTimeout The call timeout in seconds (default 60 if <= 0)
         * @param readTimeout The read timeout in seconds (default 60 if <= 0)
         * @return This Builder instance for method chaining
         */
        public Builder<S> withTimeouts(int connectTimeout, int callTimeout, int readTimeout) {

            this.httpClientBuilder.connectTimeout(Duration.ofSeconds(connectTimeout <= 0 ? 60 : connectTimeout));
            this.httpClientBuilder.callTimeout(Duration.ofSeconds(callTimeout <= 0 ? 60 : callTimeout));
            this.httpClientBuilder.readTimeout(Duration.ofSeconds(readTimeout <= 0 ? 60 : readTimeout));

            return this;
        }

    }

    /**
     * Exception thrown when a Retrofit service invocation fails.
     */
    @Getter
    public static class InvocationException extends Exception {

        private Object errorResponse;

        /**
         * Creates a new InvocationException with an error response.
         *
         * @param errorResponse The error response object
         */
        public InvocationException(Object errorResponse) {

            super();

            this.errorResponse = errorResponse;

        }

        /**
         * Creates a new InvocationException with a cause.
         *
         * @param cause The cause of the exception
         */
        public InvocationException(Exception cause) {

            super(cause);

        }

    }

}
