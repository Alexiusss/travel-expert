package com.example.user.config;

import com.example.common.util.KCRoleConverter;
import com.example.user.servise.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import javax.servlet.http.HttpServletResponse;

import static com.example.user.util.UserUtil.PASSWORD_ENCODER;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
   // private final JwtFilter jwtFilter;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
                .passwordEncoder(PASSWORD_ENCODER);
    }

    // https://www.toptal.com/spring/spring-security-tutorial
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KCRoleConverter());

        http = http.csrf().disable();
        // https://stackoverflow.com/a/55312420
        // .csrfTokenRepository(getCsrfRepository())
        //.ignoringAntMatchers("/api/v1/auth/**", "/api/v1/users/authorList").and();

        http = http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and();

        http = http
                .exceptionHandling()
                .authenticationEntryPoint((request, response, ex) -> {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
                }).and();

        http.authorizeRequests()
                .antMatchers("/api/v1/auth/**").permitAll()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers("/api/v1/users/**/author*").permitAll()
                .antMatchers("/**/swagger-ui/**", "/**/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()

                .and()

                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(jwtAuthenticationConverter);

//        http.addFilterBefore(
//                jwtFilter,
//                UsernamePasswordAuthenticationFilter.class
//        );
    }

    private static CookieCsrfTokenRepository getCsrfRepository() {
        CookieCsrfTokenRepository repository = new CookieCsrfTokenRepository();
        repository.setCookieHttpOnly(false);
        repository.setSecure(true);
        return repository;
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}