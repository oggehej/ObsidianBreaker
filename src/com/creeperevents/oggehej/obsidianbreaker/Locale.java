package com.creeperevents.oggehej.obsidianbreaker;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public enum Locale {
	DURABILITY("&6Durability:"),
	DURABILITY_LEFT("&3{0} &6of &3{1}"),
	UNLIMITED("&3Unlimited"),
	NO_PERMISSION("&4No permission!"),
	INVALID_ARGUMENTS("&4Invalid arguments!"),
	CONFIG_RELOADED("&6Config reloaded!"),
	RELOAD_CONFIG("Reload the config");

	private String def;
	private static YamlConfiguration LANG;

	Locale(String def) {
		this.def = def;
	}

	/**
	 * Set the {@code YamlConfiguration} to use
	 * 
	 * @param config The configuration to set
	 */
	static void setFile(YamlConfiguration config) {
		LANG = config;
	}

	/**
	 * @return Formatted {@code String}
	 */
	@Override
	public String toString() {
		try {
			return ChatColor.translateAlternateColorCodes('&', LANG.getString(name()));
		} catch(Exception e) {
			return def;
		}
	}

	/**
	 * Get the default value of the path
	 * 
	 * @return The default value of the path
	 */
	public String getDefault() {
		return this.def;
	}
}
