package org.example.notions3

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import reactivefeign.spring.config.EnableReactiveFeignClients

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableFeignClients
@EnableReactiveFeignClients
class NotionS3Application

fun main(args: Array<String>) {
    runApplication<NotionS3Application>(*args)
}
