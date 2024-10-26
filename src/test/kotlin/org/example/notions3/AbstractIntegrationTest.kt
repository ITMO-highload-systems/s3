package org.example.notions3

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeAll
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.MinIOContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait


@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
abstract class AbstractIntegrationTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)

        @ServiceConnection
        internal var minio = MinIOContainer("minio/minio:latest").apply {
            withCommand("server /data")
                .withExposedPorts(9000)
                .waitingFor(Wait.forHttp("/minio/health/live").forStatusCode(200))
        }

        @ServiceConnection
        internal var postgres = PostgreSQLContainer("postgres:latest")
            .withInitScript("db/test_data.sql")


        @BeforeAll
        @JvmStatic
        fun setup() {
            minio.start()
            postgres.start()
        }
    }
}