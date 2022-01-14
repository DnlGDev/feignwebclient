package com.dnlgdev.feignwebclient.configuration;

import java.util.Map;

public interface DefaultHeaderSupplier {

    Map<String, String> supply();
}
