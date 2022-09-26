package me.bram2323.deathswap.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import me.bram2323.deathswap.DeathSwap;
import net.md_5.bungee.api.ChatColor;

public class DSInfo implements TabExecutor{

	@SuppressWarnings("unused")
	private DeathSwap plugin;
	
	public DSInfo(DeathSwap plugin) {
		this.plugin = plugin;
		plugin.getCommand("dsinfo").setExecutor(this);
		plugin.getCommand("dsinfo").setTabCompleter(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		sender.sendMessage(ChatColor.GOLD + "Death Swap Info");
		sender.sendMessage(ChatColor.DARK_GREEN + "Creator of plugin: " + ChatColor.GREEN + "Bram2323");
		sender.sendMessage(ChatColor.DARK_GREEN + "Original by: " + ChatColor.GREEN + "SethBling");
		sender.sendMessage(ChatColor.DARK_GREEN + "Plugin Version: " + ChatColor.GREEN + DeathSwap.version);
		
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		List<String> list = new ArrayList<>();		
		return list;
	}
}
