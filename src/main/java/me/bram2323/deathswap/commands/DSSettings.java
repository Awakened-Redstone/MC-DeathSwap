package me.bram2323.deathswap.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import me.bram2323.deathswap.DeathSwap;
import me.bram2323.deathswap.settings.SettingsManager;
import net.md_5.bungee.api.ChatColor;

public class DSSettings implements TabExecutor {
 
	@SuppressWarnings("unused")
	private DeathSwap plugin;
	
	public DSSettings(DeathSwap plugin) {
		this.plugin = plugin;
		plugin.getCommand("dssettings").setExecutor(this);
		plugin.getCommand("dssettings").setTabCompleter(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!sender.hasPermission("ds.settings")) {
			sender.sendMessage(ChatColor.RED + "You do not have permision to use this command!");
			return true;
		}
		
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Usage: /dssettings <set,get,help>");
			return true;
		} 
		else if(args.length == 1 && args[0].equals("help")) {
			sender.sendMessage(ChatColor.GOLD + "'dssettings' help:");
			sender.sendMessage(ChatColor.DARK_GREEN + "- help: " + ChatColor.GREEN + "shows this page");
			sender.sendMessage(ChatColor.DARK_GREEN + "- get: " + ChatColor.GREEN + "sends all the settings with values");
			sender.sendMessage(ChatColor.DARK_GREEN + "- set: " + ChatColor.GREEN + "change a setting");
			sender.sendMessage(ChatColor.AQUA + "(use '/dssettings help set' for a list of all the settings)");
		} 
		else if (args.length == 2 && args[0].equals("help")) {	
			sender.sendMessage(ChatColor.GOLD + "'dssettings set' help:");
			sender.sendMessage(SettingsManager.instance.GetHelpString());
			sender.sendMessage(ChatColor.RED + "Time is measured in ticks! (20 Ticks is 1 second)");
			sender.sendMessage(ChatColor.AQUA + "(use '/dssettings help set TeleportMode' for a list of all the teleport modes)");
		} 
		else if (args.length >= 3 && args[0].equals("help")) {
			sender.sendMessage(ChatColor.GOLD + "'dssettings set TeleportMode' help:");
			sender.sendMessage(ChatColor.DARK_GREEN + "- random: " + ChatColor.GREEN + "Players will be teleported to a random player (If it can't find a assortment in 500 trys it wil use the random circle method!)");
			sender.sendMessage(ChatColor.DARK_GREEN + "- circle: " + ChatColor.GREEN + "Players will swap in a random circle thats generated at the start of the game");
			sender.sendMessage(ChatColor.DARK_GREEN + "- random_circle: " + ChatColor.GREEN + "Circle method only the circle will change when a swap occurs");
			sender.sendMessage(ChatColor.DARK_GREEN + "- fixed_circle: " + ChatColor.GREEN + "Circle method only the circle won't change between games");
			sender.sendMessage(ChatColor.DARK_GREEN + "- pairs: " + ChatColor.GREEN + "Players will be teleported in pairs (If there is a uneven amount of players there wil be a 3 player pair)");
		} 
		else if (args.length == 1 || args.length == 2){
			if (args[0].equals("get")) {
				sender.sendMessage(SettingsManager.instance.GetSettingsString());
			} 
			else if(args[0].equals("set")) {
				sender.sendMessage(ChatColor.RED + "Usage: /dssettings set <setting> <value> (Time needs to be in ticks!)");
			} 
			else {
				sender.sendMessage(ChatColor.RED + "Usage: /dssettings <set,get,help>");
			}
		} 
		else if (args.length >= 3) {
			if (args[0].equals("get")) {
				sender.sendMessage(SettingsManager.instance.GetSettingsString());
			} 
			else if(args[0].equals("set")) {
				if (args[1].toLowerCase().equals("teleportmode")) sender.sendMessage(SettingsManager.instance.SetTeleportMode(args[2]));
				else sender.sendMessage(SettingsManager.instance.SetSetting(args[1], args[2]));
				return true;
			} 
			else {
				sender.sendMessage(ChatColor.RED + "Usage: /dssettings <set,get,help>");
			}
		}
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		
		List<String> list = new ArrayList<>();
		
		if(args.length == 1) {
			list.add("get");
			list.add("set");
			list.add("help");
		}
		else if(args.length == 2 && args[0].equals("set")) {
			return SettingsManager.instance.GetSettingNamesList(args[1]);
		}
		else if(args.length == 3 && args[0].equals("set") && args[1].equals("World")) {
			for (World world : Bukkit.getServer().getWorlds()) {
				list.add(world.getName());
			}
		}
		else if(args.length == 3 && args[0].equals("set") && args[1].equals("TeleportMode")) {
			list.add("circle");
			list.add("random");
			list.add("random_circle");
			list.add("fixed_circle");
			list.add("pairs");
		}
		else if(args.length == 3 && args[0].equals("set") && SettingsManager.instance.SettingExists(args[1])) {
			return SettingsManager.instance.GetSettingTabcomplete(args[1]);
		}
		else if(args.length == 2 && args[0].equals("help")) {
			list.add("set");
		}
		else if(args.length == 3 && args[0].equals("help")) {
			list.add("TeleportMode");
		}
		
		return list;
	}

}
