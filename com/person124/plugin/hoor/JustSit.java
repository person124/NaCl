package com.person124.plugin.hoor;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.material.Stairs;

import com.person124.plugin.PPBase;

public class JustSit extends PPBase {

	public JustSit() {
		super("JustSit");
		setHasEvents();
	}
	
	@EventHandler
	public void onPlayerRightClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
			if (isStairs(event.getClickedBlock().getType()) && isEmpty(event.getClickedBlock().getLocation())) {
				Stairs s = (Stairs) event.getClickedBlock().getState().getData();
				if (!s.isInverted()) {
					Location loc = event.getClickedBlock().getLocation().add(0.5, 0, 0.5);
					Arrow arrow = (Arrow) loc.getWorld().spawnEntity(loc, EntityType.ARROW);
					
					arrow.setPassenger(event.getPlayer());
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerShift(PlayerToggleSneakEvent event) {
		if (event.getPlayer().isInsideVehicle()) {
			if (event.getPlayer().getVehicle() instanceof Arrow) {
				event.setCancelled(true);
				event.getPlayer().getVehicle().remove();
			}
		}
	}
	
	private boolean isStairs(Material m) {
		return m == Material.WOOD_STAIRS || m == Material.COBBLESTONE_STAIRS || m == Material.BRICK_STAIRS || m == Material.SMOOTH_STAIRS || m == Material.NETHER_BRICK_STAIRS || m == Material.SANDSTONE_STAIRS || m == Material.SPRUCE_WOOD_STAIRS || m == Material.BIRCH_WOOD_STAIRS || m == Material.JUNGLE_WOOD_STAIRS || m == Material.QUARTZ_STAIRS || m == Material.ACACIA_STAIRS || m == Material.DARK_OAK_STAIRS || m == Material.RED_SANDSTONE_STAIRS;
	}
	
	private boolean isEmpty(Location loc) {
		return loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ()).getType().isTransparent() && loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() + 2, loc.getBlockZ()).getType().isTransparent();
	}

	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		return false;
	}

}
