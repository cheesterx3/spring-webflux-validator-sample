package com.example.webfluxsample.config;

import com.example.webfluxsample.exception.CustomConstraintValidationException;
import lombok.Getter;
import org.springframework.util.ObjectUtils;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

sealed class ConstraintError {

    static ConstraintError error(CustomConstraintValidationException exception) {
        final var size = exception.getExceptions().size();
        if (size == 1 && !exception.isMultiple()) {
            final var violationException = exception.getExceptions().iterator().next();
            return new SimpleConstraintError(violationException);
        } else if (size == 0) {
            return new DummyError(exception);
        }
        return new ComplexConstraintError(exception);
    }

    @Getter
    private final static class DummyError extends ConstraintError {
        private final String error;

        private DummyError(CustomConstraintValidationException exception) {
            this.error = exception.getMessage();
        }
    }

    @Getter
    private final static class SimpleConstraintError extends ConstraintError {
        final List<String> errors;

        SimpleConstraintError(ConstraintViolationException exception) {
            errors = exception.getConstraintViolations()
                    .stream()
                    .map(violation -> "%s: %s".formatted(violation.getPropertyPath(), violation.getMessage()))
                    .toList();
        }
    }

    @Getter
    private final static class ComplexConstraintError extends ConstraintError {
        private final Map<String, List<String>> errors;

        ComplexConstraintError(CustomConstraintValidationException exception) {
            final var exceptions = exception.getExceptions();

            errors = exceptions.stream()
                    .collect(groupingBy(Throwable::getMessage, flatMapping(e -> e.getConstraintViolations()
                                    .stream()
                                    .map(violation -> "%s: %s".formatted(violation.getPropertyPath(), violation.getMessage()))
                            , toList())));

        }
    }
}
