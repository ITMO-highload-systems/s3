package org.example.notions3.advice


import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.annotation.Order
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebExceptionHandler
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets


@Component
@Order(-2)
class ExceptionHandler(private val objectMapper: ObjectMapper) : WebExceptionHandler {

    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        if (ex is WebClientResponseException) {
            exchange.response.setStatusCode(ex.statusCode)
            val bytes: ByteArray =
                objectMapper.readValue(ex.responseBodyAsString, ApiException::class.java).message?.toByteArray(
                    StandardCharsets.UTF_8
                )
                    ?: "No message".toByteArray(StandardCharsets.UTF_8)
            val buffer: DataBuffer = exchange.response.bufferFactory().wrap(bytes)
            return exchange.response.writeWith(Flux.just(buffer))
        }
        if (ex is IllegalArgumentException) {
            exchange.response.setStatusCode(HttpStatus.BAD_REQUEST)
            val bytes: ByteArray =
                ex.message?.toByteArray(StandardCharsets.UTF_8) ?: "No message".toByteArray(StandardCharsets.UTF_8)
            val buffer: DataBuffer = exchange.response.bufferFactory().wrap(bytes)
            return exchange.response.writeWith(Flux.just(buffer))
        }

        return Mono.error(ex)
    }

}