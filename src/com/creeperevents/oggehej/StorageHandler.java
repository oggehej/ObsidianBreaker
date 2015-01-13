package com.creeperevents.oggehej;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class StorageHandler
{
	ObsidianBreaker plugin;
	StorageHandler(ObsidianBreaker instance)
	{
		this.plugin = instance;
	}

	ConcurrentHashMap<String, BlockStatus> damage = new ConcurrentHashMap<String, BlockStatus>();

	public String generateHash(Location loc)
	{
		return loc.getWorld() + "-" + loc.getBlockX() + "-" + loc.getBlockY() + "-" + loc.getBlockZ();
	}

	@SuppressWarnings("deprecation")
	private boolean isValidBlock(Block block)
	{
		return plugin.getConfig().getConfigurationSection("Blocks").getKeys(false).contains(Integer.toString(block.getTypeId()));
	}

	@SuppressWarnings("deprecation")
	public double getTotalDurability(Block block) throws UnknownBlockTypeException
	{
		if(isValidBlock(block))
			return plugin.getConfig().getDouble("Blocks." + block.getTypeId());
		else
			throw new UnknownBlockTypeException();
	}

	public double getRemainingDurability(Block block) throws UnknownBlockTypeException
	{
		String hash = generateHash(block.getLocation());
		return getTotalDurability(block) - (this.damage.containsKey(hash) ? this.damage.get(hash).getDamage() : 0D);
	}

	public boolean addDamage(Block block, double addDamage) throws UnknownBlockTypeException
	{
		String hash = generateHash(block.getLocation());
		double totalDamage = damage.containsKey(hash) ? addDamage + (double) damage.get(hash).getDamage() : addDamage;

		if(totalDamage >= getTotalDurability(block) - 0.00001)
		{
			damage.remove(hash);
			return true;
		}
		else
		{
			damage.put(hash, new BlockStatus(totalDamage));
			return false;
		}
	}
}
