package com.example.webfluxsample.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import javax.validation.Validator;

@Aspect
@Slf4j
@Component
@ConditionalOnProperty(name = "useAspect", havingValue = "true")
@RequiredArgsConstructor
public class ServerRequestProcessAspect {
    private final Validator validator;

    @Around("@annotation(com.example.webfluxsample.config.ValidCheck)")
    public Object replaceServerRequest(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        final var index = findServerRequestArg(args);
        if (index != -1) {
            args[index] = new ValidatedServerRequest((ServerRequest) args[index], validator);
            log.info("Using aspect for validating");
            pjp.proceed(args);
        }
        return pjp.proceed();
    }

    private int findServerRequestArg(Object[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof ServerRequest) return i;
        }
        return -1;
    }

}
