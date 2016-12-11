package com.creeperevents.oggehej.obsidianbreaker.nms;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_11_R1.CraftServer;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;

import net.minecraft.server.v1_11_R1.BlockPosition;
import net.minecraft.server.v1_11_R1.PacketPlayOutBlockBreakAnimation;

public class v1_11_R1 implements NMS {
	@Override
	public void sendCrackEffect(Location location, int damage) {
		int x = location.getBlockX(), y = location.getBlockY(), z = location.getBlockZ();
		int dimension = ((CraftWorld) location.getWorld()).getHandle().dimension;
		PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(location.hashCode(), new BlockPosition(x, y, z), damage);
		((CraftServer) Bukkit.getServer()).getHandle().sendPacketNearby(null, x, y, z, 30, dimension, packet);
	}
}
