package com.example.demo.configurations;

import com.example.demo.components.JwtTokenUtil;
import com.example.demo.filters.JwtTokenFilter;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(request -> {
                    request
                            .requestMatchers(
                                    "/api/v1/users/register",
                                    "/api/v1/users/login"
                            ).permitAll()
                            .requestMatchers(GET,"/api/v1/categories").permitAll()
                            .requestMatchers(POST,"/api/v1/categories").hasRole("ADMIN")
                            .requestMatchers(PUT,"/api/v1/categories/**").hasRole("ADMIN")
                            .requestMatchers(DELETE,"/api/v1/categories/**").hasRole("ADMIN")
                            .requestMatchers(GET,"/api/v1/products").permitAll()
                            .requestMatchers(POST,"/api/v1/products").hasRole("ADMIN")
                            .requestMatchers(PUT,"/api/v1/products/**").hasRole("ADMIN")
                            .requestMatchers(DELETE,"/api/v1/products/**").hasRole("ADMIN")
                            .requestMatchers(POST, "/api/v1/orders/**").hasRole("USER")
                            .requestMatchers(PUT, "/api/v1/orders/**").hasRole("ADMIN")
                            .requestMatchers(DELETE, "/api/v1/orders/**").hasRole("ADMIN")
                            .requestMatchers(GET, "/api/v1/orders/**").hasAnyRole("USER" ,"ADMIN")
                            .anyRequest()
                            .authenticated(); // Các yêu cầu khác sẽ cần xác thực
                })
                ;
        return http.build();
    }

}
