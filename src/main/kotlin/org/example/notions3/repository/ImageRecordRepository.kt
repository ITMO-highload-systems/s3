package org.example.notions3.repository

import org.example.notions3.model.ImageRecord
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ImageRecordRepository : ReactiveCrudRepository<ImageRecord, Long> {

    @Query("SELECT * FROM image_record WHERE image_name = :imageName")
    fun findByImageName(imageName: String): Mono<ImageRecord>

    @Query("SELECT * FROM image_record WHERE paragraph_id = :paragraphId")
    fun findByParagraphId(paragraphId: Long): Flux<ImageRecord?>

    @Query("DELETE FROM image_record WHERE paragraph_id = :paragraphId")
    fun deleteByParagraphId(paragraphId: Long): Mono<Void>
}
