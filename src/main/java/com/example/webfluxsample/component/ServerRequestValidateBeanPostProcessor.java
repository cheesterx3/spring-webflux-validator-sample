package com.example.webfluxsample.component;

import com.example.webfluxsample.config.ValidCheck;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import javax.validation.Validator;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

@Component
@Slf4j
@ConditionalOnProperty(name = "useAspect", havingValue = "false", matchIfMissing = true)
@RequiredArgsConstructor
public class ServerRequestValidateBeanPostProcessor implements BeanPostProcessor {
    private final Map<String, Class<?>> annotatedBeans = new HashMap<>();
    private final Validator validator;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        final Class<?> aClass = bean.getClass();
        final Method[] declaredMethods = aClass.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.isAnnotationPresent(ValidCheck.class)) {
                annotatedBeans.put(beanName, aClass);
                break;
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        final Class<?> classInfo = annotatedBeans.get(beanName);
        if (classInfo != null) {
            return Proxy.newProxyInstance(classInfo.getClassLoader(), classInfo.getInterfaces(), (proxy, method, args) -> {
                if (method.isAnnotationPresent(ValidCheck.class)) {
                    int index = findServerRequestArg(args);
                    if (index != -1) {
                        args[index] = new ValidatedServerRequest((ServerRequest) args[index], validator);
                    }
                    log.info("Using post processor for validating");
                }
                return method.invoke(bean, args);
            });
        }
        return bean;
    }

    private static int findServerRequestArg(Object[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof ServerRequest) return i;
        }
        return -1;
    }

}
