package com.example.webfluxsample.handlers;

import com.example.webfluxsample.config.ValidCheck;
import com.example.webfluxsample.domain.ModelDto;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;


@Component
public class ModelHandlerImpl implements ModelHandler {
    private final Map<String, ModelDto> map = new ConcurrentHashMap<>();

    @Override
    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Flux.fromIterable(map.values()), ModelDto.class);
    }

    @Override
    @ValidCheck
    public Mono<ServerResponse> save(ServerRequest request) {
        final var modelDtoMono = request.body(BodyExtractors.toMono(ModelDto.class));

        return modelDtoMono
                .map(this::saveModel)
                .flatMap(modelDto -> ServerResponse.accepted().build());
    }

    @Override
    @ValidCheck
    public Mono<ServerResponse> saveMany(ServerRequest request) {
        final var flux = request.body(BodyExtractors.toFlux(ModelDto.class));
        return flux
                .map(this::saveModel)
                .then(ServerResponse.accepted().build());
    }

    private ModelDto saveModel(ModelDto modelDto) {
        map.put(modelDto.id(), modelDto);
        return modelDto;
    }

}
