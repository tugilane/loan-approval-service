package com.marten.loanprocessservice.common;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Loan Process Service API",
                version = "v1",
                description = "API for submitting loan applications, reviewing statuses, and making approval decisions.",
                contact = @Contact(name = ""),
                license = @License(name = "")
        ),
        servers = {
                @Server(url = "/", description = "Default server")
        }
)
public class OpenApiConfiguration {
}
