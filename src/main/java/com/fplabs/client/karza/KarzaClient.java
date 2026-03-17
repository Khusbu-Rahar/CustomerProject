package com.fplabs.client.karza;

import com.fplabs.Exception.BadRequestException;
import com.fplabs.Exception.ServiceNotAvailableException;
import com.fplabs.Exception.UnauthorisedException;
import com.fplabs.client.model.KarzaRequest;
import com.fplabs.client.model.KarzaResponse;
import com.fplabs.config.KarzaConfiguration;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientException;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class KarzaClient {
    private static final Logger LOG = LoggerFactory.getLogger(KarzaClient.class);

    @Inject
    @Client(value = "${karza.api.base-url}", configuration = KarzaConfiguration.class)
    private HttpClient client;

    @Value("${karza.api.base-url}")
    String KarzaBaseUrl;

    @Value("${karza.api.key}")
    String karzaApiKey;


    public KarzaResponse verifyPan(KarzaRequest karzaRequest) {

        LOG.info("Calling Karza API for PAN verification");
        try {
            HttpRequest<?> request = HttpRequest.POST("/v2/pan-authentication", karzaRequest)
                    .header("x-karza-key", karzaApiKey)
                    .header("Content-Type", "application/json");

            HttpResponse<KarzaResponse> response = client.toBlocking().exchange(request, KarzaResponse.class);

            KarzaResponse result = response.body();
            if (result == null) {
                LOG.error("Karza API returned empty response");
                throw new RuntimeException("Empty response from Karza API");
            }

            LOG.info("Karza API call successful");
            return result;

        }catch (HttpClientResponseException e) {
            HttpStatus statusCode = e.getStatus();
            LOG.error("HTTP Error: {}, Message: {}", statusCode.getCode(), e.getMessage());

            if (HttpStatus.BAD_REQUEST.equals(statusCode)) {
                LOG.error("Bad Request - Invalid data");
                throw new BadRequestException("Invalid request data");
            } else if (HttpStatus.UNAUTHORIZED.equals(statusCode)) {
                LOG.error("Unauthorized - Wrong API key or authentication failed");
                throw new UnauthorisedException("Invalid or expired Karza API key.");
            }else {
                LOG.error("Unhandled HTTP status: {}", statusCode);
                throw e;
            }

        } catch (HttpClientException e) {
            LOG.error("Connection Error: {}", e.getMessage());
            throw new ServiceNotAvailableException("Connection failed while calling Karza API service");
        } catch (Exception e) {
            LOG.error("Unexpected Error during Karza API call: {}", e.getMessage());
            throw new RuntimeException("Error during Karza API call: " + e.getMessage(), e);
        }

    }

}




