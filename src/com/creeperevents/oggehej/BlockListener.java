package com.creeperevents.oggehej;

import java.util.Iterator;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class BlockListener implements Listener
{
	private ObsidianBreaker plugin;
	BlockListener(ObsidianBreaker instance)
	{
		this.plugin = instance;
	}

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent event)
	{
		Iterator<Block> it = event.blockList().iterator();
		while(it.hasNext())
		{
			Block block = it.next();
			if(plugin.getConfig().getConfigurationSection("Blocks").getKeys(false).contains(Integer.toString(block.getTypeId())))
				it.remove();
		}

		double unalteredRadius = plugin.getConfig().getDouble("BlastRadius");
		int radius = (int) Math.ceil(unalteredRadius);
		Location detonatorLoc = event.getLocation();

		for (int x = -radius; x <= radius; x++)
			for (int y = -radius; y <= radius; y++)
				for (int z = -radius; z <= radius; z++)
				{
					Location targetLoc = new Location(detonatorLoc.getWorld(), detonatorLoc.getX() + x, detonatorLoc.getY() + y, detonatorLoc.getZ() + z);
					if (detonatorLoc.distance(targetLoc) <= unalteredRadius)
						explodeBlock(targetLoc, detonatorLoc, event.getEntityType());
				}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event)
	{
		StorageHandler storage = plugin.getStorage();
		storage.damage.remove(storage.generateHash(event.getBlock().getLocation()));
	}

	/**
	 * Explode a block
	 * 
	 * @param loc Location of the block
	 * @param source Location of the explosion source
	 * @param explisive The EntityType of the explosion cause
	 */
	@SuppressWarnings("deprecation")
	private void explodeBlock(Location loc, Location source, EntityType explosive)
	{
		Block block = loc.getWorld().getBlockAt(loc);
		if(plugin.getConfig().getConfigurationSection("Blocks").contains(Integer.toString(block.getTypeId())))
			try
			{
				double liquidDivider = plugin.getConfig().getDouble("LiquidMultiplier");
				double rawDamage = plugin.getConfig().getDouble("ExplosionSources." + explosive.toString());
				if(plugin.getStorage().addDamage(block, source.getBlock().isLiquid() && !(liquidDivider <= 0) ? rawDamage / liquidDivider : rawDamage))
					if(new Random().nextInt(100) + 1 >= plugin.getConfig().getInt("DropChance"))
						block.setType(Material.AIR);
					else
						block.breakNaturally();
			} catch (UnknownBlockTypeException e) {}
	}
}
