package com.dnlgdev.feignwebclient;

import com.dnlgdev.feignwebclient.proxy.FeignImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(FeignImportBeanDefinitionRegistrar.class)
public @interface EnableCustomClient {

    String[] basePackages() default {"com"};
}