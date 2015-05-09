package com.creeperevents.oggehej.obsidianbreaker;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
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
	 * Generate a unique {@code String} for the {@code Block} {@code Location}
	 * 
	 * @param loc Block location
	 * @return Unique string
	 */
	public String generateHash(Location loc)
	{
		return loc.getWorld().getUID().toString() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
	}

	/**
	 * Generate a {@code Location} from the unique {@code String}
	 * 
	 * @param string
	 * @return Location
	 */
	public Location generateLocation(String string)
	{
		try {
			String[] s = string.split(":");
			return new Location(Bukkit.getWorld(UUID.fromString(s[0])), Integer.parseInt(s[1]), Integer.parseInt(s[2]), Integer.parseInt(s[3]));
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Check if we even handle the explosion of the specified block
	 * 
	 * @param block Block to check
	 * @return Whether we're handling these kind of blocks
	 */
	@SuppressWarnings("deprecation")
	public boolean isValidBlock(Block block)
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
	public float getTotalDurability(Block block) throws UnknownBlockTypeException
	{
		if(isValidBlock(block))
			return (float) plugin.getConfig().getDouble("Blocks." + block.getTypeId());
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
	public float getRemainingDurability(Block block) throws UnknownBlockTypeException
	{
		String hash = generateHash(block.getLocation());
		return getTotalDurability(block) - (this.damage.containsKey(hash) ? this.damage.get(hash).getDamage() : 0F);
	}

	/**
	 * Add damage to the specified block
	 * 
	 * @param block Block
	 * @param addDamage Damage to add
	 * @return Return true if the durability left is <= 0
	 * @throws UnknownBlockTypeException There's no durability data for this block type
	 */
	public boolean addDamage(Block block, float addDamage) throws UnknownBlockTypeException
	{
		if(addDamage <= 0 || getTotalDurability(block) < 0)
			return false;

		String hash = generateHash(block.getLocation());
		float totalDamage = damage.containsKey(hash) ? addDamage + damage.get(hash).getDamage() : addDamage;

		if(totalDamage >= getTotalDurability(block) - 0.001f)
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

	/**
	 * Render cracks in {@code Block}
	 * 
	 * @param block Block
	 */
	public void renderCracks(Block block)
	{
		if(plugin.getConfig().getBoolean("BlockCracks.Enabled"))
			try {
				float totalDurability = getTotalDurability(block);

				if(totalDurability <= 0)
					return;

				int durability = 10 - (int) Math.floor(getRemainingDurability(block) / getTotalDurability(block) * 10);
				plugin.getNMS().sendCrackEffect(block, durability);
			} catch (UnknownBlockTypeException e) {
				return;
			}
	}
}
