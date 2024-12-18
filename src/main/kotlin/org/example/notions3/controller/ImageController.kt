package org.example.notions3.controller

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.servers.Server
import org.example.notions3.dto.request.SaveImageRequest
import org.example.notions3.dto.response.GetImageResponse
import org.example.notions3.service.ImageService
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/image")
@Configuration
class ImageController(
    private val imageService: ImageService
) {

    @GetMapping("{paragraphId}")
    fun getImageByParagraphId(@PathVariable paragraphId: Long): Mono<ResponseEntity<GetImageResponse>> {
        return imageService.getImageByParagraphId(paragraphId)
            .map { imageResponse -> ResponseEntity.ok(imageResponse) }
    }

    @PutMapping("/{paragraphId}", consumes= [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun saveImage(
        @PathVariable paragraphId: Long,
        @RequestPart("file") filePart: FilePart
    ): Mono<ResponseEntity<Unit>> {
        return imageService.createImages(SaveImageRequest(paragraphId, filePart))
            .then(Mono.just(ResponseEntity<Unit>(HttpStatus.CREATED)))
    }

    @DeleteMapping("/by-paragraph/{paragraphId}")
    fun deleteImageByParagraphId(@PathVariable paragraphId: Long): Mono<ResponseEntity<Unit>> {
        return imageService.deleteImageByParagraphId(paragraphId)
            .then(Mono.just(ResponseEntity.noContent().build()))
    }

    @DeleteMapping("/by-name/{imageName}")
    fun deleteImageByName(@PathVariable imageName: String): Mono<ResponseEntity<Unit>> {
        return imageService.deleteImageByName(imageName)
            .then(Mono.just(ResponseEntity.noContent().build()))
    }
}