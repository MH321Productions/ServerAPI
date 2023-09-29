package io.github.mh321productions.serverapi.api;

import java.util.List;
import javax.annotation.Nullable;

import io.github.mh321productions.serverapi.module.Module;
import io.github.mh321productions.serverapi.module.ModuleType;
import io.github.mh321productions.serverapi.util.permission.PermissionHandler;
import io.github.mh321productions.serverapi.module.nick.NickModule;
import org.bukkit.entity.Player;

/**
 * Das Hauptinterface der Plugin-API. <br/>
 * Von hier aus können sich die Sub-Plugins in die verschiedenen Module registrieren, <br/>
 * um mit der API zu kommunizieren und zu arbeiten.
 * 
 * @author 321Productions
 */
public interface ServerAPI {
	
	/**
	 * Gibt alle geladenen Module zurück
	 * @return Eine Liste aller geladenen Module
	 */
    List<Module> getLoadedModules();
	
	/**
	 * Fragt ab, ob ein Modul geladen und betriebsbereit ist
	 * @param type Der Typ des abzufragenden Moduls
	 * @return Ob das besagte Modul geladen ist
	 */
    boolean isModuleLoaded(ModuleType<?> type);
	
	/**
	 * Gibt ein geladenes Modul zurück, wenn es geladen ist. <br/>
	 * Hier empfiehlt sich, {@link ServerAPI#isModuleLoaded(ModuleType)} <br/>
	 * zu nutzen, um einer {@link NullPointerException} vorzubeugen.
	 * @param <T> Die Modulklasse
	 * @param type Der Typ des gewünschten Moduls
	 * @return Das geladene Modul, sonst null
	 */
	@Nullable
    <T extends Module> T getModule(ModuleType<T> type);

	/**
	 * Gibt den internen {@link PermissionHandler} zurück
	 * @return Der Handler
	 */
    PermissionHandler getPermissionHandler();

	/**
	 * Returns whether a player is nicked
	 * @apiNote This method forwards to {@link NickModule#isPlayerNicked(Player)}
	 */
	boolean isPlayerNicked(Player player);

	/**
	 * Returns the players nickname or its normal name when not nicked.
	 * This method should be used for name callouts in messages etc
	 * @apiNote This method forwards to {@link NickModule#getNickname(Player)}
	 */
	String getNickname(Player player);
}
