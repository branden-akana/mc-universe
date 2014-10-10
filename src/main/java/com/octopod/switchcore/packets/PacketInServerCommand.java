package com.octopod.switchcore.packets;

/**
 * @author Octopod - octopodsquad@gmail.com
 */

/**
 * This will tell a server to run a command (as the console)
 */
public class PacketInServerCommand extends SwitchPacket
{
	String command;

	public PacketInServerCommand(String command)
	{
		this.command = command;
	}

	public String getCommand()
	{
		return command;
	}
}
