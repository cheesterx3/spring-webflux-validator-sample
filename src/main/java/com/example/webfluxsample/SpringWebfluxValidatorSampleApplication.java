package com.example.webfluxsample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class SpringWebfluxValidatorSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringWebfluxValidatorSampleApplication.class, args);
    }

}
