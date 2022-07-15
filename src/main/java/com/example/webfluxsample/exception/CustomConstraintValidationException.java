package com.example.webfluxsample.exception;

import javax.validation.ConstraintViolationException;
import java.util.*;

public class CustomConstraintValidationException extends RuntimeException {
    private final List<ConstraintViolationException> exceptions = new ArrayList<>();
    private final boolean multiple;

    public CustomConstraintValidationException(String message, Collection<ConstraintViolationException> exceptions, boolean multiple) {
        super(message);
        Objects.requireNonNull(exceptions, "Exceptions cannot be null");
        this.exceptions.addAll(exceptions);
        this.multiple = multiple || exceptions.size() > 1;
    }

    public CustomConstraintValidationException(Collection<ConstraintViolationException> exceptions) {
        this(null, exceptions, true);
    }

    public CustomConstraintValidationException(ConstraintViolationException nestedException) {
        this(null, List.of(nestedException), false);
    }

    public Collection<ConstraintViolationException> getExceptions() {
        return Collections.unmodifiableList(exceptions);
    }

    public boolean isMultiple() {
        return multiple;
    }
}
