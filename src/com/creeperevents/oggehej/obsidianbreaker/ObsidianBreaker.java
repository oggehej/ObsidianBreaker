package com.creeperevents.oggehej.obsidianbreaker;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.mcstats.MetricsLite;

public class ObsidianBreaker extends JavaPlugin
{
	@SuppressWarnings("unused")
	private static File LANG_FILE;

	private BlockListener blockListener;
	private PlayerListener playerListener;
	private StorageHandler storage;

	public void onEnable()
	{
		blockListener = new BlockListener(this);
		playerListener = new PlayerListener(this);
		storage = new StorageHandler(this);

		// Register events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(blockListener, this);
		pm.registerEvents(playerListener, this);

		// Load configuration file
		getConfig().options().copyDefaults(true);
		saveConfig();
		setupLocale();

		// Initialise metrics
		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		} catch (IOException e) {}

		// Initialise command
		getCommand("obsidianbreaker").setExecutor(new CommandHandler(this));

		// Initialise block regeneration
		// Configuration can be set to a negative frequency in order to disable
		long freq = getConfig().getLong("Regen.Frequency") * 20 * 60;
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
						val.getValue().setDamage(val.getValue().getDamage() - (float) getConfig().getDouble("Regen.Amount"));
						if(val.getValue().getDamage() < 0.001f)
							storage.damage.remove(val.getKey());							
					}
				}
			}
		}.runTaskTimerAsynchronously(this, freq, freq);
	}

	/**
	 * Get the storage handler of ObsidianBreaker
	 * 
	 * @return Storage handler
	 */
	public StorageHandler getStorage()
	{
		return storage;
	}

	/**
	 * Load the lang.yml file.
	 * @return The lang.yml config.
	 */
	public void setupLocale()
	{
		File lang = new File(getDataFolder(), "lang.yml");

		if (!lang.exists())
			try
			{
				lang.createNewFile();
			}
			catch(IOException e)
			{
				e.printStackTrace();
				Bukkit.getLogger().severe("[" + getName() + "] Couldn't create language file. Disabling.");
				setEnabled(false);
			}

		YamlConfiguration conf = YamlConfiguration.loadConfiguration(lang);

		for(Locale item : Locale.values())
			if (conf.getString(item.name()) == null)
				conf.set(item.name(), item.getDefault());

		Locale.setFile(conf);

		try {
			conf.save(lang);
		} catch(IOException e) {
			Bukkit.getLogger().warning("Failed to save lang.yml");
			e.printStackTrace();
		}
	}
}
