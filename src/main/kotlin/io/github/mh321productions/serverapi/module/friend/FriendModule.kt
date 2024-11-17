package io.github.mh321productions.serverapi.module.friend

import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.api.APIImplementation
import io.github.mh321productions.serverapi.api.SubPlugin
import io.github.mh321productions.serverapi.module.Module
import io.github.mh321productions.serverapi.module.ModuleType
import io.github.mh321productions.serverapi.module.config.server.ServerFriendConfig
import io.github.mh321productions.serverapi.module.friend.command.FriendCommand
import io.github.mh321productions.serverapi.util.message.MessagePrefix
import org.bukkit.entity.Player
import java.util.*

/**
 * A wrapper module for the [ServerFriendConfig] entry
 */
class FriendModule(main: Main, api: APIImplementation) : Module(ModuleType.Friend, main, api) {

    companion object {
        val msgPrefix = MessagePrefix("§aFreunde", MessagePrefix.PrefixFormat.Main)
    }

    private val conf = api.getModule(ModuleType.Config)

    override fun initIntern() : Boolean {
        plugin.cmd.registerCommand("friend", FriendCommand(plugin, api, this))

        return true
    }

    override fun stopIntern() {}
    override fun registerSubPlugin(sub: SubPlugin, func: () -> Unit) = addIntern(sub, func)
    override fun unregisterSubPlugin(sub: SubPlugin) = removeIntern(sub)

    val maxNumberOfFriends : Int
        get() = plugin.conf.modules.friend.maxFriends

    /**
     * Returns the friends of an online player as an immutable list
     */
    fun getFriends(player: UUID) = conf
        .getPlayerConfig(player)
        .server.friends.friends
        .toList()

    fun getOutgoingRequests(player: UUID) = conf
        .getPlayerConfig(player)
        .server.friends.outgoingRequests
        .toList()

    fun getIncomingRequests(player: UUID) = conf
        .getPlayerConfig(player)
        .server.friends.incomingRequests
        .toList()

    /**
     * Tries to add a friend to a player (without sending a friend request).
     * It performs three checks:
     *
     * 1. Is there a friend limit? (passes on false)
     * 2. Has the player the maximum amount of friends? (passes on false)
     * 3. Can the player bypass the limit (permission api.friend.bypass-limit)? (passes on true)
     *
     * If any check passes, the friend is added.
     * This does **not** send messages to the players
     *
     * @return Whether the friend is added
     * @see addRequest
     */
    fun addFriend(player: UUID, newFriend: UUID) : Boolean {
        val own = conf.getPlayerConfig(player)
        val other = conf.getPlayerConfig(newFriend)
        val ownFriends = own.server.friends.friends
        val otherFriends = other.server.friends.friends

        if (maxNumberOfFriends > -1 && ownFriends.size >= maxNumberOfFriends && !canBypassFriendLimit(player)) return false
        else if (ownFriends.contains(newFriend)) return false
        else if (maxNumberOfFriends > -1 && otherFriends.size >= maxNumberOfFriends && !canBypassFriendLimit(newFriend)) return false

        ownFriends.add(newFriend)
        otherFriends.add(player)
        return true
    }

    /**
     * Tries to add a request from player to friendToRequest.
     * This does **not** send messages to the players
     *
     * @return true when the request could be made
     *
     * false when the request is already made
     *
     * or when friendToRequest has already sent a request to player
     */
    fun addRequest(player: UUID, friendToRequest: UUID) : Boolean {
        val own = conf.getPlayerConfig(player)
        val other = conf.getPlayerConfig(friendToRequest)
        val ownFriends = own.server.friends.friends
        val ownOut = own.server.friends.outgoingRequests
        val ownIn = own.server.friends.incomingRequests
        val otherIn = other.server.friends.incomingRequests

        if (ownOut.contains(friendToRequest) || ownIn.contains(friendToRequest) || ownFriends.contains(friendToRequest)) return false

        ownOut.add(friendToRequest)
        otherIn.add(player)

        return true
    }

    /**
     * Removes a friend from a player.
     * This does **not** send messages to the players
     *
     * @return Whether the friend is removed
     */
    fun removeFriend(player: UUID, friendToRemove: UUID) : Boolean {
        val own = conf.getPlayerConfig(player)
            .server.friends.friends
            .remove(friendToRemove)

        val other = conf.getPlayerConfig(friendToRemove)
            .server.friends.friends
            .remove(player)

        return own && other
    }

    fun removeRequest(player: UUID, requestToRemove: UUID) : Boolean {
        val own = conf.getPlayerConfig(player)
        val other = conf.getPlayerConfig(requestToRemove)
        val ownIn = own.server.friends.incomingRequests
        val ownOut = own.server.friends.outgoingRequests
        val otherIn = other.server.friends.incomingRequests
        val otherOut = other.server.friends.outgoingRequests

        return listOf(ownIn.remove(requestToRemove), ownOut.remove(requestToRemove), otherIn.remove(player), otherOut.remove(player))
            .reduce { a, b -> a || b }
    }

    fun hasFriend(player: UUID, friendToQuery: UUID) = conf
        .getPlayerConfig(player)
        .server.friends.friends
        .contains(friendToQuery)

    fun hasRequest(player: UUID, requestToQuery: UUID) : Boolean {
        val own = conf.getPlayerConfig(player)
        val incoming = own.server.friends.incomingRequests
        val outgoing = own.server.friends.outgoingRequests

        return listOf(incoming, outgoing)
            .flatten()
            .contains(requestToQuery)
    }

    fun hasIncomingRequest(player: UUID, requestToQuery: UUID) = conf
        .getPlayerConfig(player)
        .server.friends.incomingRequests
        .contains(requestToQuery)

    fun hasOutgoingRequest(player: UUID, requestToQuery: UUID) = conf
        .getPlayerConfig(player)
        .server.friends.outgoingRequests
        .contains(requestToQuery)

    /**
     * Checks whether a player can bypass a friend limit
     *
     * This queries the permission "api.friend.bypass-limit"
     */
    fun canBypassFriendLimit(player: Player) = canBypassFriendLimit(player.uniqueId)

    /**
     * Checks whether a player can bypass a friend limit
     *
     * This queries the permission "api.friend.bypass-limit"
     */
    fun canBypassFriendLimit(uuid: UUID) = plugin.perms.hasPermission(uuid, "api.friend.bypass-limit")
}