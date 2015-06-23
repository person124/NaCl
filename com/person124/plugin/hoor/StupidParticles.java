package com.person124.plugin.hoor;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;

import com.person124.plugin.PPBase;

public class StupidParticles extends PPBase {

	private final String PARTICLE_PERMISSION = "person.stupid";

	public StupidParticles() {
		super("StupidParticles");
		setHasEvents();
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getPlayer().hasPermission(PARTICLE_PERMISSION) || event.getPlayer().isOp()) event.getPlayer().getWorld().playEffect(getLocation(event.getBlock(), 0.5, 0.5, 0.5), Effect.SPELL, 0);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getPlayer().hasPermission(PARTICLE_PERMISSION) || event.getPlayer().isOp()) event.getPlayer().getWorld().playEffect(getLocation(event.getBlock(), 0.5, 0.5, 0.5), Effect.ENDER_SIGNAL, 0);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (event.getPlayer().hasPermission(PARTICLE_PERMISSION) || event.getPlayer().isOp()) {
			if (event.getFrom().getY() != event.getTo().getY()) event.getPlayer().getWorld().playEffect(getLocation(event.getPlayer()), Effect.FIREWORKS_SPARK, 0);
			event.getPlayer().getWorld().playEffect(getLocation(event.getPlayer()), Effect.COLOURED_DUST, 0);
		}
	}

	@EventHandler
	public void onPlayerSneak(PlayerToggleSneakEvent event) {
		if (event.getPlayer().hasPermission(PARTICLE_PERMISSION) || event.getPlayer().isOp()) event.getPlayer().getWorld().playEffect(getLocation(event.getPlayer(), 0, 0.5, 0), Effect.HEART, 0);
	}

	@EventHandler
	public void onPlayerSprint(PlayerToggleSprintEvent event) {
		if (event.getPlayer().hasPermission(PARTICLE_PERMISSION) || event.getPlayer().isOp()) if (!event.getPlayer().isSprinting()) event.getPlayer().getWorld().playEffect(getLocation(event.getPlayer(), 0, 0.5, 0), Effect.EXPLOSION_HUGE, 0);
	}

	private Location getLocation(Object obj) {
		return getLocation(obj, 0, 0, 0);
	}

	private Location getLocation(Object obj, double x, double y, double z) {
		Location loc = null;
		if (obj instanceof Entity) loc = ((Entity) obj).getLocation();
		else if (obj instanceof Block) loc = ((Block) obj).getLocation();

		loc.add(x, y, z);

		return loc;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return false;
	}

}
