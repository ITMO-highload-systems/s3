package org.example.notions3.advice

data class ApiException(
    val code: Int,
    val message: String?
)