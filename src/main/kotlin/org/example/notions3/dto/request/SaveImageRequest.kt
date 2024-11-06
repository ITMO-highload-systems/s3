package org.example.notions3.dto.request

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import org.springframework.http.codec.multipart.FilePart

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class SaveImageRequest(
    val paragraphId: Long,
    val image: FilePart
)