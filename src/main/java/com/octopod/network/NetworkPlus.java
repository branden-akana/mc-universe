package com.octopod.network;

import com.google.gson.Gson;
import com.octopod.network.cache.NetworkPlayerCache;
import com.octopod.network.events.EventManager;
import com.octopod.network.util.BukkitUtils;
import com.octopod.network.util.RequestUtils;
import lilypad.client.connect.api.Connect;
import lilypad.client.connect.api.request.impl.GetPlayersRequest;
import lilypad.client.connect.api.request.impl.RedirectRequest;
import lilypad.client.connect.api.result.StatusCode;
import lilypad.client.connect.api.result.impl.GetPlayersResult;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Octopod
 *         Created on 3/13/14
 */
public class NetworkPlus {

    /**
     * A prefix to use before server messages.
     * TODO: add this to NetworkConfig
     */
    private static final String messagePrefix = "&8[&6Net+&8] &7";

    /**
     * An instance of Gson. Instead of always making new instances, just use this one.
     */
    private static final Gson gson = new Gson();

    /**
     * The current instance of NetworkPlus.
     */
    private static NetworkPlus instance;

    /**
     * The current instance of the NetworkPlusPlugin.
     */
    private static NetworkPlusPlugin plugin;

    /**
     * Gets the current instance of Gson.
     * @return
     */
    public static Gson gson() {return gson;}

    public static String prefix() {return messagePrefix;}

    public static boolean isLoaded() {
        return (plugin != null);
    }

    public static NetworkPlus getInstance() {
        return instance;
    }

    public static NetworkPlusPlugin getPlugin() {
        return plugin;
    }

    /**
     * Gets this plugin's username on LilyPad.
     * @return This plugin's username.
     */
    public static String getUsername() {
        return getConnection().getSettings().getUsername();
    }

    public static boolean isTestBuild() {
        return getPluginVersion().equals("TEST_BUILD");
    }

    public static String getPluginVersion() {
        return getPlugin().getDescription().getVersion();
    }

    public static NetworkLogger getLogger() {
        if(!isLoaded()) return null;
        return plugin.logger();
    }

    public static ServerInfo getServerInfo() {
        if(!isLoaded()) return null;
        return plugin.getServerInfo();
    }

    public static Connect getConnection() {
        if(!isLoaded()) return null;
        return plugin.getConnection();
    }

    public static File getDataFolder() {
        if(!isLoaded()) return null;
        return plugin.getDataFolder();
    }

    /**
     * Returns if LilyPad is connected or not.
     * @return true, if Lilypad is connected.
     */
    public static boolean isConnected() {
        return (getConnection() != null && getConnection().isConnected());
    }

    /**
     * Gets this plugin's event manager, which is used to register custom events.
     * @return Network's EventManager.
     */
    public static EventManager getEventManager() {
        return EventManager.getManager();
    }

    //=========================================================================================//
    //  Player Cache methods
    //=========================================================================================//

    /**
     * Gets all the players on the network as a Set.
     * @return The Set containing all the players, or an empty Set if the request somehow fails.
     */
    public Set<String> getNetworkedPlayers() {
        GetPlayersResult result = (GetPlayersResult)RequestUtils.request(new GetPlayersRequest(true));
        if(result.getStatusCode() == StatusCode.SUCCESS) {
            return result.getPlayers();
        } else {
            return new HashSet<>();
        }
    }

    /**
     * Gets all the players on the network according to the cache.
     * @return The Set containing all the players.
     */
    public Set<String> getCachedPlayers() {
        return NetworkPlayerCache.getPlayers();
    }

    /**
     * Gets if the player is online (on the entire network)
     * @param player The name of the player.
     * @return If the player is online.
     */
    public boolean isPlayerOnline(String player) {
        return getNetworkedPlayers().contains(player);
    }

    //=========================================================================================//
    //  Server Cache methods
    //=========================================================================================//

    /**
     * Gets if the server with this username is online.
     * @param server The username of the server.
     * @return If the server is online.
     */
    public boolean isServerOnline(String server) {
        return RequestUtils.sendMessage(server, "", "").getStatusCode() == StatusCode.SUCCESS;
    }

    //=========================================================================================//
    //  Request methods
    //=========================================================================================//

    public boolean sendPlayer(String player, String server) {
        return RequestUtils.request(new RedirectRequest(server, player)).getStatusCode() == StatusCode.SUCCESS;
    }

    /**
     * Tells a server (using this plugin) to broadcast a raw message.
     * @param message The message to send.
     */
    public void broadcastNetworkMessage(String server, String message) {
        RequestUtils.sendMessage(server, NetworkConfig.CHANNEL_BROADCAST, message);
    }

    /**
     * Tells every server (using this plugin) to broadcast a raw message.
     * @param message The message to send.
     */
    public void broadcastNetworkMessage(String message) {
        RequestUtils.broadcastMessage(NetworkConfig.CHANNEL_BROADCAST, message);
    }

    /**
     * Sends a raw message to a player. Works cross-server.
     * @param player The name of the player.
     * @param message The message to send.
     */
    public void sendNetworkMessage(String player, String message) {
        if(BukkitUtils.isPlayerOnline(player)) {
            BukkitUtils.sendMessage(player, message);
        } else {
            RequestUtils.broadcastMessage(NetworkConfig.CHANNEL_MESSAGE, gson().toJson(new PreparedPlayerMessage(player, message)));
        }
    }

    /**
     * Broadcasts a message to all servers telling them to send back their info.
     * This method should only be called only when absolutely needed, as the info returned never changes.
     * This might cause messages to be recieved on the CHANNEL_INFO_RESPONSE channel.
     */
    public void requestServerInfo() {
        getLogger().verbose("Requesting info from all servers");
        RequestUtils.broadcastMessage(NetworkConfig.CHANNEL_INFO_REQUEST, gson().toJson(getServerInfo()));
    }

    /**
     * Broadcasts a message to a list of servers telling them to send back their info.
     * This method should only be called only when absolutely needed, as the info returned never changes.
     * This might cause messages to be recieved on the CHANNEL_INFO_RESPONSE channel.
     * @param servers The list of servers to message.
     */
    public void requestServerInfo(List<String> servers) {
        getLogger().verbose("Requesting info from: &a" + servers);
        RequestUtils.sendMessage(servers, NetworkConfig.CHANNEL_INFO_REQUEST, gson().toJson(getServerInfo()));
    }

    /**
     * Broadcasts a message to a list of servers telling them to send back a list of their players.
     * This method should only be called only when absolutely needed, as the PlayerCache should automatically change it.
     * This might cause messages to be recieved on the CHANNEL_PLAYERLIST_RESPONSE channel.
     */
    public void requestPlayerList() {
        getLogger().verbose("Requesting playerlist from all servers");
        RequestUtils.broadcastMessage(NetworkConfig.CHANNEL_PLAYERLIST_REQUEST, gson().toJson(BukkitUtils.getPlayerNames()));
    }

    /**
     * Broadcasts a message telling every server to uncache a server.
     * @param server
     */
    public void requestUncache(String server) {
        RequestUtils.broadcastMessage(NetworkConfig.CHANNEL_UNCACHE, server);
    }
}
