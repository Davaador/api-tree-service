package com.api.family.apitreeservice.configuration;

import com.api.family.apitreeservice.service.CustomerUserDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomerUserDetailService userDetailService;
    private final SecurityProperties securityProperties;
    private final RateLimitFilter rateLimitFilter;

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.cors(cors -> cors.configurationSource(corsConfigurationSource())).authorizeHttpRequests(
                requests -> requests.requestMatchers(HttpMethod.OPTIONS).permitAll()
                        .requestMatchers("/health/**", "/metrics", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/auth/token", "/auth/token", "/auth/refresh", "/auth/user/register",
                                "/auth/sent/otp",
                                "/auth/check/otp",
                                "/auth/reset/password")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        var configuration = new CorsConfiguration();

        // Use configured origins or default to localhost for development
        if (securityProperties.getCors().getAllowedOrigins() != null) {
            configuration.setAllowedOrigins(securityProperties.getCors().getAllowedOrigins());
        } else {
            configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:3001"));
        }

        if (securityProperties.getCors().getAllowedMethods() != null) {
            configuration.setAllowedMethods(securityProperties.getCors().getAllowedMethods());
        } else {
            configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        }

        if (securityProperties.getCors().getAllowedHeaders() != null) {
            configuration.setAllowedHeaders(securityProperties.getCors().getAllowedHeaders());
        } else {
            configuration.setAllowedHeaders(List.of("*"));
        }

        configuration.setAllowCredentials(securityProperties.getCors().isAllowCredentials());
        configuration.setMaxAge(
                securityProperties.getCors().getMaxAge() > 0 ? securityProperties.getCors().getMaxAge() : 3600L);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        log.info("CORS configuration loaded: origins={}, methods={}, allowCredentials={}",
                configuration.getAllowedOrigins(),
                configuration.getAllowedMethods(),
                configuration.getAllowCredentials());

        return source;
    }
}
