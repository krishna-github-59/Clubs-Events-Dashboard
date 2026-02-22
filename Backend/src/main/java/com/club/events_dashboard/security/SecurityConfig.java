package com.club.events_dashboard.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        var config = new org.springframework.web.cors.CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:3000", 
                "https://clubs-events-dashboard.vercel.app"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        var source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> {})
            .csrf(csrf -> csrf.disable())  // Disable CSRF for API testing in Postman
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers("/api/auth/register/student").permitAll()
                    .requestMatchers("/api/auth/register/club-admin").hasRole("SUPER_ADMIN")
                    .requestMatchers("/api/auth/login","/api/payments/**","/api/events/register/**").permitAll()
                    .requestMatchers(HttpMethod.GET,
                        "/api/events/**",
                        "/api/media/**",
                        "/api/clubs/all"
                    ).permitAll()
                    .requestMatchers("/api/clubs/**").hasRole("SUPER_ADMIN")
                    .requestMatchers("/api/events/delete/**", "/api/events/update/**", "/api/events/add", "/api/media/**").hasAnyRole("CLUB_ADMIN", "SUPER_ADMIN")
                    .requestMatchers("/api/users/email/**").authenticated()
                    .requestMatchers("/api/events/register").permitAll()
                    .anyRequest().authenticated()
            )
            .formLogin(form -> form.disable()) // Disable default login form
            .httpBasic(httpBasic -> httpBasic.disable()) // Disable basic auth
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
