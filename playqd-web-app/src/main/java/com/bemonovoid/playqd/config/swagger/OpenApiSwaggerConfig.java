package com.bemonovoid.playqd.config.swagger;

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

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("basicScheme", basicAuthScheme))
                .addSecurityItem(new SecurityRequirement().addList("basicScheme"))
                .info(new Info().title("Playqd API").version("1.0.0")
                .license(new License().url("http://playqd.org")));
    }

}
