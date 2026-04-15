package com.marten.loanprocessservice.common;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI loanProcessServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Loan Process Service API")
                        .version("v1")
                        .description("API for submitting loan applications, reviewing statuses, and making approval decisions.")
                        .contact(new Contact().name(""))
                        .license(new License().name("")))
                .servers(List.of(new Server().url("/").description("Default server")));
    }
}
