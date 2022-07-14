package com.example.webfluxsample.domain;

import org.springframework.lang.NonNull;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public record ModelDto(@NonNull String id,
                       @NotBlank String name,
                       @Min(10) int size) {
}
