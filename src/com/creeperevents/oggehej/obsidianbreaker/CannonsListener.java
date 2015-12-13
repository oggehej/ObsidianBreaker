package com.creeperevents.oggehej.obsidianbreaker;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import at.pavlov.cannons.event.ProjectilePiercingEvent;

/**
 * Listener used for the Cannons plugin
 * 
 * @author oggehej
 */
public class CannonsListener implements Listener {
	ObsidianBreaker plugin;
	CannonsListener(ObsidianBreaker plugin) {
		this.plugin = plugin;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onProjectilePiercing(ProjectilePiercingEvent event) {
		for(Block block : event.getBlockList()) {
			if(plugin.getStorage().isValidBlock(block)) {
				plugin.blockListener.explodeBlock(block.getLocation(), event.getImpactLocation(), null);
			}
		}
	}
}
