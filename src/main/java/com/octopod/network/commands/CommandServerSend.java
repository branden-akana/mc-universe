package com.octopod.network.commands;

import com.octopod.network.util.BukkitUtils;
import com.octopod.network.NetworkPermission;
import com.octopod.network.NetworkPlugin;
import org.bukkit.command.CommandSender;

public class CommandServerSend extends NetworkCommand {

	public CommandServerSend(String root) {
		super(root, "<command> <player> <server>", NetworkPermission.NETWORK_SERVER_SEND,

			"Sends a player to a server."

		);
	}

	Integer[] numArgs = new Integer[]{2};

	@Override
	public Integer[] numArgs() {
		return numArgs;
	}

	@Override
	protected boolean exec(CommandSender sender, String label, String[] args) {

		String player = args[0];
		String server = args[1];

		//Checks if the player is online.
		if(!NetworkPlugin.self.isPlayerOnline(player)) {
			BukkitUtils.sendMessage(sender, "&cThis player is not online.");
			return true;
		}

		//Checks if the server is online before sending them there.
		if(!NetworkPlugin.self.isServerOnline(server)) {
			BukkitUtils.sendMessage(sender, "&cThis server is offline or does not exist.");
			return true;
		}

		//Attempts to send them to the server
		BukkitUtils.sendMessage(sender, "&7Sending &6" + player + " &7to server &b" + server + "&7...");
		NetworkPlugin.self.sendPlayer(player, server);

		return true;

	}

}
