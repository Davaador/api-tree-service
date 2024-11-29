package com.api.family.apitreeservice.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {
    private final BuildProperties buildProperties;

    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    @Bean
    OpenAPI customOpenAPI() {
        Contact contact = new Contact();
        contact.setEmail("davaadorj.0510@gmail.com");
        contact.url("https://github.com/davaadorj/apitreeservice");
        contact.setName("davaadorj.ny");
        return new OpenAPI().addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", createSecurityScheme()))
                .info(new Info().title("ApiTreeService API").description(buildProperties.getArtifact().concat("ApiTreeService API"))
                        .version(buildProperties.getVersion()).license(new License().name("License of Api")).contact(contact));
    }
}
