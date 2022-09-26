package me.bram2323.deathswap.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import me.bram2323.deathswap.DeathSwap;
import net.md_5.bungee.api.ChatColor;

public class DSStart implements TabExecutor {

	@SuppressWarnings("unused")
	private DeathSwap plugin;
	
	public DSStart(DeathSwap plugin) {
		this.plugin = plugin;
		plugin.getCommand("dsstart").setExecutor(this);
		plugin.getCommand("dsstart").setTabCompleter(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!sender.hasPermission("ds.start")) {
			sender.sendMessage(ChatColor.RED + "You do not have permision to use this command!");
			return true;
		}
		
		if (DeathSwap.game.state != 0) {
			sender.sendMessage(ChatColor.RED + "A game is still active!");
			return true;
		}
		
		Boolean dev =  (args.length == 1 && args[0].equals("true"));
		
		DeathSwap.game.start(dev);
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		List<String> list = new ArrayList<>();
		return list;
	}
}
