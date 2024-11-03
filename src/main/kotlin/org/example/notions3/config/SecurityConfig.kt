package org.example.notions3.config

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
                auth.anyExchange().authenticated()
            }
            .securityContextRepository(securityContextRepository)

        return http.build()

    }
}