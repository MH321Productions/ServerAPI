package io.github.mh321productions.serverapi.module.realtime;

import java.time.LocalTime;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class RealtimeWorker extends BukkitRunnable {
	
	private RealtimeModule module;
	
	public RealtimeWorker(RealtimeModule module) {
		this.module = module;
	}
	
	@Override
	public void run() {
		if (module.worlds.isEmpty()) return;
		
		LocalTime t = LocalTime.now();
		
		//Formel: Stunden*1000 - 6000
		
		int secRaw = t.getMinute() * 60 + t.getSecond();
		int seconds = Math.round(secRaw / 3.6f);
		
		int timeRaw = t.getHour() * 1000 + seconds;
		int time = timeRaw - 6000;
		if (time < 0) time += 24000;
		
		for (World w: module.worlds) w.setTime(time);
	}

}
