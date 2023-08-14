package io.github.mh321productions.serverapi.util.permission;

/**
 * Eine Sammlung der Ränge auf unserem Server
 * @author 321Productions
 *
 */
public enum DefaultRank {
	Default("default"),
	
	Premium("premium"),
	
	YouTube("youtube"),
	
	Creator("creator"),
	
	VIP("vip"),
	
	Team("team"),
	
	BuilderTest("buildertest"),
	
	Builder("builder"),
	
	BuilderPlus("builder+"),
	
	DevTest("devtest"),
	
	Dev("dev"),
	
	DevPlus("dev+"),
	
	Sup("sup"),
	
	SupPlus("sup+"),
	
	Mod("mod"),
	
	ModSr("modSr"),
	
	Admin("admin"),
	
	Nicked("nicked");
	
	
	public final String name;
	
	private DefaultRank(String internalName) {
		name = internalName;
	}
	
	public static DefaultRank fromRank(Rank rank) {
		for (DefaultRank r: values()) if (rank.equals(r)) return r;
		
		return null;
	}
}
