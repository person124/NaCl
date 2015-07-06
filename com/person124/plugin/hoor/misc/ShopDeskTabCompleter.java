package com.person124.plugin.hoor.misc;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.person124.plugin.PPTabCompleter;

public class ShopDeskTabCompleter extends PPTabCompleter {

	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> list = null;
		if (args.length == 1) list = getList(args[0], "expand", "parse", "highest", "remove");
		return list;
	}
	
}
