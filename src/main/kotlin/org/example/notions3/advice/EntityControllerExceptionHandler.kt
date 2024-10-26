package org.example.notions3.advice

import org.springframework.core.annotation.AnnotationUtils
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono

@ControllerAdvice
class EntityControllerExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleException(
        request: ServerRequest,
        ex: Exception
    ): Mono<ServerResponse> {
        var status = HttpStatus.INTERNAL_SERVER_ERROR
        val message = ex.message

        val responseStatus = AnnotationUtils.findAnnotation(
            ex.javaClass,
            ResponseStatus::class.java
        )
        if (responseStatus != null) {
            status = responseStatus.value
        }

        return ServerResponse.status(status)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(ApiException(status.value(), message))
            .switchIfEmpty(Mono.error(ex))
    }
}