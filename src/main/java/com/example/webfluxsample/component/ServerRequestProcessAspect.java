package com.example.webfluxsample.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
@ConditionalOnProperty(name = "useAspect", havingValue = "true")
@RequiredArgsConstructor
public class ServerRequestProcessAspect {
    private final MethodArgumentsValidatorProcessor argumentsValidatorProcessor;

    @Around("@annotation(com.example.webfluxsample.config.ValidCheck)")
    public Object replaceServerRequest(ProceedingJoinPoint pjp) throws Throwable {
        log.info("Using aspect for validating");
        return pjp.proceed(argumentsValidatorProcessor.process(pjp.getArgs()));
    }

}
