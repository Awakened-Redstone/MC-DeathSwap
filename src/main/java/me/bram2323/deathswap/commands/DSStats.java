package me.bram2323.deathswap.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import me.bram2323.deathswap.DeathSwap;
import me.bram2323.deathswap.database.YmlFile;
import net.md_5.bungee.api.ChatColor;

public class DSStats implements TabExecutor {

	@SuppressWarnings("unused")
	private DeathSwap plugin;
	
	public DSStats(DeathSwap plugin) {
		this.plugin = plugin;
		plugin.getCommand("dsstats").setExecutor(this);
		plugin.getCommand("dsstats").setTabCompleter(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only player may execute this command");
			return true;
		}
		
		YmlFile yml = new YmlFile();
		
		Player p = (Player) sender;
		
		if (args.length == 0) {
			p.sendMessage(ChatColor.DARK_GREEN + "Stats of " + ChatColor.GOLD + p.getName());
			p.sendMessage(ChatColor.GREEN + "Games: " + ChatColor.DARK_GREEN + yml.ReadData(p.getUniqueId(), "Stats.Games"));
			p.sendMessage(ChatColor.GREEN + "Wins: " + ChatColor.GOLD + yml.ReadData(p.getUniqueId(), "Stats.Wins"));
			p.sendMessage(ChatColor.GREEN + "Swaps: " + ChatColor.DARK_GREEN + yml.ReadData(p.getUniqueId(), "Stats.Swaps"));
			p.sendMessage(ChatColor.GREEN + "Deaths: " + ChatColor.RED + yml.ReadData(p.getUniqueId(), "Stats.Deaths"));
			p.sendMessage(ChatColor.GREEN + "Disconnected: " + ChatColor.RED + yml.ReadData(p.getUniqueId(), "Stats.Disconnected"));
			int Time = yml.ReadData(p.getUniqueId(), "Stats.Time");
			int Minutes = 0;
			while (Time >= 1200) {
				Minutes++;
				Time -= 1200;
			}
			if (Time < 200) {
				p.sendMessage(ChatColor.GREEN + "Time played: " + ChatColor.DARK_GREEN + Minutes + ":0" + Time / 20f);
			} else {
				p.sendMessage(ChatColor.GREEN + "Time played: " + ChatColor.DARK_GREEN + Minutes + ":" + Time / 20f);
			}
		} else {
			@SuppressWarnings("deprecation")
			OfflinePlayer tt = Bukkit.getOfflinePlayer(args[0]);
			
				p.sendMessage(ChatColor.DARK_GREEN + "Stats of " + ChatColor.GOLD + tt.getName());
				p.sendMessage(ChatColor.GREEN + "Games: " + ChatColor.DARK_GREEN + yml.ReadData(tt.getUniqueId(), "Stats.Games"));
				p.sendMessage(ChatColor.GREEN + "Wins: " + ChatColor.GOLD + yml.ReadData(tt.getUniqueId(), "Stats.Wins"));
				p.sendMessage(ChatColor.GREEN + "Swaps: " + ChatColor.DARK_GREEN + yml.ReadData(tt.getUniqueId(), "Stats.Swaps"));
				p.sendMessage(ChatColor.GREEN + "Deaths: " + ChatColor.RED + yml.ReadData(tt.getUniqueId(), "Stats.Deaths"));
				p.sendMessage(ChatColor.GREEN + "Disconnected: " + ChatColor.RED + yml.ReadData(tt.getUniqueId(), "Stats.Disconnected"));
				int Time = yml.ReadData(tt.getUniqueId(), "Stats.Time");
				int Minutes = 0;
				while (Time >= 1200) {
					Minutes++;
					Time -= 1200;
				}
				if (Time < 200) {
					p.sendMessage(ChatColor.GREEN + "Time played: " + ChatColor.DARK_GREEN + Minutes + ":0" + Time / 20f);
				} else {
					p.sendMessage(ChatColor.GREEN + "Time played: " + ChatColor.DARK_GREEN + Minutes + ":" + Time / 20f);
				}
		}
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		List<String> list = new ArrayList<>();
		if (arg3.length == 1) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				list.add(p.getName());
			}
		}
		return list;
	}
	
}
