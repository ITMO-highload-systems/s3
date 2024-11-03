package org.example.notions3.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.security.Key
import java.util.*
import java.util.function.Function


@Component
class JWTAuthenticationManager : ReactiveAuthenticationManager {
    @Value("\${application.security.jwt.secret-key}")
    private val secretKey: String? = null

    override fun authenticate(authentication: Authentication): Mono<Authentication> {

        val authorizationHeader = authentication.credentials.toString()
        if (authorizationHeader.startsWith("Bearer ")) {
            val token = authorizationHeader.substring(7)
            val userEmail = extractUsername(token)
            if (userEmail != null && SecurityContextHolder.getContext().authentication == null) {
                if (!isTokenExpired(token)) {
                    val authenticationToken = UsernamePasswordAuthenticationToken(
                        null,
                        null,
                        null
                    )
                    return Mono.just(authenticationToken)
                }
            }
        }
        return Mono.empty()
    }

    private fun extractAllClaims(token: String?): Claims {
        return Jwts
            .parserBuilder()
            .setSigningKey(signInKey)
            .build()
            .parseClaimsJws(token)
            .body
    }

    fun extractUsername(token: String?): String? {
        return extractClaim(token) { obj: Claims -> obj.subject }
    }

    fun <T> extractClaim(token: String?, claimsResolver: Function<Claims, T>): T {
        val claims = extractAllClaims(token)
        return claimsResolver.apply(claims)
    }

    private fun isTokenExpired(token: String?): Boolean {
        return extractExpiration(token).before(Date())
    }

    private fun extractExpiration(token: String?): Date {
        return extractClaim(token) { obj: Claims -> obj.expiration }
    }

    private val signInKey: Key
        get() {
            val keyBytes = Decoders.BASE64.decode(secretKey)
            return Keys.hmacShaKeyFor(keyBytes)
        }
}