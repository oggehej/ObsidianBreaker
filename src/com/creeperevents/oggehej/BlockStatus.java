package com.creeperevents.oggehej;

class BlockStatus
{
	private double damage;
	private boolean modified = true;

	/**
	 * An object that contains information about the
	 * damage taken and id it was recently modified.
	 * 
	 * @param damage Current damage
	 */
	BlockStatus(double damage)
	{
		this.damage = damage;
	}

	/**
	 * Get current damage to block
	 * 
	 * @return Damage
	 */
	double getDamage()
	{
		return damage;
	}

	/**
	 * Set current damage to block
	 * 
	 * @param damage Damage
	 */
	void setDamage(double damage)
	{
		this.damage = damage;
	}

	/**
	 * Check whether the block was recently modified or not
	 * 
	 * @return Recently modified
	 */
	boolean isModified()
	{
		return modified;
	}

	/**
	 * Set whether the block was recently modified or not
	 * 
	 * @param modified Recently modified
	 */
	void setModified(boolean modified)
	{
		this.modified = modified;
	}
}
