package com.example.restaurant_advisor_react.filter;

import com.example.restaurant_advisor_react.servise.UserService;
import com.example.restaurant_advisor_react.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if(request.getCookies() == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<Cookie> jwtOpt = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("refresh-token"))
                .findAny();

        if (jwtOpt.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = jwtOpt.get().getValue();

        UserDetails userDetails = userService.loadUserByUsername(JwtUtil.getUserEmailFromToken(token));

        if (!JwtUtil.validateToken(token, userDetails)) {
            filterChain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null,
                userDetails.getAuthorities()
        );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}