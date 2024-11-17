package io.github.mh321productions.serverapi.module.npc

import com.comphenix.protocol.ProtocolManager
import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.api.SubPlugin
import io.github.mh321productions.serverapi.module.npc.npc.*
import io.github.mh321productions.serverapi.module.npc.npc.EntityMorphNPC
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.lang.IllegalArgumentException
import kotlin.jvm.Throws

class NPCManager(private val main: Main, private val sub: SubPlugin, private val module: NPCModule) {

    val npcs = mutableListOf<NPC>()
    val morphedPlayers = mutableSetOf<Player>()
    val protocol: ProtocolManager = main.protocol

    /**
     * Removes a loaded npc and frees its entityID (and the morphed player, if neccessary)
     */
    fun unregisterNPC(npc: NPC) {
        npc.despawn()
        npcs.remove(npc)
        module.freeEntityID(npc.entityId)

        if (npc.npcType.isMorphed()) {
            val morph = npc as MorphNPC
            morphedPlayers.remove(morph.target)
        }
    }

    /**
     * Unregisters all loaded npcs and frees their entityIDs (and the morphed players, if neccessary)
     */
    fun unregisterAll() {
        npcs.forEach { it.despawn() }
        module.freeEntityID(npcs.map { it.entityId })

        npcs.clear()
        morphedPlayers.clear()
    }

    /**
     * Creates and registers a new entity NPC. It has to be spawned manually.
     * @see createPlayerNPC
     * @see createMorphEntityNPC
     * @see createMorphPlayerNPC
     */
    fun createEntityNPC(type: EntityType, loc: Location) : NPC {
        if (type == EntityType.PLAYER) throw IllegalArgumentException("Only Non-Player entities are allowed in this function")

        val npc = EntityNPC(type, module.requestEntityID(), loc, this, main)
        npcs.add(npc)

        return npc
    }

    /**
     * Creates and registers a new player NPC. It has to be spawned manually
     * @see createEntityNPC
     * @see createMorphEntityNPC
     * @see createMorphPlayerNPC
     */
    fun createPlayerNPC(spawnInfo: PlayerSpawnInfo, loc: Location) : NPC {
        val npc = PlayerNPC(spawnInfo, module.requestEntityID(), loc, this, main)
        npcs.add(npc)

        return npc
    }

    /**
     * Creates and registers a new morphed entity NPC. It has to be spawned manually
     * @see createPlayerNPC
     * @see createEntityNPC
     * @see createMorphPlayerNPC
     * @throws IllegalArgumentException When the player is already morphed
     */
    @Throws(IllegalArgumentException::class)
    fun createMorphEntityNPC(target: Player, type: EntityType, listInTablist : Boolean) : NPC {
        if (module.isPlayerMorphed(target)) throw IllegalArgumentException("The player is already morphed")

        val npc = EntityMorphNPC(target, type, listInTablist, this, main)
        npcs.add(npc)

        return npc
    }

    /**
     * Creates and registers a new morphed player NPC. It has to be spawned manually
     * @see createPlayerNPC
     * @see createEntityNPC
     * @see createMorphEntityNPC
     * @throws IllegalArgumentException When the player is already morphed
     */
    @Throws(IllegalArgumentException::class)
    fun createMorphPlayerNPC(target: Player, spawnInfo: PlayerSpawnInfo) : NPC {
        if (module.isPlayerMorphed(target)) throw IllegalArgumentException("The player is already morphed")

        val npc = PlayerMorphNPC(target, spawnInfo, this, main)
        npcs.add(npc)

        return npc
    }
}