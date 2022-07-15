package com.example.webfluxsample.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.support.ServerRequestWrapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Validator;
import java.util.Map;

import static com.example.webfluxsample.helper.ValidationHelper.*;

@Aspect
@Component
@RequiredArgsConstructor
public class ServerRequestProcessAspect {
    private final Validator validator;

    @Around("@annotation(com.example.webfluxsample.config.ValidCheck)")
    public Object replaceServerRequest(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        final var index = findServerRequestArg(args);
        if (index != -1) {
            args[index] = new ValidatedServerRequest((ServerRequest) args[index], validator);
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

    @Slf4j
    private static class ValidatedServerRequest extends ServerRequestWrapper {
        private final Validator validator;

        /**
         * Create a new {@code ServerRequestWrapper} that wraps the given request.
         *
         * @param delegate  the request to wrap
         * @param validator validator
         */
        public ValidatedServerRequest(ServerRequest delegate, Validator validator) {
            super(delegate);
            this.validator = validator;
        }

        @Override
        public <T> Mono<T> bodyToMono(Class<? extends T> elementClass) {
            return super.bodyToMono(elementClass)
                    .flatMap(t -> validatedMono(t, validator));
        }

        @Override
        public <T> T body(BodyExtractor<T, ? super ServerHttpRequest> extractor) {
            return validated(super.body(extractor), validator);
        }

        @Override
        public <T> T body(BodyExtractor<T, ? super ServerHttpRequest> extractor, Map<String, Object> hints) {
            return validated(super.body(extractor, hints), validator);
        }

        @Override
        public <T> Mono<T> bodyToMono(ParameterizedTypeReference<T> typeReference) {
            return super.bodyToMono(typeReference)
                    .flatMap(t -> validatedMono(t, validator));
        }

        @Override
        public <T> Flux<T> bodyToFlux(Class<? extends T> elementClass) {
            return validatedFlux(super.bodyToFlux(elementClass), validator);
        }

        @Override
        public <T> Flux<T> bodyToFlux(ParameterizedTypeReference<T> typeReference) {
            return validatedFlux(super.bodyToFlux(typeReference), validator);
        }

    }
}
