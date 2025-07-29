package io.github.mh321productions.serverapi.util.functional

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.future
import org.bukkit.plugin.Plugin
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

fun <T> coroutineFuture(action: suspend () -> T): CompletableFuture<T> =
    CoroutineScope(Dispatchers.IO).future {
        action()
    }

fun <T> CompletableFuture<T>.whenCompleteServerSync(action: (T) -> Unit, plugin: Plugin) {
    val executor = Executor { runnable -> plugin.server.scheduler.runTask(plugin, runnable) }
    thenAcceptAsync(action, executor)
}