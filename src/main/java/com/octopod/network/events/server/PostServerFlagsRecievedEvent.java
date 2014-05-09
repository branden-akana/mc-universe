package com.octopod.network.events.server;

import com.octopod.network.ServerFlags;
import com.octopod.network.events.Event;

/**
 * Runs after the ServerInfo was cached.
 */
public class PostServerFlagsRecievedEvent extends Event {

	String server;
    ServerFlags serverInfo;

	public PostServerFlagsRecievedEvent(ServerFlags serverInfo) {
		this.server = serverInfo.getUsername();
        this.serverInfo = serverInfo;
	}

	public String getServer() {
		return server;
	}

	public ServerFlags getServerInfo() {
		return serverInfo;
	}

}