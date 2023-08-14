package io.github.mh321productions.serverapi.module.chat.standardchats;

import java.util.ArrayList;

import io.github.mh321productions.serverapi.api.ServerAPI;
import io.github.mh321productions.serverapi.api.SubPlugin;
import io.github.mh321productions.serverapi.util.message.MessagePrefix;

/**
 * Eine {@link SubPlugin}-Subklasse, die die Flags {@link Flags#forceChat} und <br/>
 * {@link Flags#removeOnWorldChange} automatisch setzt, um einen Totenchat <br/>
 * zu implementieren
 * @author 321Productions
 *
 */
public class DeathChat extends PluginChat {

	public DeathChat(String name, ArrayList<MessagePrefix> prefixes, ServerAPI api, SubPlugin sub) {
		super((byte) (Flags.forceChat | Flags.removeOnWorldChange), name, prefixes, api, sub);
	}

}
