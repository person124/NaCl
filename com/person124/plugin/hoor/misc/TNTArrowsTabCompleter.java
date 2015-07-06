package com.person124.plugin.hoor.misc;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.person124.plugin.PPTabCompleter;

public class TNTArrowsTabCompleter extends PPTabCompleter {
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> list = null;
		if (args.length == 1) list = Arrays.asList("bow", "arrow");
		return list;
	}

}
