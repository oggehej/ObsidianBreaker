package com.creeperevents.oggehej.obsidianbreaker;

import java.util.Iterator;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class BlockListener implements Listener {
	private ObsidianBreaker plugin;
	BlockListener(ObsidianBreaker instance) {
		this.plugin = instance;
	}

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onEntityExplode(EntityExplodeEvent event) {
		Iterator<Block> it = event.blockList().iterator();
		while(it.hasNext()) {
			Block block = it.next();
			if(plugin.getConfig().getConfigurationSection("Blocks").getKeys(false).contains(Integer.toString(block.getTypeId())))
				it.remove();
		}

		float unalteredRadius = (float) plugin.getConfig().getDouble("BlastRadius");
		int radius = (int) Math.ceil(unalteredRadius);
		Location detonatorLoc = event.getLocation();

		for (int x = -radius; x <= radius; x++)
			for (int y = -radius; y <= radius; y++)
				for (int z = -radius; z <= radius; z++) {
					if(!(event.getEntity() == null)) {
						Location targetLoc = new Location(detonatorLoc.getWorld(), detonatorLoc.getX() + x, detonatorLoc.getY() + y, detonatorLoc.getZ() + z);
						if (detonatorLoc.distance(targetLoc) <= unalteredRadius)
							explodeBlock(targetLoc, detonatorLoc, event.getEntityType());
					}
				}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onBlockBreak(BlockBreakEvent event) {
		StorageHandler storage = plugin.getStorage();
		Location loc = event.getBlock().getLocation();
		storage.damage.remove(storage.generateHash(loc));
		plugin.getNMS().sendCrackEffect(loc, -1);
	}

	/**
	 * Explode a block
	 * 
	 * @param loc {@code Location} of the block
	 * @param source {@code Location} of the explosion source
	 * @param explosive The {@code EntityType} of the explosion cause
	 */
	void explodeBlock(Location loc, Location source, EntityType explosive) {
		Block block = loc.getWorld().getBlockAt(loc);
		if(plugin.getStorage().isValidBlock(block))
			try {
				boolean isLiquid = false;
				Vector v = new Vector(loc.getBlockX() - source.getBlockX(), loc.getBlockY() - source.getBlockY(), loc.getBlockZ() - source.getBlockZ());
				try {
					BlockIterator it = new BlockIterator(source.getWorld(), source.toVector(), v, 0, (int) Math.floor(source.distance(loc)));
					while(it.hasNext())
						if(it.next().isLiquid()) {
							isLiquid = true;
							break;
						}
				} catch(Exception e) {
					if(source.getBlock().isLiquid())
						isLiquid = true;
				}

				float liquidDivider = (float) plugin.getConfig().getDouble("LiquidMultiplier");
				if(isLiquid && liquidDivider <= 0)
					return;
				float rawDamage = explosive == null ? 1 : (float) plugin.getConfig().getDouble("ExplosionSources." + explosive.toString());
				if(plugin.getStorage().addDamage(block, isLiquid ? rawDamage / liquidDivider : rawDamage)) {
					plugin.getNMS().sendCrackEffect(loc, -1);
					if(new Random().nextInt(100) + 1 >= plugin.getConfig().getInt("DropChance"))
						block.setType(Material.AIR);
					else
						block.breakNaturally();
				} else
					plugin.getStorage().renderCracks(block);
			} catch (UnknownBlockTypeException e) {}
	}
}
