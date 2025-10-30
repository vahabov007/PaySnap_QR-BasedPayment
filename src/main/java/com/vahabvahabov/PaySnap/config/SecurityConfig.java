package com.vahabvahabov.PaySnap.config;

import com.vahabvahabov.PaySnap.config.jwt.JwtRequestFilter;
import com.vahabvahabov.PaySnap.config.jwt.JwtUtil;
import com.vahabvahabov.PaySnap.model.User;
import com.vahabvahabov.PaySnap.repository.BlackListRepository;
import com.vahabvahabov.PaySnap.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.Optional;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Optional<User> optional = userRepository.findByUsername(username);
            if (optional.isEmpty()) {
                throw new UsernameNotFoundException("Username not found");
            }
            return optional.get();
        };
    }

    @Bean
    public JwtRequestFilter jwtRequestFilter(UserDetailsService userDetailsService,
                                             JwtUtil jwtUtil,
                                             BlackListRepository blackListRepository) {
        JwtRequestFilter jwtRequestFilter = new JwtRequestFilter();
        jwtRequestFilter.setUserDetailsService(userDetailsService);
        jwtRequestFilter.setJwtUtil(jwtUtil);
        jwtRequestFilter.setTokenBlackListRepository(blackListRepository);
        return jwtRequestFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/webjars/**",
                                "/ws/**",
                                "/api/auth/**",
                                "/api/register/**",
                                "/home",
                                "/api/payment/**",
                                "/api/webhook/**",
                                "/"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

}
