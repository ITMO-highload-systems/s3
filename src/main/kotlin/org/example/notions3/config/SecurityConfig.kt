package org.example.notions3.config

import org.example.notions3.auth.JWTAuthenticationManager
import org.example.notions3.auth.SecurityContextRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
    private val jwtAuthenticationManager: JWTAuthenticationManager,
    private val securityContextRepository: SecurityContextRepository
) {

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http
            .csrf { csrf -> csrf.disable() }
            .authenticationManager(jwtAuthenticationManager)
            .authorizeExchange { auth ->
                auth.pathMatchers("/actuator/**").permitAll()
                auth.anyExchange().authenticated()
            }
            .securityContextRepository(securityContextRepository)

        return http.build()

    }
}