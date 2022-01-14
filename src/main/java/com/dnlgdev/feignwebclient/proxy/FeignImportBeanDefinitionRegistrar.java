package com.dnlgdev.feignwebclient.proxy;

import com.dnlgdev.feignwebclient.EnableCustomClient;
import com.dnlgdev.feignwebclient.Log;
import com.dnlgdev.feignwebclient.Util;
import com.dnlgdev.feignwebclient.CustomClient;
import lombok.SneakyThrows;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

/*
 * @author dglod
 */
public class FeignImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {


    private final ClassPathScanner scanner   = new ClassPathScanner();
    private final List<String>     beanNames = new ArrayList<>();

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        for (String basePackage : getBasePackages(annotationMetadata)) {
            createClientProxies(basePackage, registry);
        }
    }

    @SneakyThrows
    private void createClientProxies(String basePackage, BeanDefinitionRegistry registry) {

        for (BeanDefinition beanDefinition : scanner.findCandidateComponents(basePackage)) {

            Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());

            var clientAnnotation = clazz.getAnnotation(CustomClient.class);

            String name = clientAnnotation.name();

            Log.log("createClientProxies [Name: %s]", name);

            if (beanNames.contains(name))
                throw new IllegalArgumentException(String.format("Bean '%s' already exists", name));

            beanNames.add(name);

            var feignClientBeanName = Util.getFeignClientName(name);

            Log.log("createClientProxies [FeignClientBeanName: %s]", feignClientBeanName);

            createWebClientProxy(name, registry);

            GenericBeanDefinition proxyBeanDefinition = new GenericBeanDefinition();
            proxyBeanDefinition.setBeanClass(clazz);

            ConstructorArgumentValues args = new ConstructorArgumentValues();

            args.addGenericArgumentValue(clazz.getClassLoader());
            args.addGenericArgumentValue(clazz);
            args.addGenericArgumentValue(feignClientBeanName);
            proxyBeanDefinition.setConstructorArgumentValues(args);

            proxyBeanDefinition.setFactoryBeanName(ClientProxyBeanFactory.CLIENT_PROXY_BEAN_FACTORY);
            proxyBeanDefinition.setFactoryMethodName("createBean");

            registry.registerBeanDefinition(feignClientBeanName, proxyBeanDefinition);
        }
    }

    private void createWebClientProxy(String name, BeanDefinitionRegistry registry) {


        // Neu
        var webClientBeanName = Util.getWebClientName(name);

        Log.log("createWebClientProxy [WebClientBeanName: %s]", webClientBeanName);

        Class<WebClient> clazz = WebClient.class;
        GenericBeanDefinition proxyBeanDefinition = new GenericBeanDefinition();
        proxyBeanDefinition.setBeanClass(clazz);

        ConstructorArgumentValues args = new ConstructorArgumentValues();
        args.addGenericArgumentValue(name);
        proxyBeanDefinition.setConstructorArgumentValues(args);

        proxyBeanDefinition.setFactoryBeanName(WebClientProxyBeanFactory.WEB_CLIENT_PROXY_BEAN_FACTORY);
        proxyBeanDefinition.setFactoryMethodName("createBean");

        registry.registerBeanDefinition(webClientBeanName, proxyBeanDefinition);
    }


    private String[] getBasePackages(AnnotationMetadata annotationMetadata) {

        String[] basePackages = null;

        MultiValueMap<String, Object> allAnnotationAttributes =
                annotationMetadata.getAllAnnotationAttributes(EnableCustomClient.class.getName());

        if (!allAnnotationAttributes.isEmpty()) {
            basePackages = (String[]) allAnnotationAttributes.getFirst("basePackages");
        }

        return basePackages;
    }

    @Override
    public void setEnvironment(Environment environment) {

    }
}
