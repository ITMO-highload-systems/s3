package org.example.notions3.controller

import org.example.notions3.dto.request.SaveImageRequest
import org.example.notions3.dto.response.GetImageResponse
import org.example.notions3.service.ImageService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/image")
class ImageController(
    private val imageService: ImageService
) {

    @GetMapping("/get/{paragraphId}")
    fun getImageByParagraphId(@PathVariable paragraphId: Long): ResponseEntity<GetImageResponse> {
        return ResponseEntity.ok(imageService.getImageByParagraphId(paragraphId))
    }

    @PostMapping("/create")
    fun saveImage(@ModelAttribute saveImageRequest: SaveImageRequest): ResponseEntity<Unit> {
        imageService.createImages(saveImageRequest)
        return ResponseEntity(HttpStatus.CREATED)
    }

    @DeleteMapping("/deleteByParagraphId/{paragraphId}")
    fun deleteImageByParagraphId(@PathVariable paragraphId: Long): ResponseEntity<Unit> {
        imageService.deleteImageByParagraphId(paragraphId)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/deleteByHash/{imageHash}")
    fun deleteImageByHash(@PathVariable imageHash: String): ResponseEntity<Unit> {
        imageService.deleteImageByHash(imageHash)
        return ResponseEntity.noContent().build()
    }
}