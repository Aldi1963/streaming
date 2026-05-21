package com.sonzaix.streaming.domain.model

enum class Provider(val id: String, val displayName: String) {
    MELOLO("melolo", "Melolo"),
    FREEREELS("freereels", "FreeReels"),
    FLICKREELS("flickreels", "FlickReels"),
    DRAMAWAVE("dramawave", "DramaWave"),
    DRAMANOVA("dramanova", "DramaNova"),
    MELOSHORT("meloshort", "MeloShort"),
    REELSHORT("reelshort", "ReelShort"),
    NETSHORT("netshort", "NetShort"),
    SHORTMAX("shortmax", "ShortMax"),
    DRAMABOX("dramabox", "DramaBox"),
    GOODSHORT("goodshort", "GoodShort");

    companion object {
        fun fromId(id: String): Provider = entries.find { it.id == id } ?: MELOLO
    }
}
