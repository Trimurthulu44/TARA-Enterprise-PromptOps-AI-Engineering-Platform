package com.tara.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI taraOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("TARA – Enterprise PromptOps & AI Engineering Platform API")
                .description("Production-grade Prompt Lifecycle Management, Regression Testing, Security Guardrails, and LLM-as-a-Judge Evaluation APIs.")
                .version("1.0.0")
                .contact(new Contact()
                    .name("TARA Architecture Team")
                    .email("architecture@tara.ai")
                    .url("https://tara.ai"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0")))
            .servers(List.of(
                new Server().url("/").description("Current Server Gateway")
            ))
            .addSecurityItem(new SecurityRequirement()
                .addList("BearerAuth")
                .addList("ApiKeyAuth"))
            .components(new Components()
                .addSecuritySchemes("BearerAuth", new SecurityScheme()
                    .name("BearerAuth")
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Enter your JWT Access Token obtained from /api/v1/auth/login"))
                .addSecuritySchemes("ApiKeyAuth", new SecurityScheme()
                    .name("X-Tara-API-Key")
                    .type(SecurityScheme.Type.APIKEY)
                    .in(SecurityScheme.In.HEADER)
                    .description("Enter your enterprise API key (starts with tara_live_)")));
    }
}
