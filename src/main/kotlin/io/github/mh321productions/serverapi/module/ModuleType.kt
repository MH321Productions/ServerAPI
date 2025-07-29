package io.github.mh321productions.serverapi.module

import io.github.mh321productions.serverapi.module.chat.ChatModule
import io.github.mh321productions.serverapi.module.config.ConfigModule
import io.github.mh321productions.serverapi.module.friend.FriendModule
import io.github.mh321productions.serverapi.module.log.LogModule
import io.github.mh321productions.serverapi.module.nick.NickModule
import io.github.mh321productions.serverapi.module.npc.NPCModule
import io.github.mh321productions.serverapi.module.realtime.RealtimeModule
import io.github.mh321productions.serverapi.module.report.ReportModule

class ModuleType<T: Module> private constructor(val name: String, val folder: String? = null) {

    companion object {

        /**
         * Das Logging-Modul erstellt für jedes Sub-Plugin ein eigenes Logfile
         * @see LogModule
         */
        @JvmField
        val Logging = ModuleType<LogModule>("Logging", "logs")

        /**
         * Mit dem Report-Modul können Spieler andere Spieler reporten
         * @see ReportModule
         */
        @JvmField
        val Report= ModuleType<ReportModule>("Report", "report")

        /**
         * Mit dem Chat-Modul können Sub-Plugins eigene Minichats erstellen
         * @see ChatModule
         */
        @JvmField
        val Chat = ModuleType<ChatModule>("Chat")

        /**
         * Mit dem Config-Modul können Sub-Plugins verschiedene Konfigurationen für jeden Spieler speichern
         * @see ConfigModule
         */
        @JvmField
        val Config = ModuleType<ConfigModule>("Config", "config")

        /**
         * Mit dem Realtime-Modul haben registrierte Welten immer die aktuelle Serverzeit
         * @see RealtimeModule
         */
        @JvmField
        val Realtime = ModuleType<RealtimeModule>("Realtime")

        /**
         * Mit dem NPC-Modul können Plugins NPCs spawnen und verwalten
         * @see NPCModule
         */
        @JvmField
        val NPC = ModuleType<NPCModule>("NPC")

        /**
         * Mit dem Nick-Modul können sich Spieler Nicknames geben
         * @see NickModule
         */
        @JvmField
        val Nick = ModuleType<NickModule>("Nick")

        /**
         * Ein kleiner Wrapper um das [ConfigModule], um Freunde einfacher verwalten zu können
         * @see FriendModule
         */
        @JvmField
        val Friend = ModuleType<FriendModule>("Friend")

        /**
         * Alle Modularten in einem Array
         */
        @JvmField
        val values = arrayOf(Logging, Report, Chat, Config, Realtime, NPC, Nick, Friend)
    }

}