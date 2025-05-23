package ru.flamexander.spring.security.jwt.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod; // <-- Добавлен импорт
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtRequestFilter jwtRequestFilter;

    @Autowired
    public SecurityConfig(@Lazy UserDetailsService userDetailsService, JwtRequestFilter jwtRequestFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and()
                .authorizeRequests()
                .antMatchers("/public/**").permitAll()
                .antMatchers("/api/auth/**").permitAll()
                .antMatchers("/api/services/**").permitAll()
                // .antMatchers("/api/service-orders/**").authenticated() // Закомментировано, если не используется
                .antMatchers("/api/users").hasRole("ADMIN") // Пример, если есть такой эндпоинт
                .antMatchers("/api/rooms/admin").permitAll() // Или hasRole("ADMIN") в зависимости от логики
                .antMatchers("/api/rooms/**").permitAll() // Для GET запросов к комнатам

                // Правила для Bookings
                .antMatchers("/api/bookings/add").authenticated()
                .antMatchers("/api/bookings/user/{userId}").authenticated()
                .antMatchers("/api/bookings/user/delete/{bookingId}").authenticated() // <<< НОВОЕ ПРАВИЛО
                .antMatchers("/api/bookings/{roomId}/booked-dates").permitAll()
                .antMatchers(HttpMethod.GET, "/api/bookings").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/bookings/updateStatus/{id}").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/bookings/delete/{id}").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/bookings/update/{id}").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/api/bookings/{id}").authenticated() // Просмотр деталей бронирования

                .antMatchers("/api/admin/**").hasRole("ADMIN")
                .antMatchers("/private/**").authenticated()
                .antMatchers("/api/auth/me").authenticated()
                .antMatchers("/uploads/**").permitAll()
                .antMatchers("/api/users/profile/**").authenticated()
                // .antMatchers("/api/bookings/user/**").authenticated() // Уже покрыто более специфичным правилом выше
                // .antMatchers("/api/rooms/add").hasRole("ADMIN") // Уже покрыто /api/admin/** или отдельным правилом, если нужно
                .antMatchers("/api/rentals/**").hasAnyRole("HOSTES", "ADMIN")
                .antMatchers("/api/users/**").authenticated() // Осторожно, может быть слишком общим
                .antMatchers("/api/support/send").authenticated()
                .antMatchers("/api/support/messages").hasRole("ADMIN")
                .antMatchers("/api/support/messages/**").hasRole("ADMIN")
                .anyRequest().denyAll() // Изменено с permitAll() на denyAll() для безопасности по умолчанию
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
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
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5174"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
