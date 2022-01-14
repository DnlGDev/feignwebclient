package com.dnlgdev.feignwebclient.configuration;

import reactor.netty.http.client.HttpClient;

import java.util.Map;

/*
 * @author dglod
 */
public class FeignConfiguration {

    private String name;

    private String baseUrl;

    private Map<String, String> defaultHeaders;

    private HttpClient defaultHttpClient;

    private FeignConfiguration() {

    }

    private FeignConfiguration(String name) {
        this.name = name;
    }

    public static Builder builder(String name) {
        var configuration = new FeignConfiguration(name);
        return new Builder(configuration);
    }

    public static Builder builder() {
        var configuration = new FeignConfiguration();
        return new Builder(configuration);
    }

    public static class Builder {

        FeignConfiguration configuration;

        public Builder(FeignConfiguration configuration) {
            this.configuration = configuration;
        }

        public FeignConfiguration build() {
            return configuration;
        }

        public Builder baseUrl(String baseUrl) {
            configuration.setBaseUrl(baseUrl);
            return this;
        }

        public Builder defaultHeaders(Map<String, String> headers) {
            configuration.setDefaultHeaders(headers);
            return this;
        }

        public Builder defaultHttpClient(HttpClient httpClient) {
            configuration.setDefaultHttpClient(httpClient);
            return this;
        }

    }

    public String getName() {
        return name;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    private void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Map<String, String> getDefaultHeaders() {
        return defaultHeaders;
    }

    private void setDefaultHeaders(Map<String, String> defaultHeaders) {
        this.defaultHeaders = defaultHeaders;
    }

    public HttpClient getDefaultHttpClient() {
        return defaultHttpClient;
    }

    private void setDefaultHttpClient(HttpClient defaultHttpClient) {
        this.defaultHttpClient = defaultHttpClient;
    }
}
