package org.example.notions3.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("image_record")
data class ImageRecord(
    @Id
    val id: Long,
    val imageName: String,
    val paragraphId: Long
) {
    class Builder {
        private var id: Long = 0
        private var imageName: String = ""
        private var paragraphId: Long = 0

        fun id(id: Long) = apply { this.id = id }
        fun imageName(imageName: String) = apply { this.imageName = imageName }
        fun paragraphId(paragraphId: Long) = apply { this.paragraphId = paragraphId }

        fun build(): ImageRecord {
            return ImageRecord(id, imageName, paragraphId)
        }
    }
}