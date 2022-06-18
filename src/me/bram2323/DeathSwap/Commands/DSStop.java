package me.bram2323.DeathSwap.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import me.bram2323.DeathSwap.Main;
import net.md_5.bungee.api.ChatColor;

public class DSStop implements TabExecutor {

	@SuppressWarnings("unused")
	private Main plugin;
	
	public DSStop(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("dsstop").setExecutor(this);
		plugin.getCommand("dsstop").setTabCompleter(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!sender.hasPermission("ds.stop")) {
			sender.sendMessage(ChatColor.RED + "You do not have permision to use this command!");
			return true;
		}
		
		if (Main.game.State == 0) {
			sender.sendMessage(ChatColor.RED + "There is no game active!");
			return true;
		}
		
		Main.game.Stop();
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		List<String> list = new ArrayList<>();
		return list;
	}
	
}
