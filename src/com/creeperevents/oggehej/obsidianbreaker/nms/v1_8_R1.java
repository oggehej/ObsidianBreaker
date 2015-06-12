package com.creeperevents.oggehej.obsidianbreaker.nms;

import net.minecraft.server.v1_8_R1.BlockPosition;
import net.minecraft.server.v1_8_R1.PacketPlayOutBlockBreakAnimation;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R1.CraftServer;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;

public class v1_8_R1 implements NMS {
	@Override
	public void sendCrackEffect(Block block, int damage) {
		int x = block.getX(), y = block.getY(), z = block.getZ();
		int dimension = ((CraftWorld) block.getWorld()).getHandle().dimension;
		PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(block.getLocation().hashCode(), new BlockPosition(x, y, z), damage);
		((CraftServer) Bukkit.getServer()).getHandle().sendPacketNearby(x, y, z, 120, dimension, packet);
	}

	@Override
	public boolean isDummy() {
		return false;
	}
}
