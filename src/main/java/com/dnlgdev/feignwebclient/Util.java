package com.dnlgdev.feignwebclient;

/*
 * @author dglod
 */
public class Util {

    public static String getFeignClientName(String clientName) {
        return String.format("%s_feignclient", clientName);
    }

    public static String getWebClientName(String clientName) {
        return String.format("%s_webclient", clientName);
    }
}
