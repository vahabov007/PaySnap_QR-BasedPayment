package com.vahabvahabov.PaySnap.config.jwt;

import com.vahabvahabov.PaySnap.repository.BlackListRepository;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Setter
@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    private JwtUtil jwtUtil;

    private UserDetailsService userDetailsService;

    private BlackListRepository tokenBlackListRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if(isPublicEndpoint(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }
        String authorizeHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        if (authorizeHeader != null && authorizeHeader.startsWith("Bearer ")) {
            jwt = authorizeHeader.substring(7);
            if (tokenBlackListRepository.isTokenBlacklisted(jwt)) {
                logger.warn("Blacklisted token attempted to access: {}", requestURI);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            try {
                username = jwtUtil.extractUsername(jwt);
            }catch (MalformedJwtException e) {
                logger.warn("Invalid JWT token format: {}", e.getMessage());
            }catch (Exception e) {
                logger.error("Error processing JWT token: {}", e.getMessage());
            }
        } else {
            logger.debug("No Authorization header or invalid format");
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null);
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    logger.debug("Successfully authenticated user: {}", username);
                }else {
                    logger.warn("JWT token validation failed for user: {}", username);
                }
            }catch (Exception e) {
                logger.error("Error loading user details for {}: {}", username, e.getMessage());
            }
        }

        filterChain.doFilter(request,response);

    }

    private boolean isPublicEndpoint(String requestURI) {
        return requestURI.startsWith("/api/auth/") ||
                requestURI.startsWith("/api/webhook/") ||
                requestURI.startsWith("/api/register") ||
                requestURI.equals("/") ||
                requestURI.startsWith("/css/") ||
                requestURI.startsWith("/js/") ||
                requestURI.startsWith("/images/") ||
                requestURI.startsWith("/webjars/") ||
                requestURI.endsWith(".css") ||
                requestURI.endsWith(".js") ||
                requestURI.endsWith(".ico");
    }
}
