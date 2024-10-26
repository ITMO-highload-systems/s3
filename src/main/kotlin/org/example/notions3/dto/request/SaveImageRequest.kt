package org.example.notions3.dto.request

import org.springframework.http.codec.multipart.FilePart

data class SaveImageRequest(
    val paragraphId: Long,
    val image: FilePart
)