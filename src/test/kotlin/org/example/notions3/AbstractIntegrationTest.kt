package org.example.notions3

import org.example.notions3.config.WireMockConfig
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.MinIOContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait


@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@EnableFeignClients
@EnableConfigurationProperties
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [WireMockConfig::class])
abstract class AbstractIntegrationTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    companion object {

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