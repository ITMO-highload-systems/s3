package org.example.notions3.service

import org.example.notions3.dto.request.SaveImageRequest
import org.example.notions3.dto.response.GetImageResponse

interface ImageService {

    fun getImageByParagraphId(paragraphId: Long): GetImageResponse

    fun createImages(saveImageRequest: SaveImageRequest)

    fun deleteImageByHash(imageHash: String)

    fun deleteImageByParagraphId(paragraphId: Long)
}