package com.example.webfluxsample.config;

import com.example.webfluxsample.exception.CustomConstraintValidationException;
import com.example.webfluxsample.handlers.ModelHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;


@Slf4j
@Configuration
public class RouterConfig {

    @Bean(name = "modelProcessRoute")
    public RouterFunction<ServerResponse> modelRoutes(ModelHandler modelHandler) {
        return route().path("/api/model", builder -> builder
                        .nest(RequestPredicates.accept(MediaType.APPLICATION_JSON), b -> b
                                .GET(modelHandler::getAll)
                                .POST(RequestPredicates.queryParam("all", s -> true), modelHandler::saveMany)
                                .POST(modelHandler::save))
                )
                .build();
    }

    @Bean
    @Order(-2)
    public WebExceptionHandler exceptionHandler(ErrorResolver errorResolver) {
        return (exchange, ex) -> {
            if (ex instanceof CustomConstraintValidationException e) {
                return errorResolver.resolve(exchange, ConstraintError.error(e), HttpStatus.BAD_REQUEST);
            }
            return Mono.error(ex);
        };
    }


    @Component
    @RequiredArgsConstructor
    static class ErrorResolver {
        private final DataBufferWriter bufferWriter;

        public <T> Mono<Void> resolve(ServerWebExchange exchange, T object, HttpStatus status) {
            final var httpResponse = exchange.getResponse();
            httpResponse.getHeaders().set(HttpHeaders.CONTENT_TYPE, "application/json; charset= utf-8");
            httpResponse.setStatusCode(status);
            return bufferWriter.write(httpResponse, object);
        }
    }

    @Component
    @Slf4j
    static class DataBufferWriter {
        private final ObjectMapper objectMapper;

        public DataBufferWriter(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        public <T> Mono<Void> write(ServerHttpResponse httpResponse, T object) {
            return httpResponse
                    .writeWith(Mono.fromSupplier(() -> {
                        final var bufferFactory = httpResponse.bufferFactory();
                        try {
                            return bufferFactory.wrap(objectMapper.writeValueAsBytes(object));
                        } catch (Exception ex) {
                            log.warn("Error writing response", ex);
                            return bufferFactory.wrap(new byte[0]);
                        }
                    }));
        }
    }
}
