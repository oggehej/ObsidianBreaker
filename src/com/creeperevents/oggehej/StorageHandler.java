package com.creeperevents.oggehej;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class StorageHandler
{
	private ObsidianBreaker plugin;
	StorageHandler(ObsidianBreaker instance)
	{
		this.plugin = instance;
	}

	ConcurrentHashMap<String, BlockStatus> damage = new ConcurrentHashMap<String, BlockStatus>();

	/**
	 * Generate a unique string for the block location
	 * 
	 * @param loc Block location
	 * @return Unique string
	 */
	public String generateHash(Location loc)
	{
		return loc.getWorld() + "-" + loc.getBlockX() + "-" + loc.getBlockY() + "-" + loc.getBlockZ();
	}

	/**
	 * Check if we even handle the explosion of the specified block
	 * 
	 * @param block Block to check
	 * @return Whether we're handling these kind of blocks
	 */
	@SuppressWarnings("deprecation")
	private boolean isValidBlock(Block block)
	{
		return plugin.getConfig().getConfigurationSection("Blocks").getKeys(false).contains(Integer.toString(block.getTypeId()));
	}

	/**
	 * Get the total durability of the block type
	 * 
	 * @param block Block
	 * @return Total durability of block type
	 * @throws UnknownBlockTypeException There's no durability data for this block type
	 */
	@SuppressWarnings("deprecation")
	public double getTotalDurability(Block block) throws UnknownBlockTypeException
	{
		if(isValidBlock(block))
			return plugin.getConfig().getDouble("Blocks." + block.getTypeId());
		else
			throw new UnknownBlockTypeException();
	}

	/**
	 * Get the remaining durability of the specified block
	 * 
	 * @param block Block
	 * @return Remaining durability
	 * @throws UnknownBlockTypeException There's no durability data for this block type
	 */
	public double getRemainingDurability(Block block) throws UnknownBlockTypeException
	{
		String hash = generateHash(block.getLocation());
		return getTotalDurability(block) - (this.damage.containsKey(hash) ? this.damage.get(hash).getDamage() : 0D);
	}

	/**
	 * Add damage to the specified block
	 * 
	 * @param block Block
	 * @param addDamage Damage to add
	 * @return Return true if the durability left is <= 0
	 * @throws UnknownBlockTypeException There's no durability data for this block type
	 */
	public boolean addDamage(Block block, double addDamage) throws UnknownBlockTypeException
	{
		if(addDamage <= 0 || getTotalDurability(block) < 0)
			return false;

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
