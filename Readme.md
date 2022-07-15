## spring-webflux-validator-sample
Project which demonstrates a few ways for validating requests for `HandlerFunctions` in `Spring WebFlux`.

It includes:
* a way using `AspectJ` - it may be enabled in `application.yml` through setting ``useAspect: true``
* a way using `BeanPostProcessor`, but `HandlerFunction` must implement an interface