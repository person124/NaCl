package com.person124.plugin.hoor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import com.person124.plugin.Config;
import com.person124.plugin.PPBase;

//Suspicious Account Behavior Detection
public class SABD extends PPBase {

	private File cfg, uuid, ips, warn;

	private final String SEE_NOTICE_PERMISSION = "person.sabd.see";
	private final String SET_SABD_PARDON_PERMISSION = "person.sabd.pardon";

	public SABD() {
		super("SABD");
		setHasEvents();
		setNeedsFolder();
		setCommand("sabd");
	}

	public void onEnable() {
		cfg = new File(pp.getDataFolder(), "sabd.prsn");
		uuid = new File(pp.getDataFolder(), "sabd/uuid.prsn");
		ips = new File(pp.getDataFolder(), "sabd/ip.prsn");
		warn = new File(pp.getDataFolder(), "sabd/warn.prsn");
		Config.createFile(cfg);
		Config.createFile(uuid);
		Config.createFile(ips);
		Config.createFile(warn);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		String ip = p.getAddress().getAddress().toString().replace("/", "").replace(".", "-");
		preformCheck(p.getUniqueId().toString(), ip, uuid, true);
		preformCheck(ip, p.getUniqueId().toString(), ips, false);
	}

	private void preformCheck(String label, String value, File f, boolean uuid) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(f);
		if (config.getList(label) == null) addConfig(f, config, label, value);
		else {
			if (!config.getList(label).contains(value)) {
				if (checkCheck(label, uuid)) return;
				addConfig(f, config, label, value);
				FileConfiguration warning = YamlConfiguration.loadConfiguration(warn);
				warning.set(label, config.getList(label));
				try {
					warning.save(warn);
				} catch (IOException e) {
					e.printStackTrace();
				}
				sendMessageToAll(label);
			}
		}
	}

	private boolean checkCheck(String s, boolean uuid) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(cfg);
		if (uuid) return config.getStringList("uuid").contains(s);
		return config.getStringList("ip").contains(s);
	}

	private void sendMessageToAll(String label) {
		pp.getLogger().info("ALERT!!!! INSPECT: " + label);
		for (Player p : pp.getServer().getOnlinePlayers()) {
			if (p.hasPermission(SEE_NOTICE_PERMISSION)) p.sendMessage(ChatColor.UNDERLINE + "ALERT!!!! INSPECT: " + label);
		}
	}

	private void addConfig(File f, FileConfiguration config, String label, String value) {
		List<String> list = new ArrayList<String>();
		if (config.getList(label) != null) list = config.getStringList(label);
		list.add(value);

		config.set(label, list);
		try {
			config.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (!p.hasPermission(SET_SABD_PARDON_PERMISSION)) {
				p.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
				return true;
			}
			if (args.length == 0) showHelpPlayer(p);
			else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("list")) listThings(p);
				else showHelpPlayer(p);
			}
			else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("uuid")) {
					addUUID(args[1]);
					p.sendMessage(ChatColor.GOLD + "UUID: " + args[1] + " has been added to ignore list.");
				} else if (args[0].equalsIgnoreCase("player")) {
					addPlayer(args[1]);
					p.sendMessage(ChatColor.GOLD + "Player: " + args[1] + " has been added to ignore list.");
				} else if (args[0].equalsIgnoreCase("ip")) {
					addIP(args[1]);
					p.sendMessage(ChatColor.GOLD + "IP: " + args[1] + " has been added to ignore list.");
				} else showHelpPlayer(p);
			} else if (args.length == 3) {
				if (args[2].equalsIgnoreCase("remove")) {
					if (args[0].equalsIgnoreCase("uuid")) {
						if (removeUUID(args[1])) p.sendMessage(ChatColor.GOLD + "UUID: " + args[1] + " has been removed from the ignore list.");
						else p.sendMessage(ChatColor.GOLD + "UUID: " + args[1] + " is not on the ignore list.");
					} else if (args[0].equalsIgnoreCase("player")) {
						if (removePlayer(args[1])) p.sendMessage(ChatColor.GOLD + "Player: " + args[1] + " has been removed from the ignore list.");
						else p.sendMessage(ChatColor.GOLD + "Player: " + args[1] + " is not on the ignore list.");
					} else if (args[0].equalsIgnoreCase("ip")) {
						if (removeIP(args[1])) p.sendMessage(ChatColor.GOLD + "IP: " + args[1] + " has been removed from the ignore list.");
						else p.sendMessage(ChatColor.GOLD + "IP: " + args[1] + " is not on the ignore list.");
					} else showHelpPlayer(p);
				} else showHelpPlayer(p);
			}
		} else {
			if (args.length == 0) showHelpConsole();
			else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("list")) listThings(null);
				else showHelpConsole();
			}
			else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("uuid")) {
					addUUID(args[1]);
					pp.getLogger().info("UUID: " + args[1] + " has been added to ignore list.");
				} else if (args[0].equalsIgnoreCase("player")) {
					addPlayer(args[1]);
					pp.getLogger().info("Player: " + args[1] + " has been added to ignore list.");
				} else if (args[0].equalsIgnoreCase("ip")) {
					addIP(args[1]);
					pp.getLogger().info("IP: " + args[1] + " has been added to ignore list.");
				} else showHelpConsole();
			} else if (args.length == 3) {
				if (args[2].equalsIgnoreCase("remove")) {
					if (args[0].equalsIgnoreCase("uuid")) {
						if (removeUUID(args[1])) pp.getLogger().info("UUID: " + args[1] + " has been removed from the ignore list.");
						else pp.getLogger().info("UUID: " + args[1] + " is not on the ignore list.");
					} else if (args[0].equalsIgnoreCase("player")) {
						if (removePlayer(args[1])) pp.getLogger().info("Player: " + args[1] + " has been removed from the ignore list.");
						else pp.getLogger().info("Player: " + args[1] + " is not on the ignore list.");
					} else if (args[0].equalsIgnoreCase("ip")) {
						if (removeIP(args[1])) pp.getLogger().info("IP: " + args[1] + " has been removed from the ignore list.");
						else pp.getLogger().info("IP: " + args[1] + " is not on the ignore list.");
					} else showHelpConsole();
				} else showHelpConsole();
			}
		}
		return true;
	}

	//@SuppressWarnings("unchecked")
	private void listThings(Player p) {
		FileConfiguration warning = YamlConfiguration.loadConfiguration(warn);
		Map<String, Object> map = warning.getValues(false);
		for (String s : map.keySet()) {
			String temp = "";
			for (String str : warning.getStringList(s)) {
				temp += str + ", ";
			}
			if (p != null) p.sendMessage(ChatColor.GOLD + s + ": " + temp);
			else pp.getLogger().info(s + ": " + temp);
		}
	}

	private void addIP(String ip) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(cfg);
		addConfig(cfg, config, "ip", ip.replace(".", "-"));
	}

	private boolean removeIP(String ip) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(cfg);
		List<String> list = null;
		if (config.getStringList("ip") == null) return false;
		else list = config.getStringList("ip");
		list.remove(ip.replace(".", "-"));
		config.set("ip", list);
		try {
			config.save(cfg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	private void addPlayer(String player) {
		File temp = new File(pp.getDataFolder(), "players.prsn");
		FileConfiguration config = YamlConfiguration.loadConfiguration(temp);
		addUUID(config.getString(player));
	}

	private boolean removePlayer(String player) {
		File temp = new File(pp.getDataFolder(), "players.prsn");
		FileConfiguration config = YamlConfiguration.loadConfiguration(temp);
		return removeUUID(config.getString(player));
	}

	private void addUUID(String UUID) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(cfg);
		addConfig(cfg, config, "uuid", UUID);
	}

	private boolean removeUUID(String UUID) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(cfg);
		List<String> list = null;
		if (config.getStringList("uuid") == null) return false;
		else list = config.getStringList("uuid");
		list.remove(UUID);
		config.set("uuid", list);
		try {
			config.save(cfg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	private void showHelpPlayer(Player p) {
		p.sendMessage(ChatColor.GOLD + showHelp(0));
		p.sendMessage(ChatColor.GOLD + showHelp(1));
		p.sendMessage(ChatColor.GOLD + showHelp(2));
		p.sendMessage(ChatColor.GOLD + showHelp(3));
		p.sendMessage(ChatColor.GOLD + showHelp(4));
	}

	private void showHelpConsole() {
		pp.getLogger().info(showHelp(0));
		pp.getLogger().info(showHelp(1));
		pp.getLogger().info(showHelp(2));
		pp.getLogger().info(showHelp(3));
		pp.getLogger().info(showHelp(4));
	}

	private String showHelp(int i) {
		String s = "NOT FOUND.";
		switch (i) {
			case 0:
				s = "/nabd uuid <uuid> | Excludes a player from judgement.";
				break;
			case 1:
				s = "/nabd player <player> | Excludes a player from judgement.";
				break;
			case 2:
				s = "/nabd ip <ip> | Excludes an ip from judgement (The ip can use . or -).";
				break;
			case 3:
				s = "Add 'remove' to the end of the command to remove thing from the list.";
				break;
			case 4:
				s = "/nabd list | Lists all warnings.";
				break;
		}
		return s;
	}

}
