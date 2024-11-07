package org.example.notions3.client

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import reactivefeign.spring.config.ReactiveFeignClient
import reactor.core.publisher.Mono

@ReactiveFeignClient(
    value = "notion"
)
interface CoreClient {

    @GetMapping("/paragraphs/{paragraphId}/images/availability")
    fun isPossibleAddImageToParagraph(@PathVariable paragraphId: String): Mono<Boolean>
}