package com.person124.plugin.hoor;

import java.io.File;
import java.util.Random;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.person124.plugin.Config;
import com.person124.plugin.PPBase;

public class AnnoyOTron extends PPBase {

	private Random rand = new Random();

	private final String SLAP_PERMISSION = "person.slap";
	private final String SLAP_DAMAGE_PERMISSION = "person.slap.damage";
	private final String SLAP_FIRE_PERMISSION = "person.slap.fire";

	private File cfgFile;
	private FileConfiguration config;

	private int damageMin, damageMax;
	private int fireMin, fireMax, fireDefault;

	public AnnoyOTron() {
		super("AnnoyOTron");
		setCommand("slap");
	}

	public void onEnable() {
		cfgFile = new File(pp.getDataFolder(), "annoytron.prsn");
		config = Config.create(cfgFile, "fire.default", 5, "fire.min", 1, "fire.max", 10, "damage.min", 1, "damage.max", 5);
		damageMin = config.getInt("damage.min");
		damageMax = config.getInt("damage.max");
		fireMin = config.getInt("fire.min");
		fireMax = config.getInt("fire.max");
		fireDefault = config.getInt("fire.default");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (!hasPerm(p, SLAP_PERMISSION)) return true;

			if (args.length == 0) getHelpPlayer(p);
			else if (args.length == 1) {
				Player target = checkTarget(p, args[0]);
				if (target == null) return true;

				target.setVelocity(randomVel());
			} else if (args.length == 2) {
				if (args[1].equalsIgnoreCase("fire")) {
					if (!hasPerm(p, SLAP_FIRE_PERMISSION)) return true;

					Player target = checkTarget(p, args[0]);
					if (target == null) return true;

					target.setVelocity(randomVel());
					target.setFireTicks(fireDefault * 20);
				} else {
					if (!hasPerm(p, SLAP_DAMAGE_PERMISSION)) return true;

					Player target = checkTarget(p, args[0]);
					if (target == null) return true;

					int damage = isNumber(p, args[1]);
					if (damage == -1) return true;
					if (!checkDamage(p, damage)) return true;

					target.setVelocity(randomVel());
					target.damage(damage);
				}
			} else if (args.length == 3) {
				if (args[1].equalsIgnoreCase("fire")) {
					if (!hasPerm(p, SLAP_FIRE_PERMISSION)) return true;

					Player target = checkTarget(p, args[0]);
					if (target == null) return true;

					int fire = isNumber(p, args[2]);
					if (fire == -1) return true;
					if (!checkFire(p, fire)) return true;

					target.setVelocity(randomVel());
					target.setFireTicks(fire * 20);
				} else if (args[2].equalsIgnoreCase("fire")) {
					if (!hasPerm(p, SLAP_FIRE_PERMISSION)) return true;
					if (!hasPerm(p, SLAP_DAMAGE_PERMISSION)) return true;

					Player target = checkTarget(p, args[0]);
					if (target == null) return true;

					int damage = isNumber(p, args[1]);
					if (damage == -1) return true;
					if (!checkDamage(p, damage)) return true;

					target.setVelocity(randomVel());
					target.damage(damage);
					target.setFireTicks(fireDefault * 20);
				} else getHelpPlayer(p);
			} else if (args.length == 4) {
				if (args[2].equalsIgnoreCase("fire")) {
					if (!hasPerm(p, SLAP_FIRE_PERMISSION)) return true;
					if (!hasPerm(p, SLAP_DAMAGE_PERMISSION)) return true;

					Player target = checkTarget(p, args[0]);
					if (target == null) return true;

					int damage = isNumber(p, args[1]);
					if (damage == -1) return true;
					if (!checkDamage(p, damage)) return true;

					int fire = isNumber(p, args[3]);
					if (fire == -1) return true;
					if (!checkFire(p, fire)) return true;

					target.setVelocity(randomVel());
					target.damage(damage);
					target.setFireTicks(fire * 20);
				} else getHelpPlayer(p);
			} else getHelpPlayer(p);
		} else {
			if (args.length == 0) getHelpConsole();
			else if (args.length == 1) {
				Player target = checkTarget(null, args[0]);
				if (target == null) return true;

				target.setVelocity(randomVel());
			} else if (args.length == 2) {
				if (args[1].equalsIgnoreCase("fire")) {
					Player target = checkTarget(null, args[0]);
					if (target == null) return true;

					target.setVelocity(randomVel());
					target.setFireTicks(fireDefault * 20);
				} else {
					Player target = checkTarget(null, args[0]);
					if (target == null) return true;

					int damage = isNumber(null, args[1]);
					if (damage == -1) return true;
					if (!checkDamage(null, damage)) return true;

					target.setVelocity(randomVel());
					target.damage(damage);
				}
			} else if (args.length == 3) {
				if (args[1].equalsIgnoreCase("fire")) {
					Player target = checkTarget(null, args[0]);
					if (target == null) return true;

					int fire = isNumber(null, args[2]);
					if (fire == -1) return true;
					if (!checkFire(null, fire)) return true;

					target.setVelocity(randomVel());
					target.setFireTicks(fire * 20);
				} else if (args[2].equalsIgnoreCase("fire")) {
					Player target = checkTarget(null, args[0]);
					if (target == null) return true;

					int damage = isNumber(null, args[1]);
					if (damage == -1) return true;
					if (!checkDamage(null, damage)) return true;

					target.setVelocity(randomVel());
					target.damage(damage);
					target.setFireTicks(fireDefault * 20);
				} else getHelpConsole();
			} else if (args.length == 4) {
				if (args[2].equalsIgnoreCase("fire")) {
					Player target = checkTarget(null, args[0]);
					if (target == null) return true;

					int damage = isNumber(null, args[1]);
					if (damage == -1) return true;
					if (!checkDamage(null, damage)) return true;

					int fire = isNumber(null, args[3]);
					if (fire == -1) return true;
					if (!checkFire(null, fire)) return true;

					target.setVelocity(randomVel());
					target.damage(damage);
					target.setFireTicks(fire * 20);
				} else getHelpConsole();
			} else getHelpConsole();
		}
		return true;
	}

	private Player checkTarget(Player p, String name) {
		Player target = pp.getServer().getPlayer(name);
		if (target == null) {
			if (p != null) p.sendMessage(ChatColor.RED + "Player: " + name + " not found.");
			else pp.getLogger().info("Player: " + name + " not found.");
			return null;
		}
		return target;
	}

	private int isNumber(Player p, String str) {
		int temp = -1;
		try {
			temp = Integer.parseInt(str);
		} catch (Exception e) {
			if (p != null) p.sendMessage(ChatColor.RED + str + " is not a number!");
			else pp.getLogger().info(str + " is not a number!");
			return -1;
		}
		return temp;
	}

	private boolean checkDamage(Player p, int d) {
		if (!(d <= damageMax && d >= damageMin)) {
			if (p != null) p.sendMessage(ChatColor.RED + "Damage is not within bounds: " + damageMin + " to " + damageMax + "!");
			else pp.getLogger().info("Damage is not within bounds: " + damageMin + " to " + damageMax + "!");
			return false;
		}
		return true;
	}

	private boolean checkFire(Player p, int f) {
		if (!(f <= fireMax && f >= fireMin)) {
			if (p != null) p.sendMessage(ChatColor.RED + "Fire time is not within bounds: " + damageMin + " to " + damageMax + "!");
			else pp.getLogger().info("Fire time is not within bounds: " + damageMin + " to " + damageMax + "!");
			return false;
		}
		return true;
	}

	private boolean hasPerm(Player p, String perm) {
		if (!p.hasPermission(perm)) {
			p.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
			return false;
		}
		return true;
	}

	private void getHelpPlayer(Player p) {
		p.sendMessage(ChatColor.AQUA + getHelp(0));
		p.sendMessage(ChatColor.AQUA + getHelp(1));
		p.sendMessage(ChatColor.AQUA + getHelp(2));
		p.sendMessage(ChatColor.AQUA + getHelp(3));
		p.sendMessage(ChatColor.AQUA + getHelp(4));
		p.sendMessage(ChatColor.AQUA + getHelp(5));
	}

	private void getHelpConsole() {
		pp.getLogger().info(getHelp(0));
		pp.getLogger().info(getHelp(1));
		pp.getLogger().info(getHelp(2));
		pp.getLogger().info(getHelp(3));
		pp.getLogger().info(getHelp(4));
		pp.getLogger().info(getHelp(5));
	}

	private String getHelp(int i) {
		String s = "NOPE.AVI!";
		switch (i) {
			case 0:
				s = "/slap <player name> | Slaps the player around a bit. !!No Damage!!";
				break;
			case 1:
				s = "/slap <player name> <damage> | Slaps the player around AND damages them. Limits for damage are set in the config.";
				break;
			case 2:
				s = "/slap <player name> fire | Slaps the player around AND lights them on fire for the default time.";
				break;
			case 3:
				s = "/slap <player name> fire <time> | Slaps the player around AND lights them on fire for a time. Limits for time are set in the config.";
				break;
			case 4:
				s = "/slap <player name> <damage> fire | Slaps the player around AND damages them AND lights them on fire for the default time. Limits for damage are set in the config.";
				break;
			case 5:
				s = "/slap <player name> <damage> fire <time> | Slaps the player around AND damages them AND lights them on fire for a time. Limits for damage are set in the config, and limits for time are set in the config.";
				break;
		}
		return s;
	}

	private Vector randomVel() {
		return new Vector(rand.nextFloat() * getPosOrNeg(), rand.nextFloat(), rand.nextFloat() * getPosOrNeg());
	}

	private int getPosOrNeg() {
		return rand.nextBoolean() ? 1 : -1;
	}

}
