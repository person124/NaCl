package com.person124.plugin.hoor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.ps.PS;
import com.person124.plugin.Config;
import com.person124.plugin.EnchantmentUtil;
import com.person124.plugin.PPBase;

public class TNTArrows extends PPBase {

	private Enchantment TNT_BOW = EnchantmentUtil.createEnchantment(131, "TNT_BOW_ENCHANT");
	private Enchantment TNT_ARROW = EnchantmentUtil.createEnchantment(132, "TNT_ARROW_ENCHANT");

	private final String ARROW_COMMAND_PERMISSION = "person.tntarrow";

	private File cfgFile;
	private FileConfiguration config;

	public TNTArrows() {
		super("TNTArrows");
		setHasEvents();
		setCommand("tntbow");
		setNeedsFolder();
	}

	public void onEnable() {
		cfgFile = new File(pp.getDataFolder(), "tntarrow.prsn");
		config = Config.createConfig(cfgFile, "explosion.power", 4.0F, "explosion.breakblocks", true, "explosion.causefire", true, "shootbow", Arrays.asList(new String[] { "Wilderness" }), "noexplode", Arrays.asList(new String[] { "SafeZone", "WarZone" }));

		pp.getLogger().info("Registering enchantments....");
		if (Enchantment.getByName(TNT_BOW.getName()) == null) {
			EnchantmentUtil.registerEnchants(TNT_BOW, TNT_ARROW);
		} else pp.getLogger().info("Canceling enchantment registeration; enchantment already exists.");

		pp.getLogger().info("Registering recipies....");
		addRecipe(new ItemStack(Material.BOW, 1), new Object[] { " !@", "!#@", " !@", '!', Material.BLAZE_ROD, '@', Material.STRING, '#', Material.REDSTONE_TORCH_ON });
		addRecipe(new ItemStack(Material.ARROW, 1), new Object[] { " ! ", "@#@", " ! ", '!', Material.TNT, '@', Material.SLIME_BALL, '#', Material.ARROW });
		addRecipe(new ItemStack(Material.ARROW, 1), new Object[] { " ! ", "@#@", " ! ", '!', Material.SLIME_BALL, '@', Material.TNT, '#', Material.ARROW });
	}

	@EventHandler
	public void onItemCraft(PrepareItemCraftEvent event) {
		if (event.getRecipe().getResult().getType() == Material.BOW) {
			ItemStack[] stacks = event.getInventory().getMatrix();
			boolean blaze = false;
			for (ItemStack is : stacks) {
				if (is != null) if (is.getType() == Material.BLAZE_ROD) blaze = true;
			}
			if (!blaze) return;
			event.getInventory().setResult(getBowItem());
		} else if (event.getRecipe().getResult().getType() == Material.ARROW) {
			ItemStack[] stacks = event.getInventory().getMatrix();
			boolean tnt = false;
			for (ItemStack is : stacks) {
				if (is != null) if (is.getType() == Material.TNT) tnt = true;
			}
			if (!tnt) return;
			event.getInventory().setResult(getArrowItem());
		}
	}

	@EventHandler
	public void playerShootBowEvent(EntityShootBowEvent event) {
		if (event.getEntity() instanceof Player) {
			if (event.getBow().getItemMeta().hasEnchant(TNT_BOW)) {
				System.out.println(true);
				Player p = (Player) event.getEntity();

				//ItemMeta meta = event.getBow().getItemMeta();
				if (event.getBow().getItemMeta().hasEnchant(Enchantment.ARROW_INFINITE)) {
					event.getBow().getItemMeta().removeEnchant(Enchantment.ARROW_INFINITE);
					p.sendMessage(ChatColor.AQUA + " For some reason, your bow feels slightly less magical now....");
					//event.getBow().setItemMeta(meta);
					event.setCancelled(true);
					return;
				}

				if (!p.getInventory().containsAtLeast(getArrowItem(), 1) || isInFaction(p)) event.setCancelled(true);
				else {
					p.getInventory().remove(getArrowItem());
					event.getProjectile().setCustomName("boom");
					event.getProjectile().setCustomNameVisible(false);
				}
			}
		}
	}

	@EventHandler
	public void arrowHitEvent(final ProjectileHitEvent event) {
		if (event.getEntity() instanceof Arrow && event.getEntity().getCustomName() != null) {
			if (!event.getEntity().getCustomName().equals("boom")) return;
			if (isInSafeZone(event.getEntity())) return;
			pp.getServer().getScheduler().scheduleSyncDelayedTask(pp, new Runnable() {
				public void run() {
					double x = event.getEntity().getLocation().getX();
					double y = event.getEntity().getLocation().getY();
					double z = event.getEntity().getLocation().getZ();
					event.getEntity().getWorld().createExplosion(x, y, z, config.getInt("explosion.power"), config.getBoolean("explosion.breakblocks"), config.getBoolean("explosion.causefire"));
					event.getEntity().remove();
				}
			});
		}
	}

	@EventHandler
	public void anvilUseEvent(InventoryClickEvent event) {
		if (event.getClickedInventory() instanceof AnvilInventory) {
			if (event.getSlot() == 2) {
				if (!event.getInventory().getItem(0).isSimilar(getBowItem())) return;
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void anvilDragEvent(InventoryDragEvent event) {
		if (!event.getOldCursor().isSimilar(getBowItem())) return;
		else event.setCancelled(true);
	}

	private ItemStack getBowItem() {
		ItemStack stack = new ItemStack(Material.BOW, 1);

		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName("T.N.T. Bow");
		List<String> lore = new ArrayList<String>();
		lore.add("Use with the T.N.T. Arrow(s)");
		lore.add("to create explosions!");
		meta.setLore(lore);

		stack.setItemMeta(meta);
		stack.addUnsafeEnchantment(TNT_BOW, 1);

		return stack;
	}

	private ItemStack getArrowItem() {
		ItemStack stack = new ItemStack(Material.ARROW, 1);

		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName("T.N.T. Arrow");
		List<String> lore = new ArrayList<String>();
		lore.add("Use with the T.N.T. Bow");
		lore.add("to create explosions!");
		meta.setLore(lore);

		stack.setItemMeta(meta);
		stack.addUnsafeEnchantment(TNT_ARROW, 1);

		return stack;
	}

	private boolean isInFaction(Entity e) {
		Faction f = BoardColl.get().getFactionAt(PS.valueOf(e));
		for (Object o : config.getList("noshootbow")) {
			String str = String.valueOf(o);
			if (f.getName().contains(str)) return true;
		}
		return false;
	}

	private boolean isInSafeZone(Entity e) {
		Faction f = BoardColl.get().getFactionAt(PS.valueOf(e));
		if (config.getList("noexplode") == null) return false;
		for (Object o : config.getList("noexplode")) {
			String str = String.valueOf(o);
			if (f.getName().contains(str)) return true;
		}
		return false;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (!(p.hasPermission(ARROW_COMMAND_PERMISSION) || p.isOp())) p.sendMessage(ChatColor.AQUA + "You do not have the permission to use this command!");
			else {
				if (args.length == 0) {
					p.sendMessage("/tntbow bow | Gives you the tnt bow.");
					p.sendMessage("/tntbow arrow | Gives you one tnt arrow.");
					p.sendMessage("/tntbow arrow <amount> | Gives you that amount of tnt arrows.");
				} else if (args.length == 1) {
					if (args[0].equalsIgnoreCase("bow")) {
						p.sendMessage("Bing!");
						p.getInventory().addItem(getBowItem());
					} else if (args[0].equalsIgnoreCase("arrow")) {
						p.sendMessage("Bing!");
						p.getInventory().addItem(getArrowItem());
					}
				} else if (args.length == 2) {
					if (args[0].equalsIgnoreCase("arrow")) {
						p.sendMessage("Bing!");
						ItemStack is = getArrowItem();
						is.setAmount(Integer.parseInt(args[1]));
						p.getInventory().addItem(is);
					}
				}
			}
			return true;
		} else {
			pp.getLogger().info("You need to be a player to use this command!");
			return true;
		}
	}

}
