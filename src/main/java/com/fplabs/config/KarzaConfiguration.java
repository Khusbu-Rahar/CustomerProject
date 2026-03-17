package com.fplabs.config;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.client.HttpClientConfiguration;
import io.micronaut.runtime.ApplicationConfiguration;
import java.net.URI;
import java.time.Duration;


@ConfigurationProperties(KarzaConfiguration.PREFIX)
public class KarzaConfiguration extends HttpClientConfiguration{



    public static final String PREFIX = "karza.api";
    private final ConnectionPoolConfiguration connectionPoolConfiguration;
    private final Http2ClientConfiguration http2Configuration;

    @Property(name = "karza.api.key")
    private String key;

    @Property(name = "karza.api.base-url")
    private URI baseUrl;

    public KarzaConfiguration(
            ApplicationConfiguration applicationConfiguration,
            ConnectionPoolConfiguration connectionPoolConfiguration) {

        super(applicationConfiguration);
        this.connectionPoolConfiguration = connectionPoolConfiguration;
        this.http2Configuration = new Http2ClientConfiguration();


        setReadTimeout(Duration.ofSeconds(30));


    }


    @Override
    public ConnectionPoolConfiguration getConnectionPoolConfiguration() {
        return connectionPoolConfiguration;
    }
    @Override
    public Http2ClientConfiguration getHttp2Configuration() {
        return http2Configuration;
    }




    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public URI getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(URI baseUrl) {
        this.baseUrl = baseUrl;
    }
}
