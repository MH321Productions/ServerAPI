package io.github.mh321productions.serverapi.util.message

import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent

/**
 * Diese Klasse definiert ein Präfix, welches vom [MessageFormatter] <br></br>
 * verwendet wird, um Chat-Messages an Spieler zu vereinheitlichen. <br></br>
 * Jedes Sub-Plugin kann eigene Präfixe erstellen und ohne Registrierung <br></br>
 * direkt nutzen.
 * @author 321Productions
 */
class MessagePrefix(prefix: String, format: PrefixFormat) {
    /**
     * Ein Enum, das die verschiedenen Präfixformate enthält
     * @author 321Productions
     */
    enum class PrefixFormat {
        Main,
        /**
         * Ein Nebenpräfix, das meist mit anderen Präfixen kombiniert wird: §8[&lt;Präfix&gt;§8]§r
         */

        Secondary,
        /**
         * Keine spezielle Formatierung
         */
        None
    }

    /**
     * Ein Hauptpräfix, das standardmäßig im Chat gesendet werden: &ltPräfix&gt §8»§r
     */

    companion object {
        private const val mainFormat = " §8»§r"
        private const val secondaryFormat = "§8[§r%s§8]§r"

        /**
         * Das Server-Präfix: <br></br>
         * <span style="color: #5555FF; font-size: 30">Server</span> <span style="color: #555555; font-size: 30">» </span>...
         */
        @JvmField
        val Server: MessagePrefix = MessagePrefix("§9Server", PrefixFormat.Main)

        /**
         * Das [+]-Präfix: <br></br>
         * <span style="color: #555555; font-size: 30">[</span><span style="color:#55FF55; font-size: 30; font-weight:bold">+</span><span style="color: #555555; font-size: 30">]</span> ...
         */
        val Plus: MessagePrefix = MessagePrefix("§a§l+", PrefixFormat.Secondary)

        /**
         * Das [-]-Präfix: <br></br>
         * <span style="color: #555555; font-size: 30">[</span><span style="color:#FF5555; font-size: 30; font-weight:bold">-</span><span style="color: #555555; font-size: 30">]</span> ...
         */
        val Minus: MessagePrefix = MessagePrefix("§c§l-", PrefixFormat.Secondary)

        /**
         * Ein leeres Präfix
         */
        val Plain: MessagePrefix = MessagePrefix("", PrefixFormat.None)
    }

    val simplePrefix: String = when (format) {
        PrefixFormat.Main -> prefix + mainFormat
        PrefixFormat.Secondary -> String.format(secondaryFormat, prefix)
        else -> prefix
    }

    val spigotPrefix: BaseComponent = TextComponent(simplePrefix)
}
