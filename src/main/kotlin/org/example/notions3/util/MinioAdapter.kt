package org.example.notions3.util

import io.minio.*
import io.minio.http.Method
import org.example.notions3.config.MinioConnectionDetails
import org.example.notions3.dto.response.UploadResponse
import org.slf4j.LoggerFactory
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Service
class MinioAdapter(
    private val minioClient: MinioClient,
    private val minioConnectionDetails: MinioConnectionDetails
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getImageUrl(name: String): Mono<String> {
        return Mono.fromCallable {
            minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .bucket(minioConnectionDetails.bucket)
                    .`object`(name)
                    .method(Method.GET)
                    .build()
            )
        }.subscribeOn(Schedulers.boundedElastic())
    }

    fun deleteImage(fileName: String): Mono<Unit> {
        return Mono.fromCallable {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(minioConnectionDetails.bucket)
                    .`object`(fileName)
                    .build()
            )
            logger.info("File '$fileName' deleted from bucket '${minioConnectionDetails.bucket}'")
            null
        }
            .subscribeOn(Schedulers.boundedElastic())
            .doOnError { e ->
                logger.error("Error occurred while deleting file '$fileName' from bucket '${minioConnectionDetails.bucket}': ${e.message}")
            }
            .onErrorMap { e ->
                IllegalStateException("Failed to delete image with name: $fileName", e)
            }
            .then(Mono.just(Unit))
    }

    fun putObject(file: FilePart): Mono<UploadResponse> {
        return file.content()
            .subscribeOn(Schedulers.boundedElastic())
            .reduce(InputStreamCollector()) { collector, dataBuffer ->
                collector.collectInputStream(dataBuffer.asInputStream())
            }
            .map { inputStreamCollector ->
                val startMillis = System.currentTimeMillis()
                logger.info(file.headers().contentType.toString())
                val args = PutObjectArgs.builder()
                    .`object`(file.filename())
                    .contentType(file.headers().contentType!!.toString())
                    .bucket(minioConnectionDetails.bucket)
                    .stream(inputStreamCollector.stream, inputStreamCollector.stream.available().toLong(), -1)
                    .build()

                minioClient.putObject(args)
                logger.info("upload file execution time {} ms", System.currentTimeMillis() - startMillis)
                UploadResponse("Success")
            }
            .log()
    }
}