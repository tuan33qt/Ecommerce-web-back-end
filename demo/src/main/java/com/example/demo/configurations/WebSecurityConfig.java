package com.example.demo.configurations;

import com.example.demo.components.JwtTokenUtil;
import com.example.demo.filters.JwtTokenFilter;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

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
                            .requestMatchers(GET,"/api/v1/users").permitAll()
                            .requestMatchers(GET,"/api/v1/users/**").permitAll()
                            .requestMatchers(PUT,"/api/v1/users/deactivate/**").hasRole("ADMIN")
                            .requestMatchers(PUT,"/api/v1/users/changepassword/**").hasRole("USER")
                            .requestMatchers(GET,"/api/v1/categories").permitAll()
                            .requestMatchers(GET,"/uploads?**").permitAll()
                            .requestMatchers(GET,"/api/v1/categories/**").permitAll()
                            .requestMatchers(POST,"/api/v1/categories").hasRole("ADMIN")
                            .requestMatchers(PUT,"/api/v1/categories/**").hasRole("ADMIN")
                            .requestMatchers(DELETE,"/api/v1/categories/**").hasRole("ADMIN")
                            .requestMatchers(GET,"/api/v1/products").permitAll()
                            .requestMatchers(GET,"/api/v1/products/**").permitAll()
                            .requestMatchers(GET,"/api/v1/products/images/**").permitAll()
                            .requestMatchers(POST,"/api/v1/products/uploads/**").permitAll()
                            .requestMatchers(GET, "/api/v1/products/images/**").permitAll()
                            .requestMatchers(GET, "/api/v1/products/image/**").permitAll()
                            .requestMatchers(POST,"/api/v1/products").hasRole("ADMIN")
                            .requestMatchers(PUT,"/api/v1/products/**").hasRole("ADMIN")
                            .requestMatchers(DELETE,"/api/v1/products/**").hasRole("ADMIN")
                            .requestMatchers(GET, "/api/v1/orders").permitAll()
                            .requestMatchers(POST, "/api/v1/orders").hasRole("USER")
                            .requestMatchers(PUT, "/api/v1/orders/**").hasRole("ADMIN")
                            .requestMatchers(DELETE, "/api/v1/orders/**").hasRole("ADMIN")
                            .requestMatchers(GET, "/api/v1/orders/**").permitAll()
                            .requestMatchers(GET, "api/v1/order_details/order/**").permitAll()
                            .requestMatchers(GET, "/api/v1/reviews").permitAll()
                            .requestMatchers(POST, "/api/v1/reviews").hasRole("USER")
                            .requestMatchers(PUT, "/api/v1/reviews/**").hasAnyRole("USER" ,"ADMIN")
                            .anyRequest()
                            .authenticated(); // Các yêu cầu khác sẽ cần xác thực
                })
                .csrf(AbstractHttpConfigurer::disable);
        http.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
            @Override
            public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
                CorsConfiguration configuration=new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("*"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("authorization", "content-Type", "x-auth-token", "Accept", "Origin"));
                configuration.setExposedHeaders(List.of("x-auth-token"));
                UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**",configuration);
                httpSecurityCorsConfigurer.configurationSource(source);
            }
        })
                ;
        return http.build();
    }

}
