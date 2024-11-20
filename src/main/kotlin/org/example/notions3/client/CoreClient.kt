package org.example.notions3.client

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.example.notions3.client.feign.CoreFeignClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import javax.naming.ServiceUnavailableException

@Service
class CoreClient(
    private val coreFeignClient: CoreFeignClient
) {
    companion object {
        private val logger = LoggerFactory.getLogger(CoreClient::class.java)
        private const val FALLBACK_MESSAGE = "Core Service is unavailable"
    }

    @CircuitBreaker(name = "default", fallbackMethod = "isPossibleAddImageToParagraphFallback")
    fun isPossibleAddImageToParagraph(paragraphId: String): Mono<Boolean> {
        return coreFeignClient.isPossibleAddImageToParagraph(paragraphId)
    }

    fun isPossibleAddImageToParagraphFallback(paragraphId: String, ex: Throwable): Mono<Boolean> {
        logger.error("Fallback for isPossibleAddImageToParagraph invoked due to: ${ex.message}")
        throw ServiceUnavailableException(FALLBACK_MESSAGE)
    }
}