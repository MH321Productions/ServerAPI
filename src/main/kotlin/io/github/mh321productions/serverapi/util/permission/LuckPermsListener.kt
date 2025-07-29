package io.github.mh321productions.serverapi.util.permission

import io.github.mh321productions.serverapi.Main
import net.luckperms.api.LuckPerms
import net.luckperms.api.event.EventBus
import net.luckperms.api.event.EventSubscription
import net.luckperms.api.event.LuckPermsEvent
import net.luckperms.api.event.group.GroupDataRecalculateEvent
import net.luckperms.api.event.user.UserDataRecalculateEvent
import org.bukkit.plugin.java.JavaPlugin

class LuckPermsListener(private val plugin: Main, private val permissionHandler: PermissionHandler, lp: LuckPerms) {

    private val log = plugin.logger
    private val eventBus = lp.eventBus

    fun registerEvents() {
        log.info("Registering LuckPerms listeners")

        eventBus.subscribe<GroupDataRecalculateEvent>(plugin, this::onGroupReload)
        eventBus.subscribe<UserDataRecalculateEvent>(plugin, this::onUserReload)
    }

    private fun onGroupReload(event: GroupDataRecalculateEvent) {
        permissionHandler.ranks
            .find { it.group == event.group }
            ?.loadPermissions()
    }

    private fun onUserReload(event: UserDataRecalculateEvent) {

    }

    private inline fun <reified E: LuckPermsEvent> EventBus.subscribe(plugin: JavaPlugin, noinline func: (E) -> Unit): EventSubscription<E> {
        return subscribe(plugin, E::class.java, func)
    }
}