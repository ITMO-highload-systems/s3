package org.example.notions3.service

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import org.example.notions3.AbstractIntegrationTest
import org.example.notions3.repository.ImageRecordRepository
import org.example.notions3.util.MinioAdapter
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.reactive.function.BodyInserters
import reactor.test.StepVerifier

@ActiveProfiles("test")
class ImageServiceTest: AbstractIntegrationTest() {

    @Autowired
    private lateinit var imageRecordRepository: ImageRecordRepository

    @Autowired
    private lateinit var minioAdapter: MinioAdapter

    @Autowired
    @Qualifier("mockCoreService")
    private lateinit var coreService: WireMockServer

    private val paragraphId = 1L

    private val imageName = "Cat.jpg"

    @AfterEach
    fun clean() {
        imageRecordRepository.deleteAll().block()
        minioAdapter.deleteImage(imageName).block()
    }

    @BeforeEach
    fun setUp() {
        coreService.stubFor(
            WireMock.get(WireMock.urlPathMatching("/api/v1/paragraph/paragraphs/\\d+/images/availability"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("true")
                )
        )
    }

    @Test
    fun `saveImage - valid image - success save`() {
        val imageRecordsBeforeSave = imageRecordRepository.findByParagraphId(paragraphId)
        StepVerifier.create(imageRecordsBeforeSave.collectList())
            .assertNext { recordsBefore ->
                assertEquals(0, recordsBefore.size)
            }
            .verifyComplete()

        saveImage(201)

        val imageRecordsAfterSave = imageRecordRepository.findByParagraphId(paragraphId)
        StepVerifier.create(imageRecordsAfterSave.collectList())
            .assertNext { recordsAfter ->
                assertEquals(1, recordsAfter.size)
            }
            .verifyComplete()

    }

    @Test
    fun `saveImage - invalid image - failed save`() {
        coreService.stubFor(
            WireMock.get(WireMock.urlPathMatching("/api/v1/paragraph/paragraphs/\\d+/images/availability"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("false")
                )
        )

        val imageRecordsBeforeSave = imageRecordRepository.findByParagraphId(paragraphId)
        StepVerifier.create(imageRecordsBeforeSave.collectList())
            .assertNext { recordsBefore ->
                assertEquals(0, recordsBefore.size)
            }
            .verifyComplete()

        saveImage(400)

        val imageRecordsAfterSave = imageRecordRepository.findByParagraphId(paragraphId)
        StepVerifier.create(imageRecordsAfterSave.collectList())
            .assertNext { recordsAfter ->
                assertEquals(0, recordsAfter.size)
            }
            .verifyComplete()

    }

    @ParameterizedTest
    @CsvSource(
        "true", "false"
    )
    fun `deleteImage - valid parameter - success delete`(isDeleteByParagraphId: Boolean) {
        // Сначала сохраняем изображение
        saveImage(201)

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

    fun saveImage(expectedStatus: Int) {
        val multipartBodyBuilder = MultipartBodyBuilder()
        multipartBodyBuilder.part("file", ClassPathResource("images/Cat.jpg"))

        webTestClient.put()
            .uri("/api/v1/image/$paragraphId")
            .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
            .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA.toString())
            .exchange()
            .expectStatus().value { status ->
                assert(status == expectedStatus) { "Expected status $expectedStatus but got $status" }
            }
    }

    private fun deleteImageByParagraphId(paragraphId: Long) {
        webTestClient.delete()
            .uri("/api/v1/image/by-paragraph/$paragraphId")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNoContent
    }

    private fun deleteImageByName(imageName: String) {
        webTestClient.delete()
            .uri("/api/v1/image/by-name/$imageName")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNoContent
    }
}