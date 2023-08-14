package io.github.mh321productions.serverapi.module.realtime;

import java.util.ArrayList;
import java.util.Collection;

import io.github.mh321productions.serverapi.Main;
import io.github.mh321productions.serverapi.api.APIImplementation;
import io.github.mh321productions.serverapi.api.SubPlugin;
import org.bukkit.World;

import io.github.mh321productions.serverapi.module.Module;
import io.github.mh321productions.serverapi.module.ModuleStopFunction;
import io.github.mh321productions.serverapi.module.ModuleType;

public class RealtimeModule extends Module {
	
	private RealtimeWorker worker = null;
	ArrayList<World> worlds;

	public RealtimeModule(Main plugin, APIImplementation api) {
		super(ModuleType.Realtime, plugin, api);
	}

	@Override
	protected boolean init() {
		worlds = new ArrayList<>();
		worker = new RealtimeWorker(this);
		worker.runTaskTimer(plugin, 0, 20);
		
		return true;
	}

	@Override
	protected void stopIntern() {
		if (worker != null) worker.cancel();
	}

	@Override
	public boolean registerSubPlugin(SubPlugin sub, ModuleStopFunction func) {
		return addIntern(sub, func);
	}

	@Override
	public void unregisterSubPlugin(SubPlugin sub) {
		removeIntern(sub);
	}
	
	public void addWorld(World w) {
		if (w != null) worlds.add(w);
	}
	
	public void addWorld(Collection<World> w) {
		if (w != null) worlds.addAll(w);
	}
	
	public void removeWorld(World w) {
		worlds.remove(w);
	}
	
	public void removeWorld(Collection<World> w) {
		if (w != null) worlds.removeAll(w);
	}

}
