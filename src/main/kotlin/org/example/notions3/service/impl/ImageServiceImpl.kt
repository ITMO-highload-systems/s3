package org.example.notions3.service.impl

import org.example.notions3.dto.request.SaveImageRequest
import org.example.notions3.dto.response.GetImageResponse
import org.example.notions3.model.ImageRecord
import org.example.notions3.repository.ImageRecordRepository
import org.example.notions3.service.ImageService
import org.example.notions3.util.MinioAdapter
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ImageServiceImpl(
    private val minioAdapter: MinioAdapter,
    private val imageRecordRepository: ImageRecordRepository
) : ImageService {

    override fun getImageByParagraphId(paragraphId: Long): Mono<GetImageResponse> {
        return imageRecordRepository.findByParagraphId(paragraphId)
            .filter { it != null }
            .flatMap { imageRecord ->
                minioAdapter.getImageUrl(imageRecord!!.imageName)
            }
            .collectList()
            .map { imageUrls -> GetImageResponse(imageUrls) }
    }

    override fun createImages(saveImageRequest: SaveImageRequest): Mono<Void> {
        return uploadImage(saveImageRequest.image, saveImageRequest.paragraphId).then()
    }


    override fun deleteImageByName(imageName: String): Mono<Void> {
        return imageRecordRepository.findByImageName(imageName)
            .flatMap { imageRecord ->
                if (imageRecord != null) {
                    deleteImage(imageRecord)
                } else {
                    Mono.empty()
                }
            }
    }

    override fun deleteImageByParagraphId(paragraphId: Long): Mono<Void> {
        return imageRecordRepository.deleteByParagraphId(paragraphId)
    }


    private fun uploadImage(image: FilePart, paragraphId: Long): Mono<ImageRecord> {
        return minioAdapter.putObject(image)
            .then(
                Mono.fromCallable {
                    ImageRecord.Builder()
                        .paragraphId(paragraphId)
                        .imageName(image.filename())
                        .build()
                }
            )
            .flatMap { imageRecord ->
                imageRecordRepository.save(imageRecord)
            }
    }

    private fun deleteImage(imageRecord: ImageRecord): Mono<Void> {
        return imageRecordRepository.deleteById(imageRecord.id)
            .then(
                imageRecordRepository.findByImageName(imageRecord.imageName)
                    .flatMap { existingRecord ->
                        if (existingRecord == null) {
                            minioAdapter.deleteImage(imageRecord.imageName)
                        } else {
                            Mono.empty()
                        }
                    }
            )
    }
}