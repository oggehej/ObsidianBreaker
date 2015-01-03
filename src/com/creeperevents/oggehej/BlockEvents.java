package com.creeperevents.oggehej;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class BlockEvents implements Listener
{
	ObsidianDestroyer plugin;
	BlockEvents(ObsidianDestroyer instance)
	{
		this.plugin = instance;
	}
	
	HashMap<String, Double> map = new HashMap<String, Double>();

	@EventHandler(ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent event)
	{
		double unalteredRadius = plugin.getConfig().getDouble("BlastRadius");
		int radius = (int) Math.ceil(unalteredRadius);
		Location detonatorLoc = event.getLocation();

		for (int x = -radius; x <= radius; x++)
			for (int y = -radius; y <= radius; y++)
				for (int z = -radius; z <= radius; z++)
				{
					Location targetLoc = new Location(detonatorLoc.getWorld(),detonatorLoc.getX() + x, detonatorLoc.getY() + y,detonatorLoc.getZ() + z);
					if (detonatorLoc.distance(targetLoc) <= unalteredRadius)
					{
						explodeBlock(targetLoc, detonatorLoc);
					}
				}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event)
	{
		String hash = generateHash(event.getBlock().getLocation());
		if(map.containsKey(hash))
			map.remove(hash);
	}

	public void explodeBlock(Location loc, Location source)
	{
		Block block = loc.getWorld().getBlockAt(loc);
		if(block.getType() == Material.OBSIDIAN || block.getType() == Material.ENCHANTMENT_TABLE || block.getType() == Material.ENDER_CHEST)
		{
			String hashCode = generateHash(loc);
			
			double add;
			if(source.getBlock().isLiquid())
				add = 1 / plugin.getConfig().getDouble("LiquidMultiplier");
			else
				add = 1D;
			
			if(!map.containsKey(hashCode))
				map.put(hashCode, add);
			else
				map.put(hashCode, map.get(hashCode) + add);

			if(map.get(hashCode) >= plugin.getConfig().getDouble("BlockHits"))
			{
				block.setType(Material.AIR);
				map.remove(hashCode);
			}
		}
	}
	
	public String generateHash(Location loc)
	{
		return loc.getBlockX() + "-" + loc.getBlockY() + "-" + loc.getBlockZ();
	}
}
