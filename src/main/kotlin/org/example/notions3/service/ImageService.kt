package org.example.notions3.service

import org.example.notions3.dto.request.SaveImageRequest
import org.example.notions3.dto.response.GetImageResponse
import reactor.core.publisher.Mono

interface ImageService {

    fun getImageByParagraphId(paragraphId: Long): Mono<GetImageResponse>

    fun createImages(saveImageRequest: SaveImageRequest): Mono<Void>

    fun deleteImageByName(imageName: String): Mono<Void>

    fun deleteImageByParagraphId(paragraphId: Long): Mono<Void>
}