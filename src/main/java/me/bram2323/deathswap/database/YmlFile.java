package me.bram2323.deathswap.database;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.bram2323.deathswap.DeathSwap;

public class YmlFile {
	   
    DeathSwap plugin = DeathSwap.getPlugin(DeathSwap.class);
   
    public void fileCheck(Player player){
   
	     String playerName = player.getName();
	     String playerUuid = player.getUniqueId().toString();
	     File userdata = new File(plugin.getDataFolder(), File.separator + "PlayerDatabase");
	     File f = new File(userdata, File.separator + playerUuid + ".yml");
	     FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);
	
	     if (!f.exists()) {
	         try {
	
	             playerData.createSection("Data");
	             playerData.set("Data.Name", playerName);
	             playerData.set("Data.UUID", playerUuid);
	           
	             playerData.createSection("Stats");
	             playerData.set("Stats.Wins", 0);
		    		playerData.set("Stats.Deaths", 0);
		    		playerData.set("Stats.Disconnected", 0);
		    		playerData.set("Stats.Games", 0);
		    		playerData.set("Stats.Swaps", 0);
		    		playerData.set("Stats.Time", 0);
	           
	             playerData.save(f);
	         } catch (IOException exception) {
	
	        	 exception.printStackTrace();
	         }
	     }
    }
    
    public int ReadData(UUID uuid, String data) {
	     File userdata = new File(plugin.getDataFolder(), File.separator + "PlayerDatabase");
	     File f = new File(userdata, File.separator + uuid + ".yml");
	     FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);
	
	     if (!f.exists()) {
	         return 0;
	     }
	     
	     if (!playerData.contains(data)) {
	    	 return 0;
	     }
	     
	     int Data = (int) playerData.get(data);
	     
	     return Data;
    }
    
    public void WriteData(Player player, String data, Object value) {
    	if (DeathSwap.game.dev) return;
    	String playerName = player.getName();
    	String playerUuid = player.getUniqueId().toString();
	    File userdata = new File(plugin.getDataFolder(), File.separator + "PlayerDatabase");
	    File f = new File(userdata, File.separator + playerUuid + ".yml");
	    FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);
	
	    if (!f.exists()) {
	    	try {
	
	    		playerData.createSection("Data");
	    		playerData.set("Data.Name", playerName);
	    		playerData.set("Data.UUID", playerUuid);
	    		
	    		playerData.createSection("Stats");
	    		playerData.set("Stats.Wins", 0);
	    		playerData.set("Stats.Deaths", 0);
	    		playerData.set("Stats.Disconnected", 0);
	    		playerData.set("Stats.Games", 0);
	    		playerData.set("Stats.Swaps", 0);
	    		playerData.set("Stats.Time", 0);
	    		
	    		playerData.save(f);
	    	} catch (IOException exception) {
	
	    		exception.printStackTrace();
	    	}
	    } else {
	    	
	    }
	    playerData.set(data, value);
	    try {
	    	playerData.save(f);
	    } catch (IOException exception) {
	    	exception.printStackTrace();
	    }
    }
}