package com.zmc.ierbar_web_app.security;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7).trim();

            // 💡 VALIDARE ADĂUGATĂ: Ignoră string-urile "null", "undefined" sau malformate (fără structura standard header.payload.signature)
            if (!jwt.isEmpty() && !jwt.equalsIgnoreCase("null") && !jwt.equalsIgnoreCase("undefined") && jwt.split("\\.").length == 3) {
                try {
                    email = jwtUtil.extrageEmail(jwt);
                } catch (Exception e) {
                    System.out.println("Eroare la procesarea tokenului: " + e.getMessage());
                }
            }
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken tokenAutentificare = new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());

            tokenAutentificare.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(tokenAutentificare);
        }

        filterChain.doFilter(request, response);
    }
}