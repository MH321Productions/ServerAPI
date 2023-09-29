package io.github.mh321productions.serverapi.module.npc.npc

import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.module.npc.NPC
import io.github.mh321productions.serverapi.module.npc.NPCManager
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.util.*

abstract class MorphNPC(val target: Player, type: EntityType, manager: NPCManager, main: Main)
    : NPC(type, target.entityId, target.location, manager, main) {

    override val uuid = target.uniqueId
    override var location: Location
        get() = target.location
        set(value) {
            target.teleport(value)
        }

    override fun sendLocationPacket(players: Set<Player>) {}
}