package com.person124.plugin.hoor;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
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
		if (check(event.getPlayer())) event.getPlayer().getWorld().playEffect(getLocation(event.getBlock(), 0.5, 0.5, 0.5), Effect.SPELL, 0);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (check(event.getPlayer())) event.getPlayer().getWorld().playEffect(getLocation(event.getBlock(), 0.5, 0.5, 0.5), Effect.ENDER_SIGNAL, 0);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (check(event.getPlayer())) {
			if (event.getFrom().getY() != event.getTo().getY()) event.getPlayer().getWorld().playEffect(getLocation(event.getPlayer()), Effect.FIREWORKS_SPARK, 0);
			event.getPlayer().getWorld().playEffect(getLocation(event.getPlayer()), Effect.COLOURED_DUST, 0);
		}
		if (isPerson(event.getPlayer())) event.getPlayer().getWorld().playEffect(getLocation(event.getPlayer(), 0, 0.5, 0), Effect.HEART, 0);
	}

	@EventHandler
	public void onPlayerSneak(PlayerToggleSneakEvent event) {
		if (check(event.getPlayer())) event.getPlayer().getWorld().playEffect(getLocation(event.getPlayer(), 0, 0.5, 0), Effect.HEART, 0);
	}

	@EventHandler
	public void onPlayerSprint(PlayerToggleSprintEvent event) {
		if (check(event.getPlayer())) if (!event.getPlayer().isSprinting()) event.getPlayer().getWorld().playEffect(getLocation(event.getPlayer(), 0, 0.5, 0), Effect.EXPLOSION_HUGE, 0);
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (event.getCause() == TeleportCause.SPECTATE || event.getCause() == TeleportCause.END_PORTAL || event.getCause() == TeleportCause.NETHER_PORTAL || event.getCause() == TeleportCause.ENDER_PEARL) return;
		event.getPlayer().getWorld().playEffect(event.getFrom(), Effect.ENDER_SIGNAL, 0);
		event.getPlayer().getWorld().playEffect(event.getTo(), Effect.ENDER_SIGNAL, 0);
	}

	/*
	private boolean checkMinus(Player p) {
		return p.hasPermission(PARTICLE_PERMISSION) || p.isOp();
	}
	*/

	private boolean check(Player p) {
		return (p.hasPermission(PARTICLE_PERMISSION) || p.isOp()) && !isPerson(p);
	}

	private boolean isPerson(Player p) {
		return p.getUniqueId().toString().equals("e854a81a-f2c2-4168-bef8-877a5bdd1835");
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
