package io.github.mh321productions.serverapi.module.chat

import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.api.APIImplementation
import io.github.mh321productions.serverapi.api.SubPlugin
import io.github.mh321productions.serverapi.module.Module
import io.github.mh321productions.serverapi.module.ModuleType
import io.github.mh321productions.serverapi.module.chat.command.ChatCommand
import io.github.mh321productions.serverapi.module.chat.command.MsgCommand
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerChangedWorldEvent

class ChatModule(plugin: Main, api: APIImplementation) : Module(ModuleType.Chat, plugin, api), Listener {

    private var registeredChats = mutableMapOf<SubPlugin, MutableSet<Chat>>()
    private var playerChats = mutableMapOf<Player, Chat>()

    override fun initIntern(): Boolean {
        plugin.server.pluginManager.registerEvents(this, plugin)

        //Commands registrieren
        val cmd = ChatCommand(plugin, api, this)
        val msg = MsgCommand(plugin, api)
        plugin.cmd.registerCommand("chat", cmd)
        plugin.cmd.registerCommand("message", msg)

        return true
    }

    override fun stopIntern() {}
    override fun registerSubPlugin(sub: SubPlugin, func: () -> Unit): Boolean = addIntern(sub, func)
    override fun unregisterSubPlugin(sub: SubPlugin) = removeIntern(sub)

    /**
     * Registriert einen Chat, damit Spieler dem zugewiesen werden können
     * @param sub Das Sub-Plugin, dem der Chat gehört
     * @param chat Der Chat
     */
    fun registerChat(sub: SubPlugin, chat: Chat) {
        if (!registeredChats.containsKey(sub)) {
            registeredChats[sub] = mutableSetOf(chat)
        } else {
            registeredChats[sub]?.add(chat)
        }
    }

    /**
     * Schließt einen Chat und entfernt ihn
     * @param sub Das Sub-Plugin, dem der Chat gehört
     * @param chat Der Chat
     */
    fun unregisterChat(sub: SubPlugin, chat: Chat) {
        if (!registeredChats.containsKey(sub)) return

        val l = registeredChats[sub]!!
        if (!l.remove(chat)) return

        if (l.isEmpty()) registeredChats.remove(sub)
    }


    /**
     * Weist einem Spieler einen Chat zu, sprich: Die Nachrichten werden nur über diesen Chat gesendet. <br></br>
     * Wenn der übergebene Chat `null` ist, wird der Spieler dem globalen (normalen) Chat zugewiesen. <br></br>
     * Sollte der Spieler bereits in einem Chat registriert sein, wird er nur entfernt, wenn er vom neuen <br></br>
     * Chat akzeptiert wird (siehe [AbstractChat.canJoin]).
     * @param player Der Spieler
     * @param chat Der neue Chat, oder `null`
     * @return Ob die Zuweisung erfolgreich war
     */
    fun setPlayerChat(player: Player, chat: Chat?): Boolean {
        //Ist Spieler bereits in einem Chat? Ist der neue Chat nicht der globale Chat?
        val isAllowed = chat?.canJoin(player) ?: true //Bei validem Chat wird gecheckt, bei globalem Chat immer true

        //Ist der Spieler nicht vom Chat zugelassen, passiert nichts
        if (!isAllowed) return false

        if (playerChats.containsKey(player)) { //Bereits zugewiesen
            val old = playerChats[player]!!
            old.removeMember(player)

            if (chat != null) { //Neuer Chat -> Ersetze Eintrag
                chat.addMember(player)
                playerChats[player] = chat
            } else { //Globaler Chat -> Entferne Eintrag
                playerChats.remove(player)
            }

        } else if (chat != null) { //Nicht zugewiesen, neuer Chat -> Erstelle Eintrag
            chat.addMember(player)
            playerChats[player] = chat
        } //Wenn Der Spieler dem globalen Chat zugewiesen wird, aber in keinem ist, wird true zurückgegeben (es passiert nichts)

        return true
    }

    /**
     * Convenience-Funktion für [.setPlayerChat]: Ruft die Funktion mit `null` als Chat auf
     * @param player Der Spieler, der in den globalen Chat gesetzt werden soll
     */
    fun removePlayerChat(player: Player) {
        setPlayerChat(player, null)
    }

    /**
     * Sucht nach Chats
     * @param internalName Der interne Name des Chats
     * @return Der geladene Chat oder `null`
     */
    fun getChat(internalName: String) = registeredChats.values.flatten().firstOrNull { it.internalName == internalName }

    fun getVisibleChats(player: Player) = registeredChats.values.flatten().filter { it.canSee(player) }

    @EventHandler
    fun onChat(event: AsyncPlayerChatEvent) {
        val p = event.player
        val msg = event.message

        //Spieler ist in Chat, Event canceln und Chat übernehmen lassen
        if (playerChats.containsKey(p)) {
            event.isCancelled = true
            playerChats[p]!!.sendMessage(p, msg)
        }
    }

    @EventHandler
    fun onWorldChange(event: PlayerChangedWorldEvent) {
        val p = event.player
        if (playerChats.containsKey(p) && playerChats[p]!!.checkFlag(Chat.Flags.removeOnWorldChange)) removePlayerChat(p)
    }
}
