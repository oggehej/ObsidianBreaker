package com.creeperevents.oggehej;

import org.bukkit.plugin.java.JavaPlugin;

public class ObsidianDestroyer extends JavaPlugin
{
	public void onEnable()
	{
		getServer().getPluginManager().registerEvents(new BlockEvents(this), this);
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
}
