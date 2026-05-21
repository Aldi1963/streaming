package com.sonzaix.streaming.domain.model

data class StreamSource(
    val url: String,
    val quality: String? = null,
    val type: String? = null,
    val headers: Map<String, String>? = null
)
