package com.example.webfluxsample.handlers;

import com.example.webfluxsample.config.ValidCheck;
import com.example.webfluxsample.domain.ModelDto;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface ModelHandler {
    Mono<ServerResponse> getAll(ServerRequest request);

    @ValidCheck
    Mono<ServerResponse> save(ServerRequest request);



    @ValidCheck
    Mono<ServerResponse> saveMany(ServerRequest request);
}
