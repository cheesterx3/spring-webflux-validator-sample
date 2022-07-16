package com.example.webfluxsample.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.support.ServerRequestWrapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Validator;
import java.util.Map;

import static com.example.webfluxsample.helper.ValidationHelper.*;

@Slf4j
class ValidatedServerRequest extends ServerRequestWrapper {
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
        return bodyFromExtractor(super.body(extractor), validator);
    }

    @Override
    public <T> T body(BodyExtractor<T, ? super ServerHttpRequest> extractor, Map<String, Object> hints) {
        return bodyFromExtractor(super.body(extractor, hints), validator);
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
