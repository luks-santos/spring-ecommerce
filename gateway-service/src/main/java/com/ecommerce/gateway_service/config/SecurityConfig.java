package com.ecommerce.gateway_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(ex -> ex
                // Public routes
                .pathMatchers(HttpMethod.POST, "/api/user/sign-in").permitAll()
                .pathMatchers(HttpMethod.POST, "/api/user/sign-up").permitAll()
                .pathMatchers(HttpMethod.POST, "/api/user/refresh-token").permitAll()
                .pathMatchers(HttpMethod.GET,  "/api/product-catalog/**").permitAll()
                .pathMatchers(HttpMethod.GET,  "/api/user/v3/api-docs").permitAll()
                .pathMatchers(HttpMethod.GET,  "/api/product-catalog/v3/api-docs").permitAll()
                // Admin-only writes on product catalog
                .pathMatchers(HttpMethod.POST, "/api/product-catalog/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.PUT,  "/api/product-catalog/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/api/product-catalog/**").hasRole("ADMIN")
                // Everything else requires authentication
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
            .build();
    }

    /**
     * Maps both the {@code scope} claim (to {@code SCOPE_*} authorities) and the
     * {@code roles} claim (to {@code ROLE_*} authorities) so {@code hasRole(...)} works.
     */
    private ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter scopesConverter = new JwtGrantedAuthoritiesConverter();
        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<GrantedAuthority> authorities = new ArrayList<>(scopesConverter.convert(jwt));
            List<String> roles = jwt.getClaimAsStringList("roles");
            if (roles != null) {
                roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
            }
            return Flux.fromIterable(authorities);
        });
        return converter;
    }
}
