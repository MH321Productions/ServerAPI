package io.github.mh321productions.serverapi.module.npc

import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.module.npc.metadata.MetadataStorage
import io.github.mh321productions.serverapi.util.visibility.StandardVisibilityGroups.EmptyVisibilityGroup
import io.github.mh321productions.serverapi.util.visibility.VisibilityGroup
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.util.UUID

/**
 * Die Basisklasse eines spawnbaren NPCs
 */
abstract class NPC(val type: EntityType, val entityId: Int, loc: Location, protected val manager: NPCManager, protected val main: Main) {

    enum class NPCType {
        Entity,

        Player,

        MorphEntity,

        MorphPlayer;

        fun isMorphed() : Boolean {
            return when (this) {
                MorphEntity, MorphPlayer -> true
                else -> false
            }
        }
    }

    open var location = loc
        set(value) {
            field = value
            sendLocationPacket(visibilityGroup.getActivePlayers())
        }

    /**
     * The [VisibilityGroup] of this NPC. Changing it will immediately take effect
     */
    var visibilityGroup : VisibilityGroup = EmptyVisibilityGroup()
        set(value) {
            changeVisibilityGroup(field, value)
            field = value
        }
    private val visibilityListener = VisibilityGroup.VisibilityListener({ sendSpawnPackets(setOf(it)) }, { sendDespawnPackets(setOf(it)) })
    abstract val metadata : MetadataStorage
    abstract val uuid : UUID
    abstract val npcType : NPCType
    private var active = false

    fun isActive() = active

    /**
     * Spawns the NPC
     * @return If it was successful
     */
    fun spawn() {
        if (!active) {
            active = true
            sendSpawnPackets(visibilityGroup.getActivePlayers())
        }
    }

    fun despawn() {
        if (active) {
            active = false
            sendDespawnPackets(visibilityGroup.getActivePlayers())
        }
    }

    fun updateMetadata() {
        if (active && metadata.isDirty()) {
            visibilityGroup.getActivePlayers().forEach { manager.protocol.sendServerPacket(it, metadata.generatePacket()) }
        }
    }

    protected abstract fun sendSpawnPackets(players: Set<Player>)
    protected abstract fun sendDespawnPackets(players: Set<Player>)
    protected abstract fun sendLocationPacket(players: Set<Player>)

    //Internal
    private fun changeVisibilityGroup(from: VisibilityGroup, to: VisibilityGroup) {
        from.removeListener(visibilityListener)
        to.addListener(visibilityListener)

        sendDespawnPackets(from.getActivePlayers())
        sendSpawnPackets(to.getActivePlayers())
    }
}