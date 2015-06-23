package com.person124.plugin.hoor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import com.person124.plugin.Config;
import com.person124.plugin.PPBase;

public class ShopDesk extends PPBase {

	private File file, players, deskFolder;
	private FileConfiguration configPlayers;

	public ShopDesk() {
		super("Shop Desk");
		setCommand("shopdesk");
		setHasEvents();
		setNeedsFolder();
	}

	protected void onEnable() {
		file = new File(pp.getDataFolder() + "/../Essentials/trade.log");

		players = new File(pp.getDataFolder(), "players.prsn");
		Config.createFile(players);
		configPlayers = YamlConfiguration.loadConfiguration(players);

		deskFolder = new File(pp.getDataFolder(), "shopdesk");
		deskFolder.mkdir();
	}

	private void readFileAndDoStuff() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				if (isTradeSign(line)) {
					String[] lines = line.split(",");

					String name = lines[6].replace("\"", "").substring(2);
					String uuid = configPlayers.getString(name);
					File cfgFile = new File(pp.getDataFolder(), "shopdesk/" + uuid + ".prsn");
					Config.createFile(cfgFile);
					FileConfiguration config = YamlConfiguration.loadConfiguration(cfgFile);

					//amount-thing--sold_for_amount-sold_for_thing
					String section = lines[7] + "-" + lines[8] + "--" + lines[11] + "-" + lines[12];
					if (config.get(section) == null) config.set(section, 1);
					else config.set(section, config.getInt(section) + 1);

					config.set("name", name);

					try {
						config.save(cfgFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void compileFiles() {
		File totalFile = new File(pp.getDataFolder(), "shopdesk/total.prsn");
		Config.createFile(totalFile);
		FileConfiguration total = YamlConfiguration.loadConfiguration(totalFile);

		for (String files : deskFolder.list()) {
			if (!files.contains("players") || !files.contains("total")) {
				File temp = new File(pp.getDataFolder(), "shopdesk/" + files);
				FileConfiguration config = YamlConfiguration.loadConfiguration(temp);
				int sale = 0;
				for (String sales : config.getKeys(false)) {
					if (!sales.equalsIgnoreCase("name")) sale += config.getInt(sales);
				}
				total.set(files.replace(".prsn", "") + "(" + config.getString("name") + ")", sale);
			}
		}

		try {
			total.save(totalFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String findHighest() {
		File totalFile = new File(pp.getDataFolder(), "shopdesk/total.prsn");
		FileConfiguration total = YamlConfiguration.loadConfiguration(totalFile);
		int highest = 0;
		String name = "";

		for (String names : total.getKeys(false)) {
			int current = total.getInt(names);
			if (current > highest) {
				highest = current;
				name = names;
			}
		}

		return name + ": " + highest;
	}

	private void removeFiles() {
		deskFolder.delete();
		deskFolder.mkdir();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			((Player) sender).sendMessage(ChatColor.RED + "Only console can use this command!");
		} else {
			if (args.length == 0) {
				pp.getLogger().info("shopdesk expand  | Turns trade.log into a lot of files.");
				pp.getLogger().info("shopdesk parse   | Makes one file with all the trade amounts.");
				pp.getLogger().info("shopdesk highest | Displays the player with the highest shop amount.");
				pp.getLogger().info("shopdesk remove  | Removes all but the players.prsn file.");
			} else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("expand")) {
					pp.getLogger().info("Expanding trade.log....");
					readFileAndDoStuff();
					pp.getLogger().info("trade.log expanded.");
				} else if (args[0].equalsIgnoreCase("parse")) {
					pp.getLogger().info("Parsing files to find trade amounts....");
					compileFiles();
					pp.getLogger().info("Files have been parsed.");
				} else if (args[0].equalsIgnoreCase("highest")) {
					pp.getLogger().info("Player with highest trade rate is: " + findHighest() + ".");
				} else if (args[0].equalsIgnoreCase("remove")) {
					pp.getLogger().info("Removing files....");
					removeFiles();
					pp.getLogger().info("Files removed.");
				}
			}
			return true;
		}

		return false;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		configPlayers.set(event.getPlayer().getDisplayName(), event.getPlayer().getUniqueId().toString());
		try {
			configPlayers.save(players);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean isTradeSign(String str) {
		return str.startsWith("Sign,Trade,Interact");
	}

}
