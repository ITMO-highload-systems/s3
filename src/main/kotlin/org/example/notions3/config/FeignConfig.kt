package org.example.notions3.config

import org.example.notions3.auth.JwtUtil
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactivefeign.client.ReactiveHttpRequest
import reactivefeign.client.ReactiveHttpRequestInterceptor
import reactor.core.publisher.Mono

@Configuration
class FeignConfig {

    @Bean
    fun reactiveHttpRequestInterceptor(jwtUtil: JwtUtil): ReactiveHttpRequestInterceptor {
        return ReactiveHttpRequestInterceptor { request: ReactiveHttpRequest ->
            request.headers()["Authorization"] = listOf("Bearer " + jwtUtil.generateServerToken())
            Mono.just(request)
        }
    }
}
