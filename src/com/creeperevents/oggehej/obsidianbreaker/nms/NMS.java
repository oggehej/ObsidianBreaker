package com.creeperevents.oggehej.obsidianbreaker.nms;

import org.bukkit.block.Block;

public interface NMS
{
	public void sendCrackEffect(Block block, int damage);
	public boolean isDummy();
}
