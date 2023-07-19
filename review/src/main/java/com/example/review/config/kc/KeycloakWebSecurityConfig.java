package com.example.review.config.kc;

import com.example.common.util.KCRoleConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
@Profile("kc")
public class KeycloakWebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KCRoleConverter());

        // https://stackoverflow.com/a/74633151
        http.authorizeRequests()
                .mvcMatchers(HttpMethod.GET).permitAll()
                .mvcMatchers(HttpMethod.POST, "/api/v1/reviews/user/reviewCountActive").permitAll()
                .mvcMatchers(HttpMethod.POST).hasRole("ADMIN")
                .mvcMatchers(HttpMethod.PUT).hasAnyRole("USER", "ADMIN", "MODERATOR")
                .mvcMatchers(HttpMethod.DELETE).hasAnyRole("USER", "ADMIN")

                .and().csrf().disable()

                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(jwtAuthenticationConverter);

        return http.build();
    }
}