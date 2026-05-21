package com.sonzaix.streaming.core.utils

object VideoUtils {
    fun isHls(url: String): Boolean = url.contains(".m3u8", ignoreCase = true)
    fun isMp4(url: String): Boolean = url.contains(".mp4", ignoreCase = true)
}
