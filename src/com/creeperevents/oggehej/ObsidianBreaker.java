package com.creeperevents.oggehej;

import java.util.Map.Entry;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ObsidianBreaker extends JavaPlugin
{
	private BlockListener blockListener;
	private PlayerListener playerListener;
	StorageHandler storage;
	
	public void onEnable()
	{
		blockListener = new BlockListener(this);
		playerListener = new PlayerListener(this);
		storage = new StorageHandler(this);
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(blockListener, this);
		pm.registerEvents(playerListener, this);
		
		getConfig().options().copyDefaults(true);
		saveConfig();		
		
		long freq = (long) (getConfig().getDouble("Regen.Frequency") * 20 * 60);
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				for(Entry<String, BlockStatus> val : storage.damage.entrySet())
				{
					if(val.getValue().isModified())
						val.getValue().setModified(false);
					else
					{
						val.getValue().setDamage(val.getValue().getDamage() - getConfig().getDouble("Regen.Amount"));
						if(val.getValue().getDamage() < 0.00001D)
							storage.damage.remove(val.getKey());							
					}
				}
			}
		}.runTaskTimerAsynchronously(this, freq, freq);
	}
}
