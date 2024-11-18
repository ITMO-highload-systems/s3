package org.example.notions3.config

import com.github.tomakehurst.wiremock.WireMockServer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles

@TestConfiguration
@ActiveProfiles("test")
class WireMockConfig {
    @Value("\${core.mock-port}")
    private var port: Int = 87

    @Bean(name = ["mockCoreService"], initMethod = "start", destroyMethod = "stop")
    fun mockCoreService(): WireMockServer {
        return WireMockServer(port)
    }
}