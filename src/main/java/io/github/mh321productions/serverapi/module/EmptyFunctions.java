package io.github.mh321productions.serverapi.module;

import javax.annotation.Nullable;

/**
 * Die Standardimplementierung der {@link ModuleStopFunction}, die genutzt wird, sollte ein <br/>
 * übergebener {@link ModuleStopFunction}-Parameter null sein.
 * @author 321Productions
 */
public final class EmptyFunctions implements ModuleStopFunction {
	
	//Implementierte Leere Funktionen
	@Override
	public void onStop() {}
	
	
	
	//Andere Funktionen
	public static final EmptyFunctions instance = new EmptyFunctions();
	
	/**
	 * Prüft, ob eine {@link ModuleStopFunction} null ist
	 * @param func Die zu prüfende Funktion
	 * @return Die Funktion, sonst {@link EmptyFunctions#instance}
	 */
	public static ModuleStopFunction checkNullModule(@Nullable ModuleStopFunction func) {
		if (func != null) return func;
		
		return instance;
	}
}
