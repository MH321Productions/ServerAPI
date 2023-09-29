package io.github.mh321productions.serverapi.module.npc.npc

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot.*
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction
import com.comphenix.protocol.wrappers.Pair
import com.comphenix.protocol.wrappers.PlayerInfoData
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedGameProfile
import com.comphenix.protocol.wrappers.WrappedRemoteChatSessionData
import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.module.npc.NPCManager
import io.github.mh321productions.serverapi.module.npc.metadata.MetadataStorage
import io.github.mh321productions.serverapi.util.functional.KotlinBukkitRunnable
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot

class EntityMorphNPC(target: Player, type: EntityType, val listInTablist: Boolean, manager: NPCManager, main: Main) : MorphNPC(target, type, manager, main) {

    override val metadata = MetadataStorage(this)
    override val npcType = NPCType.MorphEntity

    override fun sendSpawnPackets(players: Set<Player>) {
        //Remove Info
        val removeInfoPacket = PacketContainer(PacketType.Play.Server.PLAYER_INFO_REMOVE)
        removeInfoPacket.modifier.writeDefaults()
        removeInfoPacket.uuidLists.write(0, listOf(target.uniqueId))

        //Remove Entity
        val removePacket = PacketContainer(PacketType.Play.Server.ENTITY_DESTROY)
        removePacket.modifier.writeDefaults()
        removePacket.intLists.write(0, listOf(target.entityId))

        //Spawn Entity
        val spawnPacket = PacketContainer(PacketType.Play.Server.SPAWN_ENTITY)
        spawnPacket.modifier.writeDefaults()
        spawnPacket.integers
            .write(0, target.entityId)
            .write(1, 0).write(2, 0).write(3, 0) //Entity speed
            .write(4, 0) //Extra entity data
        spawnPacket.uuiDs.write(0, uuid)
        spawnPacket.doubles
            .write(0, target.location.x)
            .write(1, target.location.y)
            .write(2, target.location.z)
        spawnPacket.bytes
            .write(0, (target.location.pitch * (256.0F / 360.0F)).toInt().toByte())
            .write(1, (target.location.yaw * (256.0F / 360.0F)).toInt().toByte())
            .write(2, (target.location.yaw * (256.0F / 360.0F)).toInt().toByte())
        spawnPacket.entityTypeModifier.write(0, type)

        //Update equipment
        val equipmentPacket = PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT)
        equipmentPacket.modifier.writeDefaults()
        equipmentPacket.integers.write(0, target.entityId)

        val equipment = target.equipment

        if (equipment != null) {
            val data = ItemSlot.entries
                .map { Pair(it, equipment.getItem(toBukkitSlot(it))) }
                .toList()

            equipmentPacket.slotStackPairLists.write(0, data)
        }

        players
            .filter { it != target }
            .forEach {
                if (!listInTablist) manager.protocol.sendServerPacket(it, removeInfoPacket)
                manager.protocol.sendServerPacket(it, removePacket)
                manager.protocol.sendServerPacket(it, spawnPacket)
                manager.protocol.sendServerPacket(it, equipmentPacket)
            }
    }

    override fun sendDespawnPackets(players: Set<Player>) {
        //New info
        val profile = WrappedGameProfile.fromPlayer(target)
        val infoDataCreate = PlayerInfoData(
            target.uniqueId, target.ping,
            true, NativeGameMode.fromBukkit(target.gameMode),
            profile, WrappedChatComponent.fromText(target.displayName),
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

        val list = mutableListOf(addInfoPacket, addEntityPacket)
        KotlinBukkitRunnable {
            if (list.isEmpty()) {
                it.cancel()
                return@KotlinBukkitRunnable
            }

            players
                .filter { p -> p != target }
                .forEach { p -> manager.protocol.sendServerPacket(p, list.removeFirst()) }
        }.runTaskTimer(main, 0, 1)
    }

    private fun toBukkitSlot(slot: ItemSlot): EquipmentSlot {
        return when(slot) {
            MAINHAND -> EquipmentSlot.HAND
            OFFHAND -> EquipmentSlot.OFF_HAND
            FEET -> EquipmentSlot.FEET
            HEAD -> EquipmentSlot.HEAD
            LEGS -> EquipmentSlot.LEGS
            CHEST -> EquipmentSlot.CHEST
        }
    }
}