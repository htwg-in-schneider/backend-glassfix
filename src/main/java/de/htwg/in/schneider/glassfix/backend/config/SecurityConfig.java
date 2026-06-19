package de.htwg.in.schneider.glassfix.backend.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;

/**
 * Configures our application with Spring Security to restrict access to our API
 * endpoints.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        
        return http
                .cors(withDefaults())
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/api/profile").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/anfrage", "/api/anfrage/*").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/anfrage/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/anfrage/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/anfrage", "/api/anfrage/*").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/auskunft/anfrage/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/auskunft/anfrage/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/auskunft", "/api/auskunft/anfrage/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/benutzer").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/benutzer/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/benutzer/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/benutzer", "/api/benutzer/*").authenticated()
                        .requestMatchers("/api/**").permitAll())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(withDefaults()))
                .build();
    }
}