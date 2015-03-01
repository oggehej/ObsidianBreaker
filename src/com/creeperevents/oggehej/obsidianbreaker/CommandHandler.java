package com.creeperevents.oggehej.obsidianbreaker;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandHandler implements CommandExecutor
{
	private ObsidianBreaker plugin;
	CommandHandler(ObsidianBreaker instance)
	{
		plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(args.length == 0)
		{
			sender.sendMessage(ChatColor.AQUA + " -- [" + plugin.getName() + " v" + plugin.getDescription().getVersion() + "] --");
			if(sender.hasPermission("obsidianbreaker.reload"))
				sender.sendMessage(ChatColor.GOLD + "/" + label + " reload" + ChatColor.WHITE + " - Reload the config");
		}
		else if(args[0].equalsIgnoreCase("reload"))
			if(sender.hasPermission("obsidianbreaker.reload"))
			{
				plugin.saveConfig();
				plugin.reloadConfig();
				sender.sendMessage(ChatColor.GOLD + "Config reloaded!");
			}
			else
				sender.sendMessage(ChatColor.RED + "No permission!");
		else
			sender.sendMessage(ChatColor.RED + "Invalid arguments!");
		return true;
	}
}
