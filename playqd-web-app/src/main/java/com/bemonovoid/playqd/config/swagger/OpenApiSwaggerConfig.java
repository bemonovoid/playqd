package com.bemonovoid.playqd.config.swagger;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OpenApiSwaggerConfig {

    @Bean
    OpenAPI openAPI() {

        SecurityScheme basicAuthScheme = new SecurityScheme()
                .name("basic")
                .scheme("basic")
                .in(SecurityScheme.In.HEADER)
                .type(SecurityScheme.Type.HTTP);

        SecurityScheme jwtAuthScheme = new SecurityScheme()
                .name("bearer")
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .type(SecurityScheme.Type.HTTP);

        Map<String, SecurityScheme> securitySchemas = new LinkedHashMap<>();
        securitySchemas.put("basicScheme", basicAuthScheme);
        securitySchemas.put("jwtScheme", jwtAuthScheme);

        List<SecurityRequirement> securityRequirements = List.of(
                new SecurityRequirement().addList("basicScheme"),
                new SecurityRequirement().addList("jwtScheme"));

        return new OpenAPI()
                .components(new Components().securitySchemes(securitySchemas))
                .security(securityRequirements)
                .info(new Info().title("Playqd API").version("1.0.0")
                .license(new License().url("http://playqd.org")));
    }

}
