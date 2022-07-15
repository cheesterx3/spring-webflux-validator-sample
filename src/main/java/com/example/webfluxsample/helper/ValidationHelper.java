package com.example.webfluxsample.helper;

import com.example.webfluxsample.exception.CustomConstraintValidationException;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public final class ValidationHelper {
    private ValidationHelper() {
    }

    public static <T> T validated(T body, Validator validator) {
        if (body != null) {
            final var violations = validator.validate(body);
            if (!violations.isEmpty()) {
                throw new CustomConstraintValidationException(new ConstraintViolationException("", violations));
            }
        }
        return body;
    }

    public static <T> Mono<T> validatedMono(T t, Validator validator) {
        final var violations = validator.validate(t);
        return violations.isEmpty()
                ? Mono.just(t)
                : Mono.error(new CustomConstraintValidationException(new ConstraintViolationException("", violations)));
    }

    public static <T> Flux<T> validatedFlux(Flux<T> flux, Validator validator) {
        return flux.index()
                .flatMap(t -> validatedWrapper(validator, t.getT2(), t.getT1()))
                .collectMultimap(o -> o.exception() != null ? Boolean.FALSE : Boolean.TRUE)
                .flatMapMany(map -> {
                    final Collection<DataWrapper<T>> objects = map.get(Boolean.FALSE);
                    if (objects != null) {
                        final Set<ConstraintViolationException> exceptions = objects.stream()
                                .map(DataWrapper::exception)
                                .collect(Collectors.toSet());
                        return Flux.error(new CustomConstraintValidationException(exceptions));
                    }
                    return Flux.fromStream(map.getOrDefault(Boolean.TRUE, Collections.emptyList()).stream().map(DataWrapper::data));
                });
    }

    private static <T> Publisher<DataWrapper<T>> validatedWrapper(Validator validator, T body, long index) {
        if (body != null) {
            final var violations = validator.validate(body);
            if (!violations.isEmpty()) {
                return Mono.just(DataWrapper.failure(new ConstraintViolationException("Object [%d] in list violation.".formatted(index), violations), index));
            }
        }
        return Mono.just(DataWrapper.success(body, index));
    }

    private record DataWrapper<T>(T data, ConstraintViolationException exception, long index) {

        static <T> DataWrapper<T> success(T data, long index) {
            return new DataWrapper<>(data, null, index);
        }

        static <T> DataWrapper<T> failure(ConstraintViolationException exception, long index) {
            return new DataWrapper<>(null, exception, index);
        }
    }

}
