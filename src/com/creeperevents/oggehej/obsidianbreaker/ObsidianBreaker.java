package com.creeperevents.oggehej.obsidianbreaker;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.mcstats.MetricsLite;

import com.creeperevents.oggehej.obsidianbreaker.nms.NMS;

public class ObsidianBreaker extends JavaPlugin
{
	private BlockListener blockListener;
	private PlayerListener playerListener;
	private StorageHandler storage;
	private NMS nmsHandler;
	private BukkitTask crackRunner;
	BukkitTask regenRunner;

	public void onEnable() {
		blockListener = new BlockListener(this);
		playerListener = new PlayerListener(this);
		storage = new StorageHandler(this);

		// Initialise NMS class
		setupNMS();

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

		// Schedule runners
		scheduleRegenRunner();
		scheduleCrackCheck();
	}

	public void onDisable() {
		storage = null;
		blockListener = null;
		playerListener = null;
	}

	/**
	 * Get the storage handler of ObsidianBreaker
	 * 
	 * @return Storage handler
	 */
	public StorageHandler getStorage() {
		return storage;
	}

	/**
	 * Load/reload the lang.yml file.
	 */
	void setupLocale() {
		File lang = new File(getDataFolder(), "lang.yml");

		if (!lang.exists())
			try{
				lang.createNewFile();
			} catch(IOException e) {
				e.printStackTrace();
				Bukkit.getLogger().severe("[" + getName() + "] Couldn't create language file!");
				return;
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

	/**
	 * Return the {@code NMS} handler for the current version
	 * 
	 * @return {@code NMS} handler
	 */
	public NMS getNMS() {
		return this.nmsHandler;
	}

	/**
	 * Set up the NMS functions
	 */
	private void setupNMS() {
		String packageName = this.getServer().getClass().getPackage().getName();
		String version = packageName.substring(packageName.lastIndexOf('.') + 1);

		try {
			final Class<?> clazz = Class.forName(getClass().getPackage().getName() + ".nms." + version);
			if (NMS.class.isAssignableFrom(clazz)) {
				getLogger().info("Using NMS version " + version);
				this.nmsHandler = (NMS) clazz.getConstructor().newInstance();
			}
		} catch (final Exception e) {
			getLogger().info("Couldn't find support for " + version +". Block cracks not activated.");
			class Dummy implements NMS {
				@Override
				public void sendCrackEffect(Block block, int damage) {}
				public boolean isDummy() {return true;}
			}
			this.nmsHandler = new Dummy();
		}
	}

	void scheduleCrackCheck() {
		if(crackRunner != null) {
			crackRunner.cancel();
			crackRunner = null;
		}

		if(getConfig().getBoolean("BlockCracks.Enabled"))
			crackRunner = new CrackRunnable(this).runTaskTimerAsynchronously(this, 0, getConfig().getLong("BlockCracks.Interval") * 20);
	}

	void scheduleRegenRunner() {
		if(regenRunner != null) {
			regenRunner.cancel();
			regenRunner = null;
		}

		// Configuration can be set to a negative frequency in order to disable
		long freq = getConfig().getLong("Regen.Frequency") * 20 * 60;
		if(freq > 0) {
			regenRunner = new BukkitRunnable() {
				@Override
				public void run() {
					try {
						for(Entry<String, BlockStatus> val : storage.damage.entrySet()) {
							if(val.getValue().isModified())
								val.getValue().setModified(false);
							else {
								val.getValue().setDamage(val.getValue().getDamage() - (float) getConfig().getDouble("Regen.Amount"));
								if(val.getValue().getDamage() < 0.001f)
									storage.damage.remove(val.getKey());							
							}
						}
					} catch(Exception e) {
						System.out.println("[ObsidianBreaker] Error occured while trying to regen block (task "+getTaskId()+")");
						e.printStackTrace();
					}
				}
			}.runTaskTimerAsynchronously(this, freq, freq);
		}
	}

	static class CrackRunnable extends BukkitRunnable {
		ObsidianBreaker plugin;
		CrackRunnable(ObsidianBreaker plugin) {
			this.plugin = plugin;
		}

		@Override
		public void run() {
			try {
				for(String hash : plugin.getStorage().damage.keySet()) {
					Location loc = plugin.getStorage().generateLocation(hash);
					plugin.getStorage().renderCracks(loc.getBlock());
				}
			} catch(Exception e) {
				System.out.println("[ObsidianBreaker] Error occured while trying to show block cracks (task "+getTaskId()+")");
				e.printStackTrace();
			}
		}
	}
}
