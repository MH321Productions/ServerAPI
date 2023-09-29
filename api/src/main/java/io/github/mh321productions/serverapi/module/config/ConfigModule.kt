package io.github.mh321productions.serverapi.module.config

import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.api.APIImplementation
import io.github.mh321productions.serverapi.api.SubPlugin
import io.github.mh321productions.serverapi.module.Module
import io.github.mh321productions.serverapi.module.ModuleStopFunction
import io.github.mh321productions.serverapi.module.ModuleType

class ConfigModule (plugin: Main?, api: APIImplementation?) : Module(ModuleType.Config, plugin, api) {

    override fun init(): Boolean {
        return false
    }

    override fun stopIntern() {

    }

    override fun registerSubPlugin(sub: SubPlugin, func: ModuleStopFunction?) = addIntern(sub, func)

    override fun unregisterSubPlugin(sub: SubPlugin?) = removeIntern(sub)
}
