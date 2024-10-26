package org.example.notions3.service

import org.example.notions3.AbstractIntegrationTest
import org.example.notions3.repository.ImageRecordRepository
import org.example.notions3.util.MinioAdapter
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.web.reactive.function.BodyInserters
import reactor.test.StepVerifier

class ImageServiceTest: AbstractIntegrationTest() {

    @Autowired
    private lateinit var imageRecordRepository: ImageRecordRepository

    @Autowired
    private lateinit var minioAdapter: MinioAdapter

    private val paragraphId = 1L

    private val imageName = "Cat.jpg"

    @AfterEach
    fun clean() {
        imageRecordRepository.deleteAll().block()
        minioAdapter.deleteImage(imageName).block()
    }

    @Test
    fun `saveImage - valid image - success save`() {
        val imageRecordsBeforeSave = imageRecordRepository.findByParagraphId(paragraphId)
        StepVerifier.create(imageRecordsBeforeSave.collectList())
            .assertNext { recordsBefore ->
                assertEquals(0, recordsBefore.size)
            }
            .verifyComplete()

        saveImage()

        val imageRecordsAfterSave = imageRecordRepository.findByParagraphId(paragraphId)
        StepVerifier.create(imageRecordsAfterSave.collectList())
            .assertNext { recordsAfter ->
                assertEquals(1, recordsAfter.size)
            }
            .verifyComplete()

    }

    @ParameterizedTest
    @CsvSource(
        "true", "false"
    )
    fun `deleteImage - valid parameter - success delete`(isDeleteByParagraphId: Boolean) {
        // Сначала сохраняем изображение
        saveImage()

        val imageRecordsBeforeDelete = imageRecordRepository.findByParagraphId(paragraphId)
        StepVerifier.create(imageRecordsBeforeDelete.collectList())
            .assertNext { recordsBefore ->
                assertEquals(1, recordsBefore.size)
            }
            .verifyComplete()

        if (isDeleteByParagraphId) {
            deleteImageByParagraphId(paragraphId)
        } else {
            deleteImageByName(imageName)
        }

        val imageRecordsAfterDelete = imageRecordRepository.findByParagraphId(paragraphId)
        StepVerifier.create(imageRecordsAfterDelete.collectList())
            .assertNext { recordsAfter ->
                assertEquals(0, recordsAfter.size)
            }
            .verifyComplete()
    }

    fun saveImage() {
        val multipartBodyBuilder = MultipartBodyBuilder();
        multipartBodyBuilder.part("file", ClassPathResource("images/Cat.jpg"))
            .contentType(MediaType.MULTIPART_FORM_DATA)

        webTestClient.put()
            .uri("/api/v1/image/create/$paragraphId")
            .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
            .exchange()
            .expectStatus().isCreated()
    }

    private fun deleteImageByParagraphId(paragraphId: Long) {
        webTestClient.delete()
            .uri("/api/v1/image/deleteByParagraphId/$paragraphId")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNoContent
    }

    private fun deleteImageByName(imageName: String) {
        webTestClient.delete()
            .uri("/api/v1/image/deleteByName/$imageName")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNoContent
    }
}