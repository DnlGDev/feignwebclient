package com.dnlgdev.feignwebclient.configuration;

import reactor.netty.http.client.HttpClient;

public interface DefaultHttpClientSupplier {

    HttpClient supply();
}
