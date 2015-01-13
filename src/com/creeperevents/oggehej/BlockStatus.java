package com.creeperevents.oggehej;

public class BlockStatus
{
	private double damage;
	private boolean modified = true;

	BlockStatus(double damage)
	{
		this.damage = damage;
	}

	public double getDamage()
	{
		return damage;
	}

	public void setDamage(double damage)
	{
		this.damage = damage;
	}

	public boolean isModified()
	{
		return modified;
	}

	public void setModified(boolean modified)
	{
		this.modified = modified;
	}
}
