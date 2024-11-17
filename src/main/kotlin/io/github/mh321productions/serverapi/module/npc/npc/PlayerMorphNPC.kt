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
import io.github.mh321productions.serverapi.module.npc.NPCManager
import io.github.mh321productions.serverapi.module.npc.metadata.MetadataStorage
import io.github.mh321productions.serverapi.util.functional.KotlinBukkitRunnable
import org.bukkit.World
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.security.MessageDigest

class PlayerMorphNPC(target: Player, val spawnInfo: PlayerSpawnInfo, manager: NPCManager, main: Main) : MorphNPC(target, EntityType.PLAYER, manager, main) {

    override val metadata = MetadataStorage(this)
    override val npcType = NPCType.MorphPlayer

    override fun sendSpawnPackets(players: Set<Player>) {
        //Remove old info
        val removeInfoPacket = PacketContainer(PacketType.Play.Server.PLAYER_INFO_REMOVE)
        removeInfoPacket.modifier.writeDefaults()
        removeInfoPacket.uuidLists.write(0, listOf(uuid))

        //Remove entity
        val removeEntityPacket = PacketContainer(PacketType.Play.Server.ENTITY_DESTROY)
        removeEntityPacket.modifier.writeDefaults()
        removeEntityPacket.intLists.write(0, listOf(entityId))

        //New info
        val infoDataCreate = PlayerInfoData(
            target.uniqueId, target.ping,
            true, NativeGameMode.fromBukkit(target.gameMode),
            spawnInfo.profile, WrappedChatComponent.fromText(spawnInfo.displayName),
            WrappedRemoteChatSessionData.fromPlayer(target)
        )
        val addInfoPacket = PacketContainer(PacketType.Play.Server.PLAYER_INFO)
        addInfoPacket.modifier.writeDefaults()
        addInfoPacket.playerInfoActions.write(0, setOf(PlayerInfoAction.ADD_PLAYER, PlayerInfoAction.INITIALIZE_CHAT, PlayerInfoAction.UPDATE_LISTED))
        addInfoPacket.playerInfoDataLists.write(1, listOf(infoDataCreate))

        //Spawn player
        val addEntityPacket = PacketContainer(PacketType.Play.Server.NAMED_ENTITY_SPAWN)
        addEntityPacket.modifier.writeDefaults()
        addEntityPacket.integers.write(0, target.entityId)
        addEntityPacket.uuiDs.write(0, target.uniqueId)
        addEntityPacket.doubles
            .write(0, target.location.x)
            .write(1, target.location.y)
            .write(2, target.location.z)
        addEntityPacket.bytes
            .write(0, (target.location.pitch * (256.0F / 360.0F)).toInt().toByte())
            .write(1, (target.location.yaw * (256.0F / 360.0F)).toInt().toByte())

        val respawnPacket = PacketContainer(PacketType.Play.Server.RESPAWN)
        respawnPacket.modifier.writeDefaults()
        respawnPacket.worldKeys.write(0, target.world)
        respawnPacket.longs.write(0, hashWorldSeed(target.world))
        respawnPacket.gameModes.write(0, NativeGameMode.fromBukkit(target.gameMode))
        respawnPacket.booleans
            .write(0, false)
            .write(1, false)
        respawnPacket.bytes.write(0, 0)

        //Spawn
        val targetList = mutableListOf(removeInfoPacket, addInfoPacket, respawnPacket)
        val seeList = mutableListOf(removeInfoPacket, removeEntityPacket, addInfoPacket, addEntityPacket)
        val notSeeList = mutableListOf(removeInfoPacket, addInfoPacket)
        val seePlayers = players.filter { it.canSee((target)) }
        val notSeePlayers = players.filterNot { it.canSee(target) }

        KotlinBukkitRunnable {
            if (targetList.isEmpty()) {
                it.cancel()
                return@KotlinBukkitRunnable
            }

            manager.protocol.sendServerPacket(target, targetList.removeFirst())
        }.runTaskTimer(main, 0, 1)

        KotlinBukkitRunnable {
            if (seeList.isEmpty()) {
                it.cancel()
                return@KotlinBukkitRunnable
            }

            seePlayers.forEach { p -> manager.protocol.sendServerPacket(p, seeList.removeFirst()) }
        }.runTaskTimer(main, 0, 1)

        KotlinBukkitRunnable {
            if (notSeeList.isEmpty()) {
                it.cancel()
                return@KotlinBukkitRunnable
            }

            notSeePlayers.forEach { p -> manager.protocol.sendServerPacket(p, notSeeList.removeFirst()) }
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

        //New info
        val infoDataCreate = PlayerInfoData(
            uuid, target.ping,
            true, NativeGameMode.fromBukkit(target.gameMode),
            WrappedGameProfile.fromPlayer(target), WrappedChatComponent.fromText(target.displayName),
            WrappedRemoteChatSessionData.fromPlayer(target)
        )
        val addInfoPacket = PacketContainer(PacketType.Play.Server.PLAYER_INFO)
        addInfoPacket.modifier.writeDefaults()
        addInfoPacket.playerInfoActions.write(0, setOf(PlayerInfoAction.ADD_PLAYER, PlayerInfoAction.INITIALIZE_CHAT, PlayerInfoAction.UPDATE_LISTED))
        addInfoPacket.playerInfoDataLists.write(1, listOf(infoDataCreate))

        //Spawn player
        val addEntityPacket = PacketContainer(PacketType.Play.Server.NAMED_ENTITY_SPAWN)
        addEntityPacket.modifier.writeDefaults()
        addEntityPacket.integers.write(0, target.entityId)
        addEntityPacket.uuiDs.write(0, target.uniqueId)
        addEntityPacket.doubles
            .write(0, target.location.x)
            .write(1, target.location.y)
            .write(2, target.location.z)
        addEntityPacket.bytes
            .write(0, (target.location.pitch * (256.0F / 360.0F)).toInt().toByte())
            .write(1, (target.location.yaw * (256.0F / 360.0F)).toInt().toByte())

        val respawnPacket = PacketContainer(PacketType.Play.Server.RESPAWN)
        respawnPacket.modifier.writeDefaults()
        respawnPacket.worldKeys.write(0, target.world)
        respawnPacket.longs.write(0, hashWorldSeed(target.world))
        respawnPacket.gameModes.write(0, NativeGameMode.fromBukkit(target.gameMode))
        respawnPacket.booleans
            .write(0, false)
            .write(1, false)
        respawnPacket.bytes.write(0, 0)

        //Spawn
        val targetList = mutableListOf(removeInfoPacket, addInfoPacket, respawnPacket)
        val seeList = mutableListOf(removeInfoPacket, removeEntityPacket, addInfoPacket, addEntityPacket)
        val notSeeList = mutableListOf(removeInfoPacket, addInfoPacket)
        val seePlayers = players.filter { it.canSee((target)) }
        val notSeePlayers = players.filterNot { it.canSee(target) }

        KotlinBukkitRunnable {
            if (targetList.isEmpty()) {
                it.cancel()
                return@KotlinBukkitRunnable
            }

            manager.protocol.sendServerPacket(target, targetList.removeFirst())
        }.runTaskTimer(main, 0, 1)

        KotlinBukkitRunnable {
            if (seeList.isEmpty()) {
                it.cancel()
                return@KotlinBukkitRunnable
            }

            seePlayers.forEach { p -> manager.protocol.sendServerPacket(p, seeList.removeFirst()) }
        }.runTaskTimer(main, 0, 1)

        KotlinBukkitRunnable {
            if (notSeeList.isEmpty()) {
                it.cancel()
                return@KotlinBukkitRunnable
            }

            notSeePlayers.forEach { p -> manager.protocol.sendServerPacket(p, notSeeList.removeFirst()) }
        }.runTaskTimer(main, 0, 1)
    }

    private fun hashWorldSeed(world: World) : Long {
        val digest = MessageDigest.getInstance("SHA-256")
        val data = digest.digest(world.seed.toByteArray())

        return data.toLong()
    }

    private fun Long.toByteArray(): ByteArray {
        val bytes = ByteArray(8)
        bytes[7] = (this and 0xFF).toByte()
        bytes[6] = ((this ushr 8) and 0xFF).toByte()
        bytes[5] = ((this ushr 16) and 0xFF).toByte()
        bytes[4] = ((this ushr 24) and 0xFF).toByte()
        bytes[3] = ((this ushr 32) and 0xFF).toByte()
        bytes[2] = ((this ushr 40) and 0xFF).toByte()
        bytes[1] = ((this ushr 48) and 0xFF).toByte()
        bytes[0] = ((this ushr 56) and 0xFF).toByte()
        return bytes
    }

    private fun ByteArray.toLong() = toLong(0, 8)

    private fun ByteArray.toLong(startIndex: Int, endIndex: Int) : Long {
        return if (isEmpty() || startIndex >= size || startIndex == endIndex) 0
        else if (endIndex - startIndex > 8) convertToLong(copyOfRange(startIndex, startIndex + 8))
        else if (endIndex >= size) convertToLong(copyOfRange(startIndex, size))
        else if (startIndex > endIndex) convertToLong(copyOfRange(endIndex, startIndex))
        else convertToLong(copyOfRange(startIndex, endIndex))
    }

    private fun convertToLong(arr: ByteArray) : Long {
        return arr
            .map { it.toUByte().toLong() }
            .reduce { a, b -> ((a shl 8) or b) }
    }
}