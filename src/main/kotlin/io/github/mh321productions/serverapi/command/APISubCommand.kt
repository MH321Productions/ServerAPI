package io.github.mh321productions.serverapi.command

import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.api.APIImplementation

abstract class APISubCommand(main: Main, @JvmField protected val api: APIImplementation) : SubCommand<Main>(main)