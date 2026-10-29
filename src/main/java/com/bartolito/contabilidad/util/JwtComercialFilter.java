package com.bartolito.contabilidad.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class JwtComercialFilter extends OncePerRequestFilter{

    private final JwtUtil jwtUtil;

    @Autowired
    public JwtComercialFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1) Preflight CORS: dejar pasar sin validar token
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2) Sin Authorization: que Spring Security decida (permitAll o authenticated)
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        // 3) Validar firma con cualquiera de las dos claves
        if (!jwtUtil.validateToken(token)) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "invalid_token", "Token inválido o expirado");
            return;
        }

        // 4) Extraer username normalizado
        String username = jwtUtil.extractUsername(token);
        if (username == null) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "invalid_token", "No se pudo extraer el usuario");
            return;
        }

        // 5) Autenticar (authorities vacías por ahora; ver nota abajo)
        List<GrantedAuthority> authorities = List.of();
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    private void writeError(HttpServletResponse response, int status,
                            String code, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(
                String.format("{\"error\":\"%s\",\"message\":\"%s\"}", code, message));
    }
}
