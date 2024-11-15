package org.example.notions3.config

import org.springframework.boot.autoconfigure.service.connection.ConnectionDetails

interface MinioConnectionDetails : ConnectionDetails {
    val url: String?

    val accessKey: String?

    val secretKey: String?

    val bucket: String?
}