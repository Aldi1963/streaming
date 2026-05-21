package com.sonzaix.streaming.domain.model

data class DramaItem(
    val id: String,
    val provider: String,
    val title: String,
    val description: String? = null,
    val thumbnail: String? = null,
    val banner: String? = null,
    val episodeCount: Int? = null,
    val watchCount: String? = null,
    val language: String? = null,
    val tags: List<String> = emptyList(),
    val rawJson: String? = null
)
