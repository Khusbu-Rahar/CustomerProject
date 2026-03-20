package com.fplabs.connector.client.karza;

import com.fplabs.exception.BadRequestException;
import com.fplabs.exception.ServiceNotAvailableException;
import com.fplabs.exception.UnauthorisedException;
import com.fplabs.connector.client.model.KarzaRequest;
import com.fplabs.connector.client.model.KarzaResponse;
import com.fplabs.config.KarzaConfiguration;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientException;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.rxjava3.http.client.Rx3HttpClient;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class KarzaClient {

    private static final Logger LOG = LoggerFactory.getLogger(KarzaClient.class);

    @Inject
    @Client(value = "${karza.api.base-url}", configuration = KarzaConfiguration.class)
    Rx3HttpClient client;

    @Value("${karza.api.key}")
    String karzaApiKey;

    @Value("${karza.api.pan-authentication-path:/v2/pan-authentication}")
    String panAuthenticationPath;

    @Value("${karza.api.api-key-header:x-karza-key}")
    String apiKeyHeader;

    @Value("${karza.api.content-type-header:Content-Type}")
    String contentTypeHeader;

    @Value("${karza.api.content-type-json:application/json}")
    String contentTypeJson;

    public Single<KarzaResponse> verifyPan(KarzaRequest karzaRequest) {
        LOG.info("Calling Karza API for PAN verification");

        HttpRequest<KarzaRequest> request = HttpRequest.POST(panAuthenticationPath, karzaRequest)
                .header(apiKeyHeader, karzaApiKey)
                .header(contentTypeHeader, contentTypeJson);

        return client.exchange(request, KarzaResponse.class)
                .flatMapSingle(response -> {
                    KarzaResponse body = response.body();
                    if (body == null) {
                        LOG.error("Karza API returned empty response");
                        return Single.error(new RuntimeException("Empty response from Karza API"));
                    }
                    LOG.info("Karza API call successful");
                    return Single.just(body);
                }).singleOrError()
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof HttpClientResponseException) {
                        HttpStatus status = ((HttpClientResponseException) throwable).getStatus();
                        LOG.error("HTTP Error: {}, Message: {}", status.getCode(), throwable.getMessage());
                        if (HttpStatus.BAD_REQUEST.equals(status)) {
                            return Single.error(new BadRequestException("Invalid request data"));
                        } else if (HttpStatus.UNAUTHORIZED.equals(status)) {
                            return Single.error(new UnauthorisedException("Invalid or expired Karza API key."));
                        }
                    } else if (throwable instanceof HttpClientException) {
                        LOG.error("Connection Error: {}", throwable.getMessage());
                        return Single.error(new ServiceNotAvailableException("Connection failed while calling Karza API service"));
                    }
                    LOG.error("Unexpected Error during Karza API call: {}", throwable.getMessage());
                    return Single.error(new RuntimeException("Error during Karza API call: " + throwable.getMessage(), throwable));

                });
    }
}

//package com.fplabs.connector.client.karza;


//
//import com.fplabs.exception.BadRequestException;
//import com.fplabs.exception.ServiceNotAvailableException;
//import com.fplabs.exception.UnauthorisedException;
//import com.fplabs.connector.client.model.KarzaRequest;
//import com.fplabs.connector.client.model.KarzaResponse;
//import com.fplabs.config.KarzaConfiguration;
//import io.micronaut.context.annotation.Value;
//import io.micronaut.http.HttpRequest;
//import io.micronaut.http.HttpResponse;
//import io.micronaut.http.HttpStatus;
//import io.micronaut.http.client.annotation.Client;
//import io.micronaut.http.client.exceptions.HttpClientException;
//import io.micronaut.http.client.exceptions.HttpClientResponseException;
//import io.micronaut.rxjava3.http.client.Rx3HttpClient;
//import io.reactivex.rxjava3.core.Single;
//import jakarta.inject.Inject;
//import jakarta.inject.Singleton;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//
//@Singleton
//public class KarzaClient {
//    private static final Logger LOG = LoggerFactory.getLogger(KarzaClient.class);
//
//    @Inject
//    @Client(value = "${karza.api.base-url}", configuration = KarzaConfiguration.class)
//    RxHttpClient client;
//
//    @Value("${karza.api.base-url}")
//    String KarzaBaseUrl;
//
//    @Value("${karza.api.key}")
//    String karzaApiKey;
//
//
//    public KarzaResponse verifyPan(KarzaRequest karzaRequest) {
//
//        LOG.info("Calling Karza API for PAN verification");
//        try {
//            HttpRequest<KarzaRequest> request = HttpRequest.POST("/v2/pan-authentication", karzaRequest)
//                    .header("x-karza-key", karzaApiKey)
//                    .header("Content-Type", "application/json");
//
//            HttpResponse<KarzaResponse> response = client.toblocking.exchange(request, KarzaResponse.class);
//
//            KarzaResponse result = response.body();
//            if (result == null) {
//                LOG.error("Karza API returned empty response");
//                throw new RuntimeException("Empty response from Karza API");
//            }else{LOG.info("Karza API call successful");
//            return result;
//            }
//
//        }catch (HttpClientResponseException e) {
//            HttpStatus statusCode = e.getStatus();
//            LOG.error("HTTP Error: {}, Message: {}", statusCode.getCode(), e.getMessage());
//
//            if (HttpStatus.BAD_REQUEST.equals(statusCode)) {
//                LOG.error("Bad Request - Invalid data");
//                throw new BadRequestException("Invalid request data");
//            } else if (HttpStatus.UNAUTHORIZED.equals(statusCode)) {
//                LOG.error("Unauthorized - Wrong API key or authentication failed");
//                throw new UnauthorisedException("Invalid or expired Karza API key.");
//            }else {
//                LOG.error("Unhandled HTTP status: {}", statusCode);
//                throw e;
//            }
//
//        } catch (HttpClientException e) {
//            LOG.error("Connection Error: {}", e.getMessage());
//            throw new ServiceNotAvailableException("Connection failed while calling Karza API service");
//        } catch (Exception e) {
//            LOG.error("Unexpected Error during Karza API call: {}", e.getMessage());
//            throw new RuntimeException("Error during Karza API call: " + e.getMessage(), e);
//        }
//
//    }
//
//}
//
//
//
//

