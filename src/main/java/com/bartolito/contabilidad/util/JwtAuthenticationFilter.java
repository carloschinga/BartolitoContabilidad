package com.bartolito.contabilidad.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter  extends OncePerRequestFilter {
    private final Util util;

    @Autowired
    public JwtAuthenticationFilter(Util util) {
        this.util = util;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");



        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.getWriter().write("Token no proporcionado");
            return;
        }

        String token = authHeader.substring(7); // quitar "Bearer "
        if (!util.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
            response.getWriter().write("Token inválido o expirado");
            return;
        }

        // Extraer username y setear Authentication
        String username = util.extractUsername(token);
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

}
