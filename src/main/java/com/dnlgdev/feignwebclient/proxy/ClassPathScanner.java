package com.dnlgdev.feignwebclient.proxy;

import com.dnlgdev.feignwebclient.CustomClient;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

/*
 * @author dglod
 */
public class ClassPathScanner extends ClassPathScanningCandidateComponentProvider {

    public ClassPathScanner() {
        super(false);
        addIncludeFilter(new AnnotationTypeFilter(CustomClient.class));
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface();
    }
}
