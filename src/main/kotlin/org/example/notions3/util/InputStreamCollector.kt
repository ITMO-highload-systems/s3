package org.example.notions3.util

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream


class InputStreamCollector {

    private var targetStream: ByteArrayOutputStream = ByteArrayOutputStream()

    fun collectInputStream(input: InputStream?): InputStreamCollector {
        input?.transferTo(targetStream)
        return this
    }

    val stream: InputStream
        get() = ByteArrayInputStream(targetStream.toByteArray())
}