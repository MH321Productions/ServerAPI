package io.github.mh321productions.serverapi.util.permission

/**
 * Eine Sammlung der Ränge auf unserem Server
 * @author 321Productions
 */
enum class DefaultRank(val rankName: String) {
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

    ModPlus("modSr"),

    Admin("admin"),

    Nicked("nicked");

    companion object {
        fun fromRank(rank: Rank): DefaultRank? = entries.find { it.rankName == rank.name }
    }
}
