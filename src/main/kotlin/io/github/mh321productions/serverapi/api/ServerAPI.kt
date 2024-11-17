package io.github.mh321productions.serverapi.api

import io.github.mh321productions.serverapi.module.Module
import io.github.mh321productions.serverapi.module.ModuleType
import io.github.mh321productions.serverapi.util.permission.PermissionHandler
import org.bukkit.entity.Player

/**
 * Das Hauptinterface der Plugin-API. <br></br>
 * Von hier aus können sich die Sub-Plugins in die verschiedenen Module registrieren, <br></br>
 * um mit der API zu kommunizieren und zu arbeiten.
 *
 * @author 321Productions
 */
interface ServerAPI {
    /**
     * Gibt alle geladenen Module zurück
     * @return Eine Liste aller geladenen Module
     */
    val loadedModules: List<Module>

    /**
     * Fragt ab, ob ein Modul geladen und betriebsbereit ist
     * @param type Der Typ des abzufragenden Moduls
     * @return Ob das besagte Modul geladen ist
     */
    fun isModuleLoaded(type: ModuleType<*>): Boolean

    /**
     * Gibt ein geladenes Modul zurück, wenn es geladen ist. <br></br>
     * Hier empfiehlt sich, [ServerAPI.isModuleLoaded] <br></br>
     * zu nutzen, um einer [NullPointerException] vorzubeugen.
     * @param <T> Die Modulklasse
     * @param type Der Typ des gewünschten Moduls
     * @return Das geladene Modul, sonst null
     */
    fun <T : Module> getModule(type: ModuleType<T>): T

    /**
     * Gibt den internen [PermissionHandler] zurück
     * @return Der Handler
     */
    val permissionHandler: PermissionHandler

    /**
     * Returns whether a player is nicked
     * @apiNote This method forwards to [NickModule.isPlayerNicked]
     */
    fun isPlayerNicked(player: Player): Boolean

    /**
     * Returns the players nickname or its normal name when not nicked.
     * This method should be used for name callouts in messages etc
     * @apiNote This method forwards to [NickModule.getNickname]
     */
    fun getNickname(player: Player): String
}
