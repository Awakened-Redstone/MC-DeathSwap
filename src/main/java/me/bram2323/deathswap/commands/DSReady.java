package me.bram2323.deathswap.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import me.bram2323.deathswap.DeathSwap;
import me.bram2323.deathswap.settings.SettingsManager;
import net.md_5.bungee.api.ChatColor;

public class DSReady implements TabExecutor {

	static public List<UUID> Ready = new ArrayList<>();

	private Boolean AutoStart = false;
	
	@SuppressWarnings("unused")
	private DeathSwap plugin;
	
	public DSReady(DeathSwap plugin) {
		this.plugin = plugin;
		plugin.getCommand("dsready").setExecutor(this);
		plugin.getCommand("dsready").setTabCompleter(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only players may execute this command!");
			return true;
		}
		
		Player p = (Player) sender;
		
		if (DeathSwap.game.state != 0) {
			p.sendMessage(ChatColor.RED + "A game is still active!");
			return true;
		}
		
		if (args.length == 0) {
			if (Ready.contains(p.getUniqueId())) {
				Ready.remove(p.getUniqueId());
				Bukkit.broadcastMessage(ChatColor.DARK_RED + p.getName() + ChatColor.RED + " is no longer ready! " + ChatColor.WHITE + "[" + ChatColor.GREEN + Ready.toArray().length + "/" + Bukkit.getOnlinePlayers().toArray().length + ChatColor.WHITE + "]");
			}
			else {
				Ready.add(p.getUniqueId());
				Bukkit.broadcastMessage(ChatColor.DARK_GREEN + p.getName() + ChatColor.GREEN + " is ready! " + ChatColor.WHITE + "[" + ChatColor.GREEN + Ready.toArray().length + "/" + Bukkit.getOnlinePlayers().toArray().length + ChatColor.WHITE + "]");
				if (Ready.toArray().length == Bukkit.getOnlinePlayers().toArray().length && Ready.toArray().length > 1) {
					if ((int)SettingsManager.instance.GetSetting("AutoStart") < 0) {
						Bukkit.broadcastMessage(ChatColor.GREEN + "You can start the game with /dsready start");
					}
					else if (!AutoStart) {
						int Seconds = (int)SettingsManager.instance.GetSetting("AutoStart") / 20;
						Bukkit.broadcastMessage(ChatColor.GREEN + "The game is starting in " + Seconds + " seconds!");
						AutoStart = true;
						BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
						scheduler.scheduleSyncDelayedTask(DeathSwap.main, new Runnable() {
							@Override
							public void run() {
								AutoStart = false;
								Ready.clear();
								DeathSwap.game.start(false);
							}
						}, (int)SettingsManager.instance.GetSetting("AutoStart"));
					}
					else {
						Bukkit.broadcastMessage(ChatColor.GREEN + "The game is starting in soon!");
					}
				}
			}
		}
		else {
			if (args[0].equals("get")) {
				if (Ready.contains(p.getUniqueId())) {
					p.sendMessage(ChatColor.GREEN + "You are ready " + ChatColor.WHITE + "[" + ChatColor.GREEN + Ready.toArray().length + "/" + Bukkit.getOnlinePlayers().toArray().length + ChatColor.WHITE + "]");
				}
				else {
					p.sendMessage(ChatColor.RED + "You are not ready "  + ChatColor.WHITE + "[" + ChatColor.GREEN + Ready.toArray().length + "/" + Bukkit.getOnlinePlayers().toArray().length + ChatColor.WHITE + "]");
				}
			}
			else if (args[0].equals("start")) {
				if (AutoStart)
				{
					p.sendMessage(ChatColor.RED + "Can't start game manually when auto start is enabled!");
				}
				else if (Ready.toArray().length == Bukkit.getOnlinePlayers().toArray().length) {
					Ready.clear();
					Bukkit.broadcastMessage(ChatColor.GOLD + p.getName() + ChatColor.GREEN + " has started the game!");
					DeathSwap.game.start(false);
				}
				else {
					p.sendMessage(ChatColor.RED + "Not everyone is ready yet! " + ChatColor.WHITE + "[" + ChatColor.GREEN + Ready.toArray().length + "/" + Bukkit.getOnlinePlayers().toArray().length + ChatColor.WHITE + "]");
				}
			}
		}
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		List<String> list = new ArrayList<>();
		
		if (arg3.length == 1) {
			list.add("get");
			list.add("start");
		}
		
		return list;
	}
}
