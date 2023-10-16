package io.github.mh321productions.serverapi.module.config.server

/**
 * Categorize the players you "trust" for certain configs
 */
enum class PlayerTrustType {
    /**
     * You allow all players to do the specified operation
     */
    All,

    /**
     * You allow only your friends to do the specified operation
     */
    OnlyFriends,

    /**
     * You allow no one to do the specified operation
     */
    None;

    val entryValue = ordinal.toByte()

    companion object {
        fun fromEntryValue(entryValue: Byte) : PlayerTrustType =
            when(entryValue) {
                All.entryValue -> All
                OnlyFriends.entryValue -> OnlyFriends
                None.entryValue -> None
                else -> throw IllegalArgumentException("No entry with given value ($entryValue) found")
            }
    }
}