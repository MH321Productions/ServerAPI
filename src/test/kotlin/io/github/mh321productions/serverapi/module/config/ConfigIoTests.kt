package io.github.mh321productions.serverapi.module.config

import kotlin.test.Test

class ConfigIoTests {
    @Test
    fun `Deserializing anonymously should give valid entries`() {
        // Arrange
        val config = ConfigIo.createTestConfig()

        // Act
        val server = config.deserialize("server", ServerConfig::class.java) as ServerConfig
        val lobby = config.deserialize("lobby", LobbyConfig::class.java) as LobbyConfig

        // Assert
        require(server.party.allowRequests == false)
        require(lobby.number == 420)
    }

    @Test
    fun `Deserializing generic should give valid entries`() {
        // Arrange
        val config = ConfigIo.createTestConfig()

        // Act
        val server = config.deserializeGeneric("server", ServerConfig::class.java)
        val lobby = config.deserializeGeneric("lobby", LobbyConfig::class.java)

        // Assert
        require(server.party.allowRequests == false)
        require(lobby.number == 420)
    }
}