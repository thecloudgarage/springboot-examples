package com.mak.springbootefficientsearchapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

@EnableResourceServer
@SpringBootApplication
public class SpringBootEfficientSearchApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootEfficientSearchApiApplication.class, args);
    }
}
