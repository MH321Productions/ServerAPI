package io.github.mh321productions.serverapi.module

import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.api.APIImplementation
import io.github.mh321productions.serverapi.api.SubPlugin
import java.lang.Module

/**
 * Die Superklasse aller API-Module. <br></br>
 * Der Zugriff erfolgt über [ServerAPI]
 * @author 321Productions
 */
abstract class Module protected constructor(
	val type: ModuleType<*>,
	protected val plugin: Main,
	protected val api: APIImplementation
) {

	var initialized = false
        private set

    protected val loadedSubPlugins = mutableMapOf<SubPlugin, () -> Unit>()
	protected val log = plugin.logger

    fun init(): Boolean {
        //Initialisieren
        log.info(String.format(startFormat, type.name))

        initialized = initIntern()
        if (initialized) log.info(String.format(startFormatSuccess, type.name))
        else log.severe(String.format(startFormatFailure, type.name))

        return initialized
    }

    /**
     * Internes initialisieren des Moduls
     */
    protected abstract fun initIntern(): Boolean

    /**
     * Interne Stoppfunktion des Moduls (siehe [Module.stop])
     */
    protected abstract fun stopIntern()

    /**
     * Registriert ein Sub-Plugin für das Modul.
     * @param sub Das Sub-Plugin
     * @param func Die Stoppfunktion (kann null sein)
     * @return Ob das Registrieren erfolgreich war (wenn nicht, wird das Sub-Plugin wieder entfernt)
     */
    abstract fun registerSubPlugin(sub: SubPlugin, func: () -> Unit = {}): Boolean

    /**
     * Entfernt ein geladenes Sub-Plugin (wenn es nicht vorher registriert ist, passiert nichts)
     * @param sub Das Sub-Plugin
     */
    abstract fun unregisterSubPlugin(sub: SubPlugin)

    /**
     * Fügt das Sub-Plugin intern zur Liste der geladenen Sub-Plugins hinzu.
     * @param sub Das Sub-Plugin
     * @param func Die Stoppfunktion
     * @return Ob das Hinzufügen erfolgreich war
     */
    protected fun addIntern(sub: SubPlugin, func: () -> Unit): Boolean {
        loadedSubPlugins[sub] = func
        api.addSub(sub)

        return true
    }

    /**
     * Entfernt des Sub-Plugin aus der internen Liste.
     * @param sub Das Sub-Plugin
     */
    protected fun removeIntern(sub: SubPlugin) {
        if (loadedSubPlugins.containsKey(sub)) {
            loadedSubPlugins.remove(sub)
            api.removeSub(sub)
        }
    }

    /**
     * Entlädt das Modul in folgender Reihenfolge:
     *  1. Alle registrierten Sub-Plugins werden benachrichtigt, <br></br>
     * dass das Modul entladen wird (siehe [ModuleStopFunction])
     *  2. Die interne [Module.stopIntern] Stoppfunktion wird aufgerufen, <br></br>
     * um das Modul artgerecht zu entladen
     *  3. Alle geladenen Sub-Plugins werden entfernt (erst hier, <br></br>
     * damit das Modul noch mit den Sub-Plugins interagieren kann)
     *
     */
    fun stop() {
        log.info(String.format(stopFormat, type.name))

        for (func in loadedSubPlugins.values) func()
        stopIntern()
        loadedSubPlugins.clear()

        log.info(String.format(stopFormatSuccess, type.name))
    }

    /**
     * Gibt zurück, ob ein Sub-Plugin im Modul registriert ist. Diese Methode kann von Subklassen erweitert werden.
     * @param sub Das abzufragende Sub-Plugin
     * @return Ob das Plugin registriert ist
     */
    open fun isSubPluginLoaded(sub: SubPlugin): Boolean {
        return loadedSubPlugins.containsKey(sub)
    }

    companion object {
        private const val startFormat = "Lade %s-Modul"
        private const val startFormatSuccess = "%s-Modul erfolgreich geladen"
        private const val startFormatFailure = "%s-Modul konnte nicht geladen werden"
        private const val stopFormat = "Stoppe %s-Modul"
        private const val stopFormatSuccess = "%s-Modul erfolgreich gestoppt"

        @JvmField
        val emptyFunction: () -> Unit = {}
    }
}
