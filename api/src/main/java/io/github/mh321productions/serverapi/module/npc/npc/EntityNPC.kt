package io.github.mh321productions.serverapi.module.npc.npc

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.module.npc.NPC
import io.github.mh321productions.serverapi.module.npc.NPCManager
import io.github.mh321productions.serverapi.module.npc.metadata.MetadataStorage
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.util.UUID

/**
 * An NPC for any entity (**except** Players)
 * @see PlayerNPC
 */
class EntityNPC(type: EntityType, entityId: Int, loc: Location, manager: NPCManager, main: Main) : NPC(type, entityId, loc, manager, main) {

    override val metadata = MetadataStorage(this)
    override val uuid: UUID = UUID.randomUUID()
    override val npcType = NPCType.Entity

    override fun sendSpawnPackets(players: Set<Player>) {
        val spawnPacket = PacketContainer(PacketType.Play.Server.SPAWN_ENTITY)
        spawnPacket.modifier.writeDefaults()
        spawnPacket.integers
            .write(0, entityId)
            .write(1, 0).write(2, 0).write(3, 0) //Entity speed
            .write(4, 0) //Extra entity data
        spawnPacket.uuiDs.write(0, uuid)
        spawnPacket.doubles
            .write(0, location.x)
            .write(1, location.y)
            .write(2, location.z)
        spawnPacket.bytes
            .write(0, (location.pitch * (256.0F / 360.0F)).toInt().toByte())
            .write(1, (location.yaw * (256.0F / 360.0F)).toInt().toByte())
            .write(2, (location.yaw * (256.0F / 360.0F)).toInt().toByte())
        spawnPacket.entityTypeModifier.write(0, type)

        players.forEach { manager.protocol.sendServerPacket(it, spawnPacket) }
    }

    override fun sendDespawnPackets(players: Set<Player>) {
        val destroyPacket = PacketContainer(PacketType.Play.Server.ENTITY_DESTROY)
        destroyPacket.modifier.writeDefaults()
        destroyPacket.intLists.write(0, listOf(entityId))

        players.forEach { manager.protocol.sendServerPacket(it, destroyPacket) }
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