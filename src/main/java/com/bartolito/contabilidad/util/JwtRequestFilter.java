package com.bartolito.contabilidad.util;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bartolito.contabilidad.repository.ConciliacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ConciliacionRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();

        if (request.getMethod().equalsIgnoreCase("OPTIONS")
                || path.endsWith("/auth/login")
                || path.endsWith("/auth/loginBartolito")
                || path.endsWith("/auth/loginByUsername")
                || path.endsWith("/auth/loginBartolitoByUsername")
                || path.endsWith("/auth/loginInventario")
                || path.endsWith("/auth/loginInventarioByUsername")
                || path.endsWith("/auth/getUser")
                || path.endsWith("/auth/getUserBartolito")
                || path.endsWith("/auth/getUserInventario")
                || path.endsWith("/menu/paginasxgrupo")) {
            filterChain.doFilter(request, response);
            return;
        }



        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.getWriter().write("Token no proporcionado");
            return;
        }

        String token = authHeader.substring(7); // quitar "Bearer "
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
            response.getWriter().write("Token inválido o expirado");
            return;
        }


        // Extraer username y setear Authentication
        String username = jwtUtil.extractUsername(token);
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}