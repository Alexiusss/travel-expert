package com.example.apigw.filter;

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
public class KCRequestFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = new HttpHeaders();

        if (request.getCookies().containsKey("AT")) {
            String accessToken = request.getCookies().get("AT").get(0).getValue();
            headers.setBearerAuth(accessToken);
            exchange = exchange.mutate().request(r -> r.headers(httpHeaders -> httpHeaders.addAll(headers))).build();
        }

        return chain.filter(exchange);
    }
}