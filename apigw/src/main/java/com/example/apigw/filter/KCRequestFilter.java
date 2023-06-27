package com.example.apigw.filter;

import com.example.apigw.service.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Profile("kc")
@AllArgsConstructor
public class KCRequestFilter implements GlobalFilter {

    private final TokenService tokenService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = new HttpHeaders();

        if (request.getCookies().containsKey("AT")) {
            tokenService.addBearerHeader(exchange, headers);
        } else if (request.getCookies().containsKey("RT")) {
            try {
                tokenService.refreshAccessTokenAndAddBearerHeader(exchange, headers);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return chain.filter(exchange);
    }
}