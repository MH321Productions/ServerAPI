package io.github.mh321productions.serverapi.util.testing

import com.google.common.collect.Lists
import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.api.APIImplementation
import io.github.mh321productions.serverapi.command.APISubCommand
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.IOException
import java.net.URI
import java.net.URL
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class ResourcePackTest(plugin: Main, api: APIImplementation) :
    APISubCommand(plugin, api) {
    private val link =
        "https://github.com/MH321Productions/AndyChi.aternos.me-MurderMinigame/raw/master/Murder%20Resource%20Pack.zip"

    override fun executeIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>): Boolean {
        if (!isPlayer) {
            sender.sendMessage("Nur Spieler können diesen Command ausführen")
            return true
        } else if (args.isEmpty()) return false

        val player = sender as Player

        if (args[0].equals(Companion.sub[0], ignoreCase = true)) { //set
            player.sendMessage("Setze Resource Pack")
            try {
                val url = URI(link).toURL()
                val sha = MessageDigest.getInstance("SHA1")
                val hash = sha.digest(url.openStream().readAllBytes())
                player.setResourcePack(link, hash)
            } catch (e: IOException) {
                player.sendMessage("Das Pack konnte nicht geladen werden: " + e.javaClass.simpleName + " -> " + e.localizedMessage)
                player.setResourcePack(link)
                e.printStackTrace()
            } catch (e: NoSuchAlgorithmException) {
                player.sendMessage("Das Pack konnte nicht geladen werden: " + e.javaClass.simpleName + " -> " + e.localizedMessage)
                player.setResourcePack(link)
                e.printStackTrace()
            }
        } else if (args[0].equals(Companion.sub[1], ignoreCase = true)) { //reset
            player.sendMessage("Setze Resource Pack zurück")
            player.setResourcePack("https://www.google.com")
        }

        return true
    }

    override fun tabIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>): List<String> {
        if (!isPlayer) return emptyList

        val ausgabe = ArrayList<String>()

        if (args.size == 1) for (s in Companion.sub) if (s.lowercase(Locale.getDefault()).startsWith(
                args[0].lowercase(Locale.getDefault())
            )
        ) ausgabe.add(s)

        return ausgabe
    }

    companion object {
        private val sub: ArrayList<String> = Lists.newArrayList("set", "reset")
    }
}
