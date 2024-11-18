package org.example.notions3.minio

import org.example.notions3.AbstractIntegrationTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import java.net.HttpURLConnection
import java.net.URI

@ActiveProfiles("test")
class MinioContainerTest : AbstractIntegrationTest() {

    @Test
    fun `test container healthcheck`() {
        val url =
            URI.create("http://${minio.host}:${minio.getMappedPort(9000)}/minio/health/live").toURL()

        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        val responseCode = connection.responseCode
        Assertions.assertEquals(200, responseCode, "MinIO container is not healthy")
    }
}