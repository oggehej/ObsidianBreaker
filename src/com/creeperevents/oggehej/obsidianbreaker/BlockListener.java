package com.creeperevents.oggehej.obsidianbreaker;

import java.util.Iterator;
import java.util.List;
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

/**
 * Block listener class
 * 
 * @author oggehej
 */
public class BlockListener implements Listener {
	private ObsidianBreaker plugin;
	BlockListener(ObsidianBreaker instance) {
		this.plugin = instance;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onEntityExplode(EntityExplodeEvent event) {
		if(event.getEntity() == null)
			return;

		Iterator<Block> it = event.blockList().iterator();
		while(it.hasNext()) {
			Block block = it.next();
			if(plugin.getStorage().isValidBlock(block))
				it.remove();
		}

		float unalteredRadius = (float) plugin.getConfig().getDouble("BlastRadius");
		int radius = (int) Math.ceil(unalteredRadius);
		Location detonatorLoc = event.getLocation();

		for (int x = -radius; x <= radius; x++)
			for (int y = -radius; y <= radius; y++)
				for (int z = -radius; z <= radius; z++) {
					Location targetLoc = new Location(detonatorLoc.getWorld(), detonatorLoc.getX() + x, detonatorLoc.getY() + y, detonatorLoc.getZ() + z);
					if (detonatorLoc.distance(targetLoc) <= unalteredRadius)
						explodeBlock(targetLoc, detonatorLoc, event.getEntityType());
				}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event) {
		// Remove BlockStatus if block is broken
		StorageHandler storage = plugin.getStorage();
		Block block = event.getBlock();
		BlockStatus status = storage.getBlockStatus(block, false);
		if(status != null) {
			storage.removeBlockStatus(status);
			plugin.getNMS().sendCrackEffect(block.getLocation(), -1);
		}
	}

	/**
	 * Explode a block
	 * 
	 * @param loc {@code Location} of the block
	 * @param source {@code Location} of the explosion source
	 * @param explosive The {@code EntityType} of the explosion cause
	 */
	@SuppressWarnings("deprecation")
	void explodeBlock(Location loc, Location source, EntityType explosive) {
		if(!loc.getChunk().isLoaded() || (loc.getBlockY() == 0 && plugin.getConfig().getBoolean("VoidProtector")))
			return;

		Block block = loc.getWorld().getBlockAt(loc);
		if(plugin.getStorage().isValidBlock(block)) {
			try {
				boolean isLiquid = false;
				float liquidDivider = (float) plugin.getConfig().getDouble("LiquidMultiplier");

				// Set multiplier to 1 to bypass
				if(liquidDivider != 1) {
					try {
						Vector v = new Vector(loc.getBlockX() - source.getBlockX(), loc.getBlockY() - source.getBlockY(), loc.getBlockZ() - source.getBlockZ());
						BlockIterator it = new BlockIterator(source.getWorld(), source.toVector(), v, 0, (int) source.distance(loc));
						while(it.hasNext()) {
							Block b = it.next();
							if(b.isLiquid()) {
								isLiquid = true;
								break;
							}
						}
					} catch(Exception e) {
						if(source.getBlock().isLiquid())
							isLiquid = true;

						plugin.printError("Liquid detection system failed. Fell back to primitive detection.", e);
					}
				}

				if(isLiquid && liquidDivider <= 0)
					return;

				float rawDamage = explosive == null ? 1 : (float) plugin.getConfig().getDouble("ExplosionSources." + explosive.toString());
				if(plugin.getStorage().addDamage(block, isLiquid ? rawDamage / liquidDivider : rawDamage)) {
					plugin.getNMS().sendCrackEffect(loc, -1);

					@SuppressWarnings("unchecked")
					List<String> list = (List<String>) plugin.getConfig().getList("Drops.DontDrop");
					for(Object section : list) {
						if(section instanceof Integer)
							section = Integer.toString((Integer) section);

						String[] s = ((String) section).split(":");
						if(block.getTypeId() == Integer.parseInt(s[0]) && (s.length == 1 || block.getData() == Byte.parseByte(s[1]))) {
							block.setType(Material.AIR);
							return;
						}
					}

					if(new Random().nextInt(100) + 1 >= plugin.getConfig().getInt("Drops.DropChance"))
						block.setType(Material.AIR);
					else
						block.breakNaturally();
				} else
					plugin.getStorage().renderCracks(block);
			} catch (UnknownBlockTypeException e) {}
		}
	}
}
