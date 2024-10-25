package org.example.notions3

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class NotionS3Application

fun main(args: Array<String>) {
    runApplication<NotionS3Application>(*args)
}
