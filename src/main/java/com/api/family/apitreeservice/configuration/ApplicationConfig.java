package com.api.family.apitreeservice.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;



@Configuration
@EnableJpaAuditing
public class ApplicationConfig {

    @Bean
    ModelMapper getModelMapper() {
        var mapper = new ModelMapper();
        mapper.getConfiguration().setFieldMatchingEnabled(Boolean.TRUE).setFieldAccessLevel(AccessLevel.PUBLIC);
        return mapper;
    }

    @Bean
    public ObjectMapper getObjectMapper() {
        Jackson2ObjectMapperBuilder.json()
                .featuresToDisable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);

        var objectMapper = new ObjectMapper();

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return objectMapper;
    }

    @Bean
    public WebClient getWebClient() {
        HttpClient httpClient = HttpClient.create().followRedirect(true);
        return WebClient.builder().defaultHeader("Content-Type", "application/json")
                .clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }
}
