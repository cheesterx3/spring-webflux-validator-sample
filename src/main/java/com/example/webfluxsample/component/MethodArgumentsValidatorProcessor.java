package com.example.webfluxsample.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import javax.validation.Validator;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class MethodArgumentsValidatorProcessor {
    private final Validator validator;

    public Object[] process(Object[] args) {
        Objects.requireNonNull(args, "Arguments cannot be null");
        final var index = findServerRequestArg(args);
        if (index != -1) {
            args[index] = new ValidatedServerRequest((ServerRequest) args[index], validator);
        }
        return args;
    }

    private int findServerRequestArg(Object[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof ServerRequest) return i;
        }
        return -1;
    }
}
