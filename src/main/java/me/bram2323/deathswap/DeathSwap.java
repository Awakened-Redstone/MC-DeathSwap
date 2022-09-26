package me.bram2323.deathswap;

import org.bukkit.World;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import me.bram2323.deathswap.commands.DSInfo;
import me.bram2323.deathswap.commands.DSKill;
import me.bram2323.deathswap.commands.DSReady;
import me.bram2323.deathswap.commands.DSSettings;
import me.bram2323.deathswap.commands.DSStart;
import me.bram2323.deathswap.commands.DSStats;
import me.bram2323.deathswap.commands.DSStop;
import me.bram2323.deathswap.commands.DSTP;
import me.bram2323.deathswap.game.Events;
import me.bram2323.deathswap.game.Game;
import me.bram2323.deathswap.settings.SettingsManager;
import me.bram2323.deathswap.settings.SettingsModifier;

public class DeathSwap extends JavaPlugin {

    static public World world;
    static public World mainWorld;
    static public String version;

    static public DeathSwap main;
    static public Game game;

    @Override
    public void onEnable() {
        main = this;

        SettingsManager settingsManager = new SettingsManager();

        game = new Game();
        main = this;
        new DSSettings(this);
        new DSStart(this);
        new DSStop(this);
        new DSInfo(this);
        new DSReady(this);
        new DSKill(this);
        new DSTP(this);
        new DSStats(this);

        world = getServer().getWorlds().get(0);
        mainWorld = getServer().getWorlds().get(0);

        settingsManager.AddSetting("AutoStart", -1, "The time the game will start after everyone is ready (set to -1 to disable)", true, SettingsModifier.HigherEquals, -1, SettingsModifier.Ignore, "");
        settingsManager.AddSetting("MinTimer", 2400, "The minimum amount of time before a swap", true, SettingsModifier.Higher, 0, SettingsModifier.LowerEquals, "MaxTimer");
        settingsManager.AddSetting("MaxTimer", 6000, "The maximum amount of time before a swap", true, SettingsModifier.Higher, 0, SettingsModifier.HigherEquals, "MinTimer");
        settingsManager.AddSetting("Warning", 200, "The time you'll get a warning before a swap", true, SettingsModifier.HigherEquals, 0, SettingsModifier.Ignore, "");
        settingsManager.AddSetting("Safe", 0, "The time of invincibility after a swap", true, SettingsModifier.HigherEquals, 0, SettingsModifier.Ignore, "");
        settingsManager.AddSetting("LoadChunkRadius", 2, "The radius of chunks around the player that are loaded at the start of the game", false, SettingsModifier.HigherEquals, 0, SettingsModifier.Ignore, "");
        settingsManager.AddSetting("ClearInv", true, "Set to clear everyone's inventory at the start of a game");
        settingsManager.AddSetting("TeleportMode", "random", "The method that will be used when teleporting");
        settingsManager.AddSetting("RandomSpawn", true, "Set to spawn everyone at a random location at the start of the game");
        settingsManager.AddSetting("TeleportRadius", 1000000, "The radius in wich players will be teleported at the start of a game if RandomSpawn is enabled", false, SettingsModifier.Higher, 0, SettingsModifier.Ignore, "");
        settingsManager.AddSetting("KillPearls", true, "Kill all enderpearls at a swap");
        settingsManager.AddSetting("ShowTime", true, "Set to show the timers and if a swap can occur");
        settingsManager.AddSetting("World", world.getName(), "The world where everyone will spawn in at the start");
        settingsManager.AddSetting("RevokeAdvancements", true, "Set to reset all advancements of everyone at the start of the game");
        //settingsManager.AddSetting("SendChunks", true, "Set to send chunks at a swap");

        PluginDescriptionFile pdf = this.getDescription();
        version = pdf.getVersion();
        getServer().getPluginManager().registerEvents(new Events(), this);
        this.getLogger().info("[DeathSwap] Enabled plugin!");
    }


    public void onDisable() {
        this.getConfig().options().copyDefaults(true);
        saveConfig();
    }
}
