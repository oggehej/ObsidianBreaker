package com.creeperevents.oggehej;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerListener implements Listener
{
	ObsidianBreaker plugin;
	PlayerListener(ObsidianBreaker instance)
	{
		this.plugin = instance;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInterect(PlayerInteractEvent event)
	{
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			Player player = event.getPlayer();
			if(player.getItemInHand().getTypeId() == plugin.getConfig().getInt("DurabilityChecker"))
				if(player.hasPermission("obsidianbreaker.test"))
					try {
						Block block = event.getClickedBlock();
						String durability = ObsidianMath.smartRound(plugin.storage.getTotalDurability(block));
						String durabilityLeft = ObsidianMath.smartRound(plugin.storage.getRemainingDurability(block));
						player.sendMessage(ChatColor.GOLD + "Durability: " + durabilityLeft + " of " + durability);
					} catch (UnknownBlockTypeException e) {}
		}
	}
}
