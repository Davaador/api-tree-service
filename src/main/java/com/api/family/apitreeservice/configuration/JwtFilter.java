package com.api.family.apitreeservice.configuration;

import com.api.family.apitreeservice.exception.CustomException;
import com.api.family.apitreeservice.exception.Errors;
import com.api.family.apitreeservice.repository.UserRepository;
import com.api.family.apitreeservice.service.CustomerUserDetailService;
import com.api.family.apitreeservice.service.JwtUtility;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtUtility jwtUtility;
    private final UserRepository userRepository;

    public JwtFilter(HandlerExceptionResolver handlerExceptionResolver, JwtUtility jwtUtility,
            UserRepository userRepository) {
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.jwtUtility = jwtUtility;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (SecurityContextHolder.getContext().getAuthentication() == null && authHeader != null
                    && authHeader.startsWith("Bearer ")) {
                var token = authHeader.substring(7);
                var username = jwtUtility.extractUsername(token);
                UserDetails userDetails = new CustomerUserDetailService(userRepository).loadUserByUsername(username);
                if (jwtUtility.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            filterChain.doFilter(request, response);
        } catch (MalformedJwtException e) {

            handlerExceptionResolver.resolveException(request, response, null,
                    new CustomException(Errors.ACCESS_DENIED, e));
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
        }

    }
}
