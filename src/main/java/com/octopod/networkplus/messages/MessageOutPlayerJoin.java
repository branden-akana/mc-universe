package com.octopod.networkplus.messages;

import com.octopod.minecraft.MinecraftPlayer;
import com.octopod.networkplus.StaticChannel;

/**
 * @author Octopod - octopodsquad@gmail.com
 */

/**
 * This will tell a server that a player has joined this server.
 */
public class MessageOutPlayerJoin extends NetworkMessage
{
	String UUID;

	public MessageOutPlayerJoin(MinecraftPlayer player)
	{
		UUID = player.getUUID();
	}

	@Override
	public String[] getMessage()
	{
		return new String[]{UUID};
	}

	@Override
	public String getChannelOut()
	{
		return StaticChannel.PLAYER_JOIN_SERVER.toString();
	}
}
