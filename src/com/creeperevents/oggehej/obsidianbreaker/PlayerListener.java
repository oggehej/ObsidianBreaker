package com.creeperevents.oggehej.obsidianbreaker;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerListener implements Listener
{
	private ObsidianBreaker plugin;
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
			if(player.getItemInHand().getTypeId() == plugin.getConfig().getInt("DurabilityChecker") && player.hasPermission("obsidianbreaker.test"))
				try {
					Block block = event.getClickedBlock();
					if(!(plugin.getStorage().getTotalDurability(block) < 0))
					{
						// Will round our numbers so they can be printed
						DecimalFormat format = new DecimalFormat("##.##");
						DecimalFormatSymbols symbol = new DecimalFormatSymbols();
						symbol.setDecimalSeparator('.');
						format.setDecimalFormatSymbols(symbol);

						String durability = format.format(plugin.getStorage().getTotalDurability(block));
						String durabilityLeft = format.format(plugin.getStorage().getRemainingDurability(block));
						player.sendMessage(ChatColor.GOLD + "Durability: " + durabilityLeft + " of " + durability);
					}
					else
						player.sendMessage(ChatColor.GOLD + "Durability: Unlimited");
				} catch (UnknownBlockTypeException e) {}
		}
	}
}
