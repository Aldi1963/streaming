package com.sonzaix.streaming.presentation.navigation

object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val SEARCH = "search"
    const val POPULAR = "popular"
    const val DETAIL = "detail/{provider}/{dramaId}"
    const val PLAYER = "player/{provider}/{dramaId}/{episodeId}/{episodeNumber}"
    const val FAVORITE = "favorite"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
    const val API_TESTER = "api_tester"

    fun detail(provider: String, dramaId: String) = "detail/$provider/$dramaId"
    fun player(provider: String, dramaId: String, episodeId: String, episodeNumber: Int) =
        "player/$provider/$dramaId/$episodeId/$episodeNumber"
}
