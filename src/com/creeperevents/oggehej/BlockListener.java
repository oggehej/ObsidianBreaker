package com.creeperevents.oggehej;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class BlockListener implements Listener
{
	ObsidianBreaker plugin;
	public BlockListener(ObsidianBreaker instance)
	{
		this.plugin = instance;
	}

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
					Location targetLoc = new Location(detonatorLoc.getWorld(), detonatorLoc.getX() + x, detonatorLoc.getY() + y, detonatorLoc.getZ() + z);
					if (detonatorLoc.distance(targetLoc) <= unalteredRadius)
						explodeBlock(targetLoc, detonatorLoc);
				}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event)
	{
		plugin.storage.damage.remove(plugin.storage.generateHash(event.getBlock().getLocation()));
	}

	@SuppressWarnings("deprecation")
	private void explodeBlock(Location loc, Location source)
	{
		Block block = loc.getWorld().getBlockAt(loc);
		if(plugin.getConfig().getConfigurationSection("Blocks").contains(Integer.toString(block.getTypeId())))
			try
			{
				if(plugin.storage.addDamage(block, source.getBlock().isLiquid() ? 1 / plugin.getConfig().getDouble("LiquidMultiplier") : 1D))
					block.setType(Material.AIR);
			} catch (UnknownBlockTypeException e) {}
	}
}
