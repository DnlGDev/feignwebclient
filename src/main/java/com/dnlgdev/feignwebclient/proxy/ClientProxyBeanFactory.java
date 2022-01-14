package com.dnlgdev.feignwebclient.proxy;

import com.dnlgdev.feignwebclient.InvocationHandler;
import com.dnlgdev.feignwebclient.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;

/*
 * @author dglod
 */
@Component(ClientProxyBeanFactory.CLIENT_PROXY_BEAN_FACTORY)
public class ClientProxyBeanFactory {

    public static final String CLIENT_PROXY_BEAN_FACTORY = "clientProxyBeanFactory";

    @Autowired
    private InvocationHandler clientInvocationHandler;

    @SuppressWarnings("unchecked")
    public <T> T createBean(ClassLoader classLoader, Class<T> clazz, String name) {
        Log.log("ClientProxyBeanFactory.createBean [Name: %s]", name);
        return (T) Proxy.newProxyInstance(classLoader, new Class[]{clazz}, clientInvocationHandler);
    }

}