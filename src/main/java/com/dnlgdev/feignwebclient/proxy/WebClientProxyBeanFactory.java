package com.dnlgdev.feignwebclient.proxy;

import com.dnlgdev.feignwebclient.Log;
import com.dnlgdev.feignwebclient.configuration.FeignConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

/*
 * @author dglod
 */
@Component(WebClientProxyBeanFactory.WEB_CLIENT_PROXY_BEAN_FACTORY)
public class WebClientProxyBeanFactory {

    public static final String WEB_CLIENT_PROXY_BEAN_FACTORY = "webClientProxyBeanFactory";

    @Autowired
    private Map<String, FeignConfiguration> feignConfigurations;

    public WebClient createBean(String name) {

        Log.log("WebClientProxyBeanFactory.createBean [Name: %s]", name);

        var configuration = feignConfigurations.get(name);

        final WebClient.Builder builder = WebClient.builder();

        if (configuration != null) {

            // base url
            if (configuration.getBaseUrl() != null)
                builder.baseUrl(configuration.getBaseUrl());

            // headers
            if (configuration.getDefaultHeaders() != null)
                configuration.getDefaultHeaders().forEach(builder::defaultHeader);

            if (configuration.getDefaultHttpClient() != null)
                builder.clientConnector(new ReactorClientHttpConnector(configuration.getDefaultHttpClient()));
        }

        return builder.build();
    }

}