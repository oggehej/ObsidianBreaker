package com.creeperevents.oggehej.obsidianbreaker;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import at.pavlov.cannons.event.ProjectilePiercingEvent;

public class CannonsListener implements Listener {
	ObsidianBreaker plugin;
	CannonsListener(ObsidianBreaker plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true)
	public void onProjectilePiercing(ProjectilePiercingEvent event) {
		for(Block block : event.getBlockList()) {
			if(plugin.getConfig().getConfigurationSection("Blocks").getKeys(false).contains(Integer.toString(block.getTypeId()))) {
				plugin.blockListener.explodeBlock(block.getLocation(), event.getImpactLocation(), null);
			}
		}
	}
}
