package com.person124.plugin.hoor;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;
import com.person124.plugin.PPBase;

public class DeathChest extends PPBase {

	private File cfgFile;
	private FileConfiguration config;

	private int searchRange;
	private boolean needChest, deleteChest;

	public DeathChest() {
		super("BecauseWhyNot");
		setHasEvents(true);
		setNeedsFolder(true);
	}

	public void onEnable() {
		cfgFile = new File(pp.getDataFolder(), "deathChest.prsn");
		if (!cfgFile.exists()) {
			try {
				cfgFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			config = YamlConfiguration.loadConfiguration(cfgFile);
			config.set("needChest", true);
			config.set("deleteChest", true);
			config.set("searchRange", 5);
			try {
				config.save(cfgFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else config = YamlConfiguration.loadConfiguration(cfgFile);

		needChest = config.getBoolean("needChest");
		deleteChest = config.getBoolean("deleteChest");
		searchRange = config.getInt("searchRange");
	}

	@EventHandler
	public void onChestClose(InventoryCloseEvent event) {
		if (deleteChest) {
			if (event.getInventory().getType() == InventoryType.CHEST) {
				if (event.getInventory().getName().equalsIgnoreCase(ChatColor.AQUA + "R.I.P. Stupid")) {
					if (isEmpty(event.getInventory())) {
						Chest c = (Chest) event.getInventory().getHolder();
						c.setType(Material.AIR);
						c.update(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Location l = checkLocation(event.getEntity().getLocation());
		if (l == null) return;

		if (needChest && event.getEntity().getInventory().contains(Material.CHEST)) event.getEntity().getInventory().remove(Material.CHEST);
		else return;

		if (isInRegion(event.getEntity())) return;

		event.getDrops().clear();
		Player p = event.getEntity();

		l.getBlock().setType(Material.CHEST);
		Chest chest = (Chest) l.getBlock().getState();

		int temp = 0, other = 0;
		for (int i = 0; i < 27; i++) {
			ItemStack is = p.getInventory().getContents()[i + other];
			if (is == null) {
				temp++;
				other++;
				break;
			}
			chest.getBlockInventory().addItem(is);
			p.getInventory().remove(is);
		}
		if (temp > 0) {
			for (int i = 0; i < (temp > 4 ? 4 : temp); i++) {
				ItemStack is = p.getInventory().getContents()[i];
				if (is == null) break;
				chest.getBlockInventory().addItem(is);
			}
		}
		setChestName(l, ChatColor.AQUA + "R.I.P. Stupid");

		setSign(l, p.getName());
	}

	private Location checkLocation(Location old) {
		if (old.getBlock().getType() == Material.AIR) return old;
		for (int i = -searchRange; i <= searchRange; i++) {
			int x = i + old.getBlockX();
			int y = i + old.getBlockY();
			int z = i + old.getBlockZ();

			Location loc = new Location(old.getWorld(), x, y, z);
			if (loc.getBlock().getType() == Material.AIR) return loc;
		}
		return null;
	}

	private boolean isInRegion(Player p) {
		Faction f = BoardColl.get().getFactionAt(PS.valueOf(p));
		MPlayer player = MPlayer.get(p);
		if (player.isInOwnTerritory() || f.getName().contains("WarZone") || f.getName().contains("Wilderness")) return false;
		return true;
	}

	@SuppressWarnings("deprecation")
	private void setSign(Location loc, String name) {
		Location l = null;
		int data = 0;
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();

		if (loc.getWorld().getBlockAt(x + 1, y, z).getType() == Material.AIR) {
			l = new Location(loc.getWorld(), x + 1, y, z);
			data = 5;
		} else if (loc.getWorld().getBlockAt(x - 1, y, z).getType() == Material.AIR) {
			l = new Location(loc.getWorld(), x - 1, y, z);
			data = 4;
		} else if (loc.getWorld().getBlockAt(x, y, z + 1).getType() == Material.AIR) {
			l = new Location(loc.getWorld(), x, y, z + 1);
			data = 3;
		} else if (loc.getWorld().getBlockAt(x, y, z - 1).getType() == Material.AIR) {
			l = new Location(loc.getWorld(), x, y, z - 1);
			data = 2;
		}
		if (l == null) return;

		l.getBlock().setType(Material.WALL_SIGN);
		l.getBlock().setData((byte) data);
		Sign s = (Sign) l.getBlock().getState();

		s.setLine(0, "R.I.P.");
		s.setLine(1, name);

		DateFormat format = new SimpleDateFormat("MM/dd/YYYY");
		Date date = new Date();
		s.setLine(2, format.format(date));

		format = new SimpleDateFormat("HH:mm");
		s.setLine(3, format.format(date));
		s.update(false);
	}

	private boolean isEmpty(Inventory inv) {
		for (int i = 0; i < inv.getSize(); i++) {
			if (inv.getItem(i) != null) return false;
		}
		return true;
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		return false;
	}

}
