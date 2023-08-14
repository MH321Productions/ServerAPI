package io.github.mh321productions.serverapi.module.chat.standardchats;

import java.util.ArrayList;

import io.github.mh321productions.serverapi.util.message.MessageBuilder;
import org.bukkit.entity.Player;

import io.github.mh321productions.serverapi.util.message.Message;
import io.github.mh321productions.serverapi.util.message.MessagePrefix;

/**
 * Eine abstrakte Subklasse von {@link AbstractChat}, die
 * Präfixfunktionalität hinzufügt
 * @see MessagePrefix
 * @author 321Productions
 *
 */
public abstract class PrefixChat extends AbstractChat {
	
	private ArrayList<MessagePrefix> prefix;
	protected MessageBuilder builder;

	public PrefixChat(byte flags, String name, ArrayList<MessagePrefix> prefixes) {
		super(flags, name);
		prefix = prefixes;
	}

	@Override
	protected Message formatMessage(Player sender, String message) {
		builder = new MessageBuilder().setPrefixes(prefix);
		formatAfterPrefix(message, sender);
		return builder.build();
	}
	
	protected abstract void formatAfterPrefix(String message, Player sender);
}
