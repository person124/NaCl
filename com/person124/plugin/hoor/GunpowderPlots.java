package com.person124.plugin.hoor;

import java.io.File;
import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.FurnaceInventory;

import com.person124.plugin.Config;
import com.person124.plugin.PPBase;

public class GunpowderPlots extends PPBase {
	
	private File cfgFile;
	private FileConfiguration config;

	public GunpowderPlots() {
		super("GunpowderPlots");
		setHasEvents();
		setNeedsFolder();
	}
	
	protected void onEnable() {
		cfgFile = new File(pp.getDataFolder(), "gplot.prsn");
		config = Config.create(cfgFile, "explosion.power", 4.0F, "explosion.breakblocks", true, "explosion.causefire", true);
	}
	
	@EventHandler
	public void onFurnaceOpen(final PlayerInteractEvent event) {
		if (event.getClickedBlock() == null) return;
		if (event.getClickedBlock().getType() == Material.FURNACE && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			FurnaceInventory inv = ((Furnace) event.getClickedBlock().getState()).getInventory();
			if (inv.getSmelting() == null) return;
			if (inv.getSmelting().getType() == Material.SULPHUR) {
				pp.getServer().getScheduler().scheduleSyncDelayedTask(pp, new Runnable() {
					public void run() {
						double x = event.getClickedBlock().getLocation().getX();
						double y = event.getClickedBlock().getLocation().getY();
						double z = event.getClickedBlock().getLocation().getZ();
						event.getClickedBlock().getWorld().createExplosion(x, y, z, config.getInt("explosion.power"), config.getBoolean("explosion.breakblocks"), config.getBoolean("explosion.causefire"));
					}
				});
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		return false;
	}

}
