package io.github.mh321productions.serverapi.module.config;

import io.github.mh321productions.serverapi.Main;
import io.github.mh321productions.serverapi.api.APIImplementation;
import io.github.mh321productions.serverapi.api.SubPlugin;
import io.github.mh321productions.serverapi.module.Module;
import io.github.mh321productions.serverapi.module.ModuleStopFunction;
import io.github.mh321productions.serverapi.module.ModuleType;

public class ConfigModule extends Module {

	protected ConfigModule(Main plugin, APIImplementation api) {
		super(ModuleType.Config, plugin, api);
	}

	@Override
	protected boolean init() {
		return false;
	}

	@Override
	protected void stopIntern() {

	}

	@Override
	public boolean registerSubPlugin(SubPlugin sub, ModuleStopFunction func) {
		return addIntern(sub, func);
	}

	@Override
	public void unregisterSubPlugin(SubPlugin sub) {
		removeIntern(sub);
	}

}
