package com.hediske.nutrition.config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class UserRoleAuthenticationFilter extends OncePerRequestFilter {

    private static final String USER_EMAIL_HEADER = "X-User-Email";
    private static final String USER_ROLE_HEADER = "X-User-Role";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String email = request.getHeader(USER_EMAIL_HEADER);
        String role = request.getHeader(USER_ROLE_HEADER);

        if (email != null && role != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // Convert role string to SimpleGrantedAuthority list
            // Suppose role can be comma separated for multiple roles
            List<SimpleGrantedAuthority> authorities = List.of(role.split(",")).stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.trim()))
                .toList();

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(email, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}
