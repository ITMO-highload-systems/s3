package org.example.notions3.dto.request

import org.springframework.web.multipart.MultipartFile

data class SaveImageRequest(
    val paragraphId: Long,
    val images: List<MultipartFile> = emptyList()
)