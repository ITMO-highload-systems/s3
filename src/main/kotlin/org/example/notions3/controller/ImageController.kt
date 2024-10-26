package org.example.notions3.controller

import org.example.notions3.dto.request.SaveImageRequest
import org.example.notions3.dto.response.GetImageResponse
import org.example.notions3.service.ImageService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/image")
class ImageController(
    private val imageService: ImageService
) {

    @GetMapping("/get/{paragraphId}")
    fun getImageByParagraphId(@PathVariable paragraphId: Long): ResponseEntity<Mono<GetImageResponse>> {
        return ResponseEntity.ok(imageService.getImageByParagraphId(paragraphId))
    }

    @PutMapping("/create/{paragraphId}")
    fun saveImage(
        @PathVariable paragraphId: Long,
        @RequestPart("file") filePart: FilePart
    ): Mono<ResponseEntity<Unit>> {
        return imageService.createImages(SaveImageRequest(paragraphId, filePart))
            .then(Mono.just(ResponseEntity<Unit>(HttpStatus.CREATED)))
    }

    @DeleteMapping("/deleteByParagraphId/{paragraphId}")
    fun deleteImageByParagraphId(@PathVariable paragraphId: Long): Mono<ResponseEntity<Unit>> {
        return imageService.deleteImageByParagraphId(paragraphId)
            .then(Mono.just(ResponseEntity.noContent().build()))
    }

    @DeleteMapping("/deleteByName/{imageName}")
    fun deleteImageByName(@PathVariable imageName: String): Mono<ResponseEntity<Unit>> {
        return imageService.deleteImageByName(imageName)
            .then(Mono.just(ResponseEntity.noContent().build()))
    }
}