package io.github.mh321productions.serverapi.module.config

class ServerConfig {
    val friends = ServerFriendConfig()
    val party = ServerPartyConfig()
}

class ServerFriendConfig {
    var allowRequests: Boolean = true
}

class ServerPartyConfig {
    var allowRequests: Boolean = true
}

class LobbyConfig {
    var showScoreboard: Boolean = true
    var number: Int = 69
}