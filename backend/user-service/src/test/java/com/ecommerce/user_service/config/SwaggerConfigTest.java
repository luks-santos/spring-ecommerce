package com.ecommerce.user_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SwaggerConfigTest {

    @Test
    void customOpenAPI_ShouldExposeServiceInfoAndSecuritySchemes() {
        OpenAPI openAPI = new SwaggerConfig().customOpenAPI();

        assertThat(openAPI.getInfo().getTitle()).isEqualTo("User Service API");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0.0");
        assertThat(openAPI.getComponents().getSecuritySchemes())
                .containsKeys("bearer-key", "basic-auth");
    }
}
