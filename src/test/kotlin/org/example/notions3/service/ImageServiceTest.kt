package org.example.notions3.service

import org.example.notion.minio.util.calculateFileHash
import org.example.notions3.AbstractIntegrationTest
import org.example.notions3.repository.ImageRecordRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.io.File

class ImageServiceTest: AbstractIntegrationTest() {

    @Autowired
    private lateinit var imageRecordRepository: ImageRecordRepository

    private val paragraphId = 1L

    @AfterEach
    fun clean() {
        imageRecordRepository.deleteAll()
    }

    @Test
    fun `saveImage - valid image - success safe`() {
        val imageRecordsBeforeSave = imageRecordRepository.findByParagraphId(paragraphId)
        Assertions.assertEquals(0, imageRecordsBeforeSave.size)

        saveImage()
        val imageRecordsAfterSave = imageRecordRepository.findByParagraphId(paragraphId)
        Assertions.assertEquals(1, imageRecordsAfterSave.size)
    }

    @Test
    fun `deleteImageByParagraphId - valid id - success delete`() {
        saveImage()
        val imageRecordsBeforeSave = imageRecordRepository.findByParagraphId(paragraphId)
        Assertions.assertEquals(1, imageRecordsBeforeSave.size)
        deleteImageByParagraphId(paragraphId)

        val imageRecordsAfterSave = imageRecordRepository.findByParagraphId(paragraphId)
        Assertions.assertEquals(0, imageRecordsAfterSave.size)
    }

//    @Test
    fun `deleteImageByImageHash - valid hash - success delete`() {
        val imageHash = saveImage()
        val imageRecordsBeforeSave = imageRecordRepository.findByParagraphId(paragraphId)
        Assertions.assertEquals(1, imageRecordsBeforeSave.size)

        deleteImageByImageHash(imageHash)

        val imageRecordsAfterSave = imageRecordRepository.findByParagraphId(paragraphId)
        Assertions.assertEquals(0, imageRecordsAfterSave.size)
    }

    private fun saveImage(): String {
        val file = File("src/test/resources/images/Cat.jpg")

        val image = MockMultipartFile(
            "images",
            file.name,
            MediaType.IMAGE_JPEG_VALUE,
            file.readBytes()
        )

        mockMvc.perform(
            MockMvcRequestBuilders.multipart("/api/v1/image/create")
                .file(image)  // Передача файла
                .param("paragraphId", paragraphId.toString())
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        ).andExpect(status().isCreated)

        return image.calculateFileHash()
    }

    private fun deleteImageByParagraphId(paragraphId: Long) {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/image/deleteByParagraphId/${paragraphId}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent)
    }

    private fun deleteImageByImageHash(imageHash: String) {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/image/deleteByImageHash/${imageHash}")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent)
    }
}