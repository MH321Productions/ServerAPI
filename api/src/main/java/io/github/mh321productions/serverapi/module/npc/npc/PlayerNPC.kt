package io.github.mh321productions.serverapi.module.npc.npc

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction
import com.comphenix.protocol.wrappers.PlayerInfoData
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedGameProfile
import com.comphenix.protocol.wrappers.WrappedRemoteChatSessionData
import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.module.npc.NPC
import io.github.mh321productions.serverapi.module.npc.NPCManager
import io.github.mh321productions.serverapi.module.npc.metadata.MetadataStorage
import io.github.mh321productions.serverapi.util.functional.KotlinBukkitRunnable
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.util.*

data class PlayerSpawnInfo(val profile: WrappedGameProfile, val displayName: String, val isListed: Boolean)

class PlayerNPC(val spawnInfo: PlayerSpawnInfo, entityId: Int, loc: Location, manager: NPCManager, main: Main)
    : NPC(EntityType.PLAYER, entityId, loc, manager, main) {

    override val metadata = MetadataStorage(this)
    override val uuid: UUID = spawnInfo.profile.uuid
    override val npcType = NPCType.Player

    override fun sendSpawnPackets(players: Set<Player>) {
        val infoData = PlayerInfoData(
            uuid, 0,
            spawnInfo.isListed, NativeGameMode.CREATIVE,
            spawnInfo.profile, WrappedChatComponent.fromText(spawnInfo.displayName),
            null as WrappedRemoteChatSessionData?
        )
        val infoPacket = PacketContainer(PacketType.Play.Server.PLAYER_INFO)
        infoPacket.modifier.writeDefaults()
        infoPacket.playerInfoActions.write(0, setOf(PlayerInfoAction.ADD_PLAYER, PlayerInfoAction.UPDATE_LISTED))
        infoPacket.playerInfoDataLists.write(1, listOf(infoData))

        val addEntityPacket = PacketContainer(PacketType.Play.Server.NAMED_ENTITY_SPAWN)
        addEntityPacket.modifier.writeDefaults()
        addEntityPacket.integers.write(0, entityId)
        addEntityPacket.uuiDs.write(0, uuid)
        addEntityPacket.doubles
            .write(0, location.x)
            .write(1, location.y)
            .write(2, location.z)
        addEntityPacket.bytes
            .write(0, (location.pitch * (256.0F / 360.0F)).toInt().toByte())
            .write(1, (location.yaw * (256.0F / 360.0F)).toInt().toByte())

        val list = mutableListOf(infoPacket, addEntityPacket)
        KotlinBukkitRunnable {
            if (list.isEmpty()) {
                it.cancel()
                return@KotlinBukkitRunnable
            }

            players.forEach { p -> manager.protocol.sendServerPacket(p, list.removeFirst()) }
        }.runTaskTimer(main, 0, 1)
    }

    override fun sendDespawnPackets(players: Set<Player>) {
        //Remove old info
        val removeInfoPacket = PacketContainer(PacketType.Play.Server.PLAYER_INFO_REMOVE)
        removeInfoPacket.modifier.writeDefaults()
        removeInfoPacket.uuidLists.write(0, listOf(uuid))

        //Remove entity
        val removeEntityPacket = PacketContainer(PacketType.Play.Server.ENTITY_DESTROY)
        removeEntityPacket.modifier.writeDefaults()
        removeEntityPacket.intLists.write(0, listOf(entityId))

        val list = mutableListOf(removeEntityPacket, removeEntityPacket)
        KotlinBukkitRunnable {
            if (list.isEmpty()) {
                it.cancel()
                return@KotlinBukkitRunnable
            }

            players.forEach { p -> manager.protocol.sendServerPacket(p, list.removeFirst()) }
        }.runTaskTimer(main, 0, 1)
    }

    override fun sendLocationPacket(players: Set<Player>) {
        val locPacket = PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT)
        locPacket.modifier.writeDefaults()
        locPacket.integers.write(0, entityId)
        locPacket.doubles
            .write(0, location.x)
            .write(1, location.y)
            .write(2, location.z)
        locPacket.bytes
            .write(0, (location.pitch * (256.0F / 360.0F)).toInt().toByte())
            .write(1, (location.yaw * (256.0F / 360.0F)).toInt().toByte())

        players.forEach { manager.protocol.sendServerPacket(it, locPacket) }
    }
}