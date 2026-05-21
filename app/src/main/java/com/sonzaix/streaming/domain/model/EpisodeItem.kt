package com.sonzaix.streaming.domain.model

data class EpisodeItem(
    val id: String,
    val dramaId: String,
    val provider: String,
    val title: String,
    val episodeNumber: Int,
    val thumbnail: String? = null,
    val streamUrl: String? = null,
    val rawJson: String? = null
)
