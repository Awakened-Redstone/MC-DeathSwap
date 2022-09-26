package me.bram2323.deathswap.game;


import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;

import me.bram2323.deathswap.DeathSwap;
import me.bram2323.deathswap.commands.DSReady;
import me.bram2323.deathswap.database.YmlFile;
import me.bram2323.deathswap.settings.SettingsManager;
import net.md_5.bungee.api.ChatColor;

public class Events implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player p = event.getPlayer();

        if (DeathSwap.game.state != 0) {
            p.setGameMode(GameMode.SPECTATOR);
        }
    }

    @EventHandler
    public void onPlayerDisconect(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        Game game = DeathSwap.game;

        DSReady.Ready.remove(p.getUniqueId());
        if (game.state != 0) {
            if (game.inGame.contains(p.getUniqueId())) {
                YmlFile ymlfile = new YmlFile();
                ymlfile.WriteData(p, "Stats.Disconnected", 1 + ymlfile.ReadData(p.getUniqueId(), "Stats.Disconnected"));
            }
            game.removePlayer(p);
            if ((Boolean) SettingsManager.instance.GetSetting("RandomSpawn"))
                p.teleport(DeathSwap.world.getSpawnLocation());
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            p.setScoreboard(manager.getNewScoreboard());
            p.setGameMode(GameMode.SURVIVAL);
        }
    }

    //private HashMap<UUID, Location> playerNextSpawnLocation = new HashMap<>();

    /*@EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player p = event.getEntity();
        Game game = DeathSwap.game;
        Location deathLocation = p.getLocation().clone();
        if (game.state != 0 && game.inGame.contains(p.getUniqueId())) {
            YmlFile ymlfile = new YmlFile();
            ymlfile.WriteData(p, "Stats.Deaths", 1 + ymlfile.ReadData(p.getUniqueId(), "Stats.Deaths"));
        }
        if (game.state != 0) {
            game.removePlayer(p);
            p.sendMessage(ChatColor.AQUA + "You can use /dstp <player> to teleport to players!");
            playerNextSpawnLocation.put(p.getUniqueId(), deathLocation);
            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
            scheduler.scheduleSyncDelayedTask(DeathSwap.main, new Runnable() {
                @Override
                public void run() {
                    p.spigot().respawn();
                }
            }, 1);
        }
    }*/

    /*@EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player p = event.getPlayer();
        if (playerNextSpawnLocation.containsKey(p.getUniqueId())) {
            event.setRespawnLocation(playerNextSpawnLocation.get(p.getUniqueId()));
            playerNextSpawnLocation.remove(p.getUniqueId());
        }
    }*/
}
