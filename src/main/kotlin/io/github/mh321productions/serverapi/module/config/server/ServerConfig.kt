package io.github.mh321productions.serverapi.module.config.server

import java.util.UUID

/**
 * A list of the server-wide player configs
 */
class ServerConfig {
    val friends: ServerFriendConfig = ServerFriendConfig()
    val party: ServerPartyConfig = ServerPartyConfig()
    val chat: ServerChatConfig = ServerChatConfig()
}

class ServerFriendConfig {
    /**
     * Allow friends to "jump" to you (Type: [Boolean], Default: true)
     */
    var allowJumping: Boolean = true

    /**
     * Allow others to send you friend requests (Type: [Boolean], Default: true)
     */
    var recieveInvites: Boolean = true

    /**
     * Allow others to see the map you're on (Type: [Boolean], Default: true)
     */
    var requestMapName: Boolean = true

    /**
     * Your friends (Type: List[[UUID]], Default: [])
     */
    var friends: MutableList<UUID> = mutableListOf()

    /**
     * Friend requests sent to you by other players (Type: List[[UUID]], Default: [])
     */
    var incomingRequests: MutableList<UUID> = mutableListOf()

    /**
     * Friend requests you sent to other players (Type: List[[UUID]], Default: [])
     */
    var outgoingRequests: MutableList<UUID> = mutableListOf()
}

class ServerPartyConfig {
    /**
     * Allow others to send you party invites (Type: [PlayerTrustType] (Byte), Default: [PlayerTrustType.All])
     */
    var recieveInvites: Byte = PlayerTrustType.All.entryValue
}

class ServerChatConfig {
    /**
     * Allow others to send you private messages (Type: [PlayerTrustType] (Byte), Default: [PlayerTrustType.All])
     */
    var recieveMsg: Byte = PlayerTrustType.All.entryValue
}