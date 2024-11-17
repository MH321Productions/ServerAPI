package io.github.mh321productions.serverapi.module.log

import org.bukkit.plugin.Plugin
import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.api.APIImplementation
import io.github.mh321productions.serverapi.api.SubPlugin
import io.github.mh321productions.serverapi.module.Module
import io.github.mh321productions.serverapi.module.ModuleType
import java.io.IOException

/**
 * Das Logging-Modul erstellt für jedes registrierte Sub-Plugin ein eigenes
 * Logfile, welches automatisch über den internen Logger
 * ([Plugin.getLogger]) beschrieben wird. Wenn das Modul gestoppt
 * oder das Sub-Plugin entladen wird, wird das Logfile geschlossen.
 * Das Logfile ist unter `plugins/ServerAPI/logs/<Sub-Plugin Name>.txt`
 * zu finden.
 * @author 321Productions
 */
class LogModule(plugin: Main, api: APIImplementation) : Module(ModuleType.Logging, plugin, api) {

    private val logWrappers = mutableListOf<LogWrapper>()

    override fun initIntern() = true

    override fun stopIntern() = logWrappers.forEach { it.end() }

    private fun getWrapper(sub: SubPlugin) = logWrappers.firstOrNull { it.sub == sub }

    override fun registerSubPlugin(sub: SubPlugin, func: () -> Unit): Boolean {
        return try {
            val wrapper = LogWrapper(plugin, sub)
            logWrappers.add(wrapper)
            addIntern(sub, func)
            true
        } catch (_: IOException) {
            false
        }
    }

    override fun unregisterSubPlugin(sub: SubPlugin) {
        if (isSubPluginLoaded(sub)) {
            val wrapper = getWrapper(sub)
            if (wrapper != null) { //Sollte nicht passieren, aber sicher ist sicher
                wrapper.end()
                logWrappers.remove(wrapper)
                removeIntern(sub)
            }
        }
    }

    override fun isSubPluginLoaded(sub: SubPlugin): Boolean {
        return super.isSubPluginLoaded(sub) && getWrapper(sub) != null //Es wird auch geprüft, ob der Wrapper geladen ist
    }
}
