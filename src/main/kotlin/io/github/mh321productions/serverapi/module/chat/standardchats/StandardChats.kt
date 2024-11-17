package io.github.mh321productions.serverapi.module.chat.standardchats

import io.github.mh321productions.serverapi.api.ServerAPI
import io.github.mh321productions.serverapi.api.SubPlugin
import io.github.mh321productions.serverapi.module.chat.Chat
import io.github.mh321productions.serverapi.util.formatting.StringFormatter.formatPlayerName
import io.github.mh321productions.serverapi.util.message.Message
import io.github.mh321productions.serverapi.util.message.MessageBuilder
import io.github.mh321productions.serverapi.util.message.MessagePrefix
import io.github.mh321productions.serverapi.util.permission.Rank
import org.bukkit.World
import org.bukkit.entity.Player
import kotlin.experimental.or

/**
 * Eine abstrakte Subklasse von [AbstractChat], die Präfixfunktionalität hinzufügt
 * @see MessagePrefix
 *
 * @author 321Productions
 */
abstract class PrefixChat(flags: Byte, name: String, private val prefix: List<MessagePrefix>) : AbstractChat(flags, name) {

    override fun formatMessage(sender: Player, message: String): Message {
        val builder = MessageBuilder().setPrefixes(prefix)
        formatAfterPrefix(builder, message, sender)
        return builder.build()
    }

    protected abstract fun formatAfterPrefix(builder: MessageBuilder, message: String, sender: Player)
}

/**
 * Eine abstrakte Subklasse von {@link PrefixChat}, die den normalen Chat nachbaut. <br/>
 * @author 321Productions
 */
abstract class StandardChat(flags: Byte, name: String, prefix: List<MessagePrefix>, protected val api: ServerAPI) : PrefixChat(flags, name, prefix) {

    protected val perms = api.permissionHandler

    override fun formatAfterPrefix(builder: MessageBuilder, message: String, sender: Player) {
        val r: Rank = perms.getHighestRank(sender)
        builder
            .addComponent(formatPlayerName(sender, r)) //Spielerrang
            .addComponent("§8: §f")
            .addComponent(message)
    }
}

/**
 * Eine Chatimplementation, die einen Weltchat nachbaut. <br/>
 * Bei den Flags wird {@link Flags#removeOnWorldChange} automatisch gesetzt.
 * @author 321Productions
 *
 */
open class WorldChat(flags: Byte, name: String, prefixes: List<MessagePrefix>, api: ServerAPI, private val world: World)
    : StandardChat(flags or Chat.Flags.removeOnWorldChange, name, prefixes, api) {

    override fun canSee(player: Player): Boolean {
        return if (checkFlag(Chat.Flags.forceChat)) false else (player.world == world)
    }

    override fun canJoin(player: Player): Boolean {
        return player.world == world
    }
}

open class PluginChat(flags: Byte, name: String, prefixes: List<MessagePrefix>, api: ServerAPI, private val sub: SubPlugin)
    : StandardChat(flags, name, prefixes, api) {

    override fun canSee(player: Player): Boolean {
        return if (checkFlag(Chat.Flags.forceChat)) false else sub.isPlayerInGame(player)
    }

    override fun canJoin(player: Player): Boolean {
        return sub.isPlayerInGame(player)
    }
}

/**
 * Eine [SubPlugin]-Subklasse, die die Flags [Flags.forceChat] und <br></br>
 * [Flags.removeOnWorldChange] automatisch setzt, um einen Totenchat <br></br>
 * zu implementieren
 * @author 321Productions
 */
open class DeathChat(name: String, prefixes: List<MessagePrefix>, api: ServerAPI, sub: SubPlugin) :
    PluginChat(Chat.Flags.forceChat or Chat.Flags.removeOnWorldChange, name, prefixes, api, sub)