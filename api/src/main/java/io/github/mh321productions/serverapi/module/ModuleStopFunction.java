package io.github.mh321productions.serverapi.module;

/**
 * Ein Funktionsinterface, das aufgerufen wird, wenn ein Modul gestoppt wird, <br/>
 * damit die registrierten Sub-Plugins die Arbeit ordnungsgemäß beenden können <br/>
 * (z.B. geladene Statistiken abspeichern).
 * @author 321Productions
 */
public interface ModuleStopFunction {
	
	/**
	 * Die Callback-Funktion, die aufgerufen wird, wenn das Modul gestoppt wird.
	 */
	public void onStop();
}