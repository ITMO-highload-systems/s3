package org.example.notions3.client

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import reactivefeign.spring.config.ReactiveFeignClient
import reactor.core.publisher.Mono

@ReactiveFeignClient(
    value = "notion"
)
interface CoreClient {

    @GetMapping("/api/v1/paragraph/isPossibleAddImageToParagraph/{paragraphId}")
    fun isPossibleAddImageToParagraph(@PathVariable paragraphId: String): Mono<Boolean>
}