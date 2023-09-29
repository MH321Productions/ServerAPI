package io.github.mh321productions.serverapi.module.log;

import java.util.ArrayList;

import javax.annotation.Nullable;

import io.github.mh321productions.serverapi.Main;
import io.github.mh321productions.serverapi.api.APIImplementation;
import io.github.mh321productions.serverapi.api.SubPlugin;
import org.bukkit.plugin.Plugin;

import io.github.mh321productions.serverapi.module.Module;
import io.github.mh321productions.serverapi.module.ModuleStopFunction;
import io.github.mh321productions.serverapi.module.ModuleType;
import org.jetbrains.annotations.NotNull;

/**
 * Das Logging-Modul erstellt für jedes registrierte Sub-Plugin ein eigenes <br/>
 * Logfile, welches automatisch über den internen Logger <br/>
 * ({@link Plugin#getLogger()}) beschrieben wird. Wenn das Modul gestoppt <br/>
 * oder das Sub-Plugin entladen wird, wird das Logfile geschlossen. <br/>
 * Das Logfile ist unter "plugins/ServerAPI/logs/&lt;Sub-Plugin Name&gt;.txt" <br/>
 * zu finden.
 * @author 321Productions
 */
public class LogModule extends Module {
	
	private ArrayList<LogWrapper> logWrappers = new ArrayList<>();

	public LogModule(Main plugin, APIImplementation api) {
		super(ModuleType.Logging, plugin, api);
	}

	@Override
	protected boolean init() {
		return true;
	}

	@Override
	protected void stopIntern() {
		for (LogWrapper wrapper: logWrappers) wrapper.end();
	}
	
	private LogWrapper getWrapper(SubPlugin sub) {
		for (LogWrapper w: logWrappers) if (w.sub == sub) return w;
		
		return null;
	}
	
	public boolean registerSubPlugin(@NotNull SubPlugin sub, @Nullable ModuleStopFunction func) {
		LogWrapper wrapper = new LogWrapper(plugin, sub);
		boolean result = wrapper.init();
		
		if (result) {
			logWrappers.add(wrapper);
			addIntern(sub, func);
		}
		
		return result;
	}
	
	public void unregisterSubPlugin(@Nullable SubPlugin sub) {
		if (isSubPluginLoaded(sub)) {
			LogWrapper wrapper = getWrapper(sub);
			if (wrapper != null) { //Sollte nicht passieren, aber sicher ist sicher
				wrapper.end();
				logWrappers.remove(wrapper);
				removeIntern(sub);
			}
		}
	}
	
	@Override
	public boolean isSubPluginLoaded(SubPlugin sub) {
		return super.isSubPluginLoaded(sub) && getWrapper(sub) != null; //Es wird auch geprüft, ob der Wrapper geladen ist
	}
}
