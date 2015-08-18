package com.person124.plugin.hoor;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.person124.plugin.PPBase;

public class CrappleGapples extends PPBase {
	
	public CrappleGapples() {
		super("CrappleGapples");
		setHasEvents();
	}
	
	@EventHandler
	public void onPlayerEatItem(PlayerItemConsumeEvent event) {
		if (event.getItem().getType() == Material.GOLDEN_APPLE && event.getItem().getDurability() == (short) 1) {
			final Player p = event.getPlayer();
			pp.getServer().getScheduler().runTask(pp, new Runnable() {
				public void run() {
					p.removePotionEffect(PotionEffectType.REGENERATION);
					p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 600, 3));
				}
			});
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return false;
	}

}
