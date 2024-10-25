package org.example.notions3.service.impl

import org.example.notion.minio.util.calculateFileHash
import org.example.notions3.dto.request.SaveImageRequest
import org.example.notions3.dto.response.GetImageResponse
import org.example.notions3.model.ImageRecord
import org.example.notions3.repository.ImageRecordRepository
import org.example.notions3.service.ImageService
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class ImageServiceImpl(
    private val minioStorageService: MinioStorageServiceImpl,
    private val imageRecordRepository: ImageRecordRepository
) : ImageService {

    override fun getImageByParagraphId(paragraphId: Long): GetImageResponse {
        val imageUrls = imageRecordRepository.findByParagraphId(paragraphId).map { minioStorageService.getImageUrl(it.imageHash) }
        return GetImageResponse(imageUrls)
    }

    override fun createImages(saveImageRequest: SaveImageRequest) {
        saveImageRequest.images.forEach { uploadImage(it, saveImageRequest.paragraphId) }
    }

    override fun deleteImageByHash(imageHash: String) {
        imageRecordRepository.findByImageHash(imageHash)?.let { deleteImage(it) }
    }

    override fun deleteImageByParagraphId(paragraphId: Long) {
        imageRecordRepository.findByParagraphId(paragraphId).forEach { deleteImage(it) }
    }

    private fun uploadImage(image: MultipartFile, paragraphId: Long) {
        minioStorageService.uploadImg(image)
        imageRecordRepository.save(
            ImageRecord.Builder()
                .paragraphId(paragraphId)
                .imageHash("${image.calculateFileHash()}.jpg")
                .build()
        )
    }

    private fun deleteImage(imageRecord: ImageRecord) {
        imageRecordRepository.deleteById(imageRecord.id)
        if (imageRecordRepository.findByImageHash(imageRecord.imageHash) == null) {
            minioStorageService.deleteImage(imageRecord.imageHash)
        }
    }
}