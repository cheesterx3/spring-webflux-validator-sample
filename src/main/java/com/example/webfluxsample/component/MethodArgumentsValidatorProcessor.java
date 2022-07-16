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
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof ServerRequest e) args[i] = new ValidatedServerRequest(e, validator);
        }
        return args;
    }

}
