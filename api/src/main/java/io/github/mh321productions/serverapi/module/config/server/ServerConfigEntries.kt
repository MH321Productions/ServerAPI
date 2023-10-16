package io.github.mh321productions.serverapi.module.config.server

import io.github.mh321productions.serverapi.configio.ConfigEntry
import io.github.mh321productions.serverapi.module.config.DefaultEntryCollection
import java.util.*

/**
 * A list of the server-wide player configs
 */
object ServerConfigEntries : DefaultEntryCollection {
    /**
     * Allow friends to "jump" to you (Type: Boolean, Default: true)
     */
    const val ALLOW_JUMPING = "server.friend.allow_jumping"

    /**
     * Allow others to send you private messages (Type: [PlayerTrustType] (Byte), Default: [PlayerTrustType.All])
     */
    const val RECIEVE_MSG = "server.chat.recieve_msg"

    /**
     * Allow others to send you party invites (Type: [PlayerTrustType] (Byte), Default: [PlayerTrustType.All])
     */
    const val RECIEVE_PARTY_INVITES = "server.party.recieve_invites"

    /**
     * Allow others to send you friend requests (Type: Boolean, Default: true)
     */
    const val RECIEVE_FRIEND_INVITES = "server.friend.recieve_invites"

    /**
     * Allow others to see the map you're on (Type: Boolean, Default: true)
     */
    const val REQUEST_MAP_NAME = "server.friend.request_map_name"

    /**
     * Your friends (Type: Array[[UUID]], Default: [])
     */
    const val FRIEND_LIST = "server.friend.friends"

    override fun getDefaultEntries(): List<ConfigEntry> {
        return listOf(
            ConfigEntry(ALLOW_JUMPING, true),
            ConfigEntry(RECIEVE_MSG, PlayerTrustType.All.entryValue),
            ConfigEntry(RECIEVE_PARTY_INVITES, PlayerTrustType.All.entryValue),
            ConfigEntry(RECIEVE_FRIEND_INVITES, true),
            ConfigEntry(REQUEST_MAP_NAME, true)
        )
    }
}