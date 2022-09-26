package me.bram2323.deathswap.game;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.advancement.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.Vector;

import io.netty.util.internal.ThreadLocalRandom;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import me.bram2323.deathswap.DeathSwap;
import me.bram2323.deathswap.database.YmlFile;
import me.bram2323.deathswap.settings.SettingsManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Game {

    Objective objective;

    public World world;

    public static HashMap<UUID, Location> locations = new HashMap<UUID, Location>();

    public List<UUID> inGame = new ArrayList<>();
    public List<UUID> randomTP = new ArrayList<>();
    public List<Location> randomLoc = new ArrayList<>();

    public List<UUID[]> Pairs = new ArrayList<>();

    public int state = 0;

    int try_ = 0;
    int trys = 0;
    Boolean trySucces;
    public Boolean dev;

    int totalTimer = 0;
    int mainTimer = 0;
    int timer = 0;
    int seconds = 0;
    int minutes = 0;
    int untilSwap = 0;
    int totalSwap = 0;

    public void start(Boolean devMode) {

        dev = devMode;

        if (state != 0) return;

        if (!(Bukkit.getOnlinePlayers().size() > 1 || (dev && Bukkit.getOnlinePlayers().size() > 0))) {
            Bukkit.broadcastMessage(ChatColor.RED + "You need at least 2 players to start the game!");
            return;
        }

        world = Bukkit.getServer().getWorld((String) SettingsManager.instance.GetSetting("World"));
        if (world == null) world = Bukkit.getServer().getWorlds().get(0);

        Bukkit.broadcastMessage("DeathSwap is about to start!");
        world.setTime(0);

        inGame.clear();
        locations.clear();

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getMainScoreboard();
        if (board.getObjective("showhealth") != null) board.getObjective("showhealth").unregister();
        objective = board.registerNewObjective("showhealth", "health", "Health");
        objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        objective.setRenderType(RenderType.HEARTS);

        for (Player p : Bukkit.getOnlinePlayers()) {
            inGame.add(p.getUniqueId());
            p.spigot().respawn();
            p.setGameMode(GameMode.SURVIVAL);
            if ((Boolean) SettingsManager.instance.GetSetting("ClearInv")) p.getInventory().clear();
            for (PotionEffect effect : p.getActivePotionEffects()) p.removePotionEffect(effect.getType());
            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, (int) SettingsManager.instance.GetSetting("Safe"), 255));
            if ((Boolean) SettingsManager.instance.GetSetting("RandomSpawn")) p.teleport(world.getSpawnLocation());
            p.setHealth(19);
            p.setSaturation(20);
            p.setFoodLevel(20);
            p.setFallDistance(0);
            p.setExp(0);
            p.setLevel(0);
            p.setFireTicks(0);
            YmlFile ymlfile = new YmlFile();
            ymlfile.WriteData(p, "Stats.Games", 1 + ymlfile.ReadData(p.getUniqueId(), "Stats.Games"));

            if ((Boolean) SettingsManager.instance.GetSetting("RevokeAdvancements")) {
                Iterator<Advancement> advancements = Bukkit.getServer().advancementIterator();
                while (advancements.hasNext()) {
                    AdvancementProgress progress = p.getAdvancementProgress(advancements.next());
                    for (String s : progress.getAwardedCriteria())
                        progress.revokeCriteria(s);
                }
            }
        }

        if (SettingsManager.instance.GetTeleportMode() == 4) makePairs();

        for (World w : Bukkit.getWorlds()) {
            for (Entity e : w.getEntities()) {
                if (e.getType() == EntityType.ENDER_PEARL) {
                    e.remove();
                }
            }
        }

        if (SettingsManager.instance.GetTeleportMode() != 3) Collections.shuffle(inGame);

        Random rand = new Random();

        untilSwap = rand.nextInt((int) SettingsManager.instance.GetSetting("MaxTimer") + 1 - (int) SettingsManager.instance.GetSetting("MinTimer")) + (int) SettingsManager.instance.GetSetting("MinTimer");
        totalTimer = 0;
        timer = 0;
        seconds = 0;
        minutes = 0;
        mainTimer = 0;
        trys = 0;
        totalSwap = 0;

        if ((Boolean) SettingsManager.instance.GetSetting("RandomSpawn")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                Location randomLocation = getRandomLocation(world);
                p.teleport(randomLocation);
                p.setBedSpawnLocation(randomLocation, true);
            }
        }

        int Loaded = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            locations.put(p.getUniqueId(), p.getLocation());
            Chunk ch = p.getLocation().getChunk();
            p.setGameMode(GameMode.SPECTATOR);
            if ((int) SettingsManager.instance.GetSetting("LoadChunkRadius") > 0)
                for (int i = (int) SettingsManager.instance.GetSetting("LoadChunkRadius") * -1 + 1; i < (int) SettingsManager.instance.GetSetting("LoadChunkRadius"); i++) {
                    for (int j = (int) SettingsManager.instance.GetSetting("LoadChunkRadius") * -1 + 1; j < (int) SettingsManager.instance.GetSetting("LoadChunkRadius"); j++) {
                        Chunk chunk = Bukkit.getWorld(ch.getWorld().getUID()).getChunkAt(ch.getX() + i, ch.getX() + j);
                        chunk.load(true);
                        if (Bukkit.getWorld(ch.getWorld().getUID()).getChunkAt(ch.getX() + i, ch.getX() + j).isLoaded())
                            Loaded++;
                        for (Player t : Bukkit.getOnlinePlayers()) {
                            t.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Generating chunks: " + ChatColor.GREEN + Loaded + ChatColor.WHITE + "/" + ChatColor.GOLD + Bukkit.getOnlinePlayers().size() * ((int) SettingsManager.instance.GetSetting("LoadChunkRadius") * 2 - 1) * ((int) SettingsManager.instance.GetSetting("LoadChunkRadius") * 2 - 1)));
                        }
                    }
                }
        }
        Loaded = 0;
        if ((int) SettingsManager.instance.GetSetting("LoadChunkRadius") > 0) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                Chunk ch = p.getLocation().getChunk();
                for (int i = (int) SettingsManager.instance.GetSetting("LoadChunkRadius") * -1 + 1; i < (int) SettingsManager.instance.GetSetting("LoadChunkRadius"); i++) {
                    for (int j = (int) SettingsManager.instance.GetSetting("LoadChunkRadius") * -1 + 1; j < (int) SettingsManager.instance.GetSetting("LoadChunkRadius"); j++) {
                        Chunk chunk = Bukkit.getWorld(ch.getWorld().getUID()).getChunkAt(ch.getX() + i, ch.getX() + j);
                        sendChunk(chunk, p);
                        Loaded++;
                        for (Player t : Bukkit.getOnlinePlayers()) {
                            t.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Sending chunks: " + ChatColor.GREEN + Loaded + ChatColor.WHITE + "/" + ChatColor.GOLD + Bukkit.getOnlinePlayers().size() * ((int) SettingsManager.instance.GetSetting("LoadChunkRadius") * 2 - 1) * ((int) SettingsManager.instance.GetSetting("LoadChunkRadius") * 2 - 1)));
                        }
                    }
                }
            }
        }

        Bukkit.broadcastMessage(ChatColor.GREEN + "Death Swap, coded by " + ChatColor.GOLD + "bram2323" + ChatColor.GREEN + ", pitched by " + ChatColor.GOLD + "SethBling" + ChatColor.GREEN + ":\n" + ChatColor.WHITE + SettingsManager.instance.GetSettingsGameString() + ChatColor.GOLD + "\nGood luck!");

        String devm = "";
        boolean first = true;

        for (UUID uuid : inGame) {
            if (!first) devm += ", ";
            devm += Bukkit.getPlayer(uuid).getName();
            first = false;
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.isOp() && dev) {
                p.sendMessage(ChatColor.DARK_GREEN + "The next circle of teleportation is: " + devm);
            }
        }

        state = 1;
        ticker();
    }

    public void stop() {

        if (state < 1) {
            return;
        }

        if (inGame.size() == 1) {
            Player tt = Bukkit.getPlayer(inGame.get(0));
            Bukkit.broadcastMessage(ChatColor.GOLD + tt.getName() + ChatColor.RESET + ChatColor.GREEN + " has won the game!");

            for (Player t : Bukkit.getOnlinePlayers()) {
                if ((Boolean) SettingsManager.instance.GetSetting("RandomSpawn")) t.teleport(world.getSpawnLocation());
                if (!t.getUniqueId().equals(tt.getUniqueId())) {
                    t.teleport(world.getSpawnLocation());
                    t.sendTitle(ChatColor.GOLD + tt.getName() + ChatColor.RESET + ChatColor.GREEN + " has won!", ChatColor.DARK_RED + "You lost...", 10, 80, 10);
                    t.playSound(t.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0F, 1.0F);
                } else {
                    t.sendTitle(ChatColor.GOLD + "You" + ChatColor.RESET + ChatColor.GREEN + " win!", ChatColor.DARK_GREEN + "Good Job!", 10, 80, 10);
                    t.playSound(t.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F);
                    YmlFile ymlfile = new YmlFile();
                    ymlfile.WriteData(t, "Stats.Wins", 1 + ymlfile.ReadData(t.getUniqueId(), "Stats.Wins"));
                    ymlfile.WriteData(t, "Stats.Time", ymlfile.ReadData(t.getUniqueId(), "Stats.Time") + totalTimer);
                }
            }
        } else {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.teleport(world.getSpawnLocation());
                p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0F, 1.0F);
                YmlFile ymlfile = new YmlFile();
                ymlfile.WriteData(p, "Stats.Disconnected", 1 + ymlfile.ReadData(p.getUniqueId(), "Stats.Disconnected"));
            }
            Bukkit.broadcastMessage("DeathSwap has stopped!");
        }

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        objective.unregister();

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setGameMode(GameMode.SURVIVAL);
            p.setFallDistance(0);
            p.setHealth(19);
            p.setSaturation(20);
            p.setFoodLevel(20);
            p.setFireTicks(0);
            p.setScoreboard(manager.getMainScoreboard());
        }

        for (World w : Bukkit.getWorlds()) {
            for (Entity e : w.getEntities()) {
                if (e.getType() == EntityType.ENDER_PEARL) {
                    e.remove();
                }
            }
        }

        inGame.clear();
        state = 0;
    }

    public void removePlayer(Player p) {

        if (state != 1 || !inGame.contains(p.getUniqueId())) {
            return;
        }

        p.setGameMode(GameMode.SPECTATOR);
        p.setFireTicks(0);

        inGame.remove(p.getUniqueId());
        Bukkit.broadcastMessage(ChatColor.DARK_RED + p.getName() + ChatColor.RED + " is out! " + ChatColor.DARK_GREEN + inGame.size() + ChatColor.GREEN + " Remaining!");

        p.getWorld().strikeLightningEffect(p.getLocation());

        YmlFile ymlfile = new YmlFile();
        ymlfile.WriteData(p, "Stats.Time", ymlfile.ReadData(p.getUniqueId(), "Stats.Time") + totalTimer);

        if (SettingsManager.instance.GetTeleportMode() == 4) updatePairs(p.getUniqueId());

        if (inGame.size() <= 1) {
            state = 2;
        }
    }

    public void makePairs() {

        if (inGame.size() < 2) {
            return;
        }

        Pairs.clear();
        randomTP.clear();
        randomTP.addAll(inGame);
        Collections.shuffle(randomTP);

        if (randomTP.size() % 2 != 0) {
            Pairs.add(new UUID[]{randomTP.get(randomTP.size() - 1), randomTP.get(randomTP.size() - 2), randomTP.get(randomTP.size() - 3)});
            Player p1 = Bukkit.getPlayer(randomTP.get(randomTP.size() - 1));
            Player p2 = Bukkit.getPlayer(randomTP.get(randomTP.size() - 2));
            Player p3 = Bukkit.getPlayer(randomTP.get(randomTP.size() - 3));
            p1.sendMessage(ChatColor.GREEN + "Your pair: " + ChatColor.GOLD + p3.getName() + ChatColor.GREEN + " --> " + ChatColor.GOLD + p1.getName() + ChatColor.GREEN + " --> " + ChatColor.GOLD + p2.getName());
            p2.sendMessage(ChatColor.GREEN + "Your pair: " + ChatColor.GOLD + p1.getName() + ChatColor.GREEN + " --> " + ChatColor.GOLD + p2.getName() + ChatColor.GREEN + " --> " + ChatColor.GOLD + p3.getName());
            p3.sendMessage(ChatColor.GREEN + "Your pair: " + ChatColor.GOLD + p2.getName() + ChatColor.GREEN + " --> " + ChatColor.GOLD + p3.getName() + ChatColor.GREEN + " --> " + ChatColor.GOLD + p1.getName());
            int Size = randomTP.size();
            randomTP.remove(Size - 1);
            randomTP.remove(Size - 2);
            randomTP.remove(Size - 3);
        }

        while (randomTP.size() >= 2) {
            Pairs.add(new UUID[]{randomTP.get(randomTP.size() - 1), randomTP.get(randomTP.size() - 2)});
            Player p1 = Bukkit.getPlayer(randomTP.get(randomTP.size() - 1));
            Player p2 = Bukkit.getPlayer(randomTP.get(randomTP.size() - 2));
            p1.sendMessage(ChatColor.GREEN + "Your pair: " + ChatColor.GOLD + p1.getName() + ChatColor.GREEN + " <--> " + ChatColor.GOLD + p2.getName());
            p2.sendMessage(ChatColor.GREEN + "Your pair: " + ChatColor.GOLD + p2.getName() + ChatColor.GREEN + " <--> " + ChatColor.GOLD + p1.getName());
            int Size = randomTP.size();
            randomTP.remove(Size - 1);
            randomTP.remove(Size - 2);
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            if ((p.isOp() && dev) || !inGame.contains(p.getUniqueId())) showPairs(p);
        }
    }

    public void showPairs(Player p) {
        if (inGame.size() < 2) {
            p.sendMessage(ChatColor.RED + "There are no pairs!");
            return;
        }

        p.sendMessage(ChatColor.DARK_GREEN + "The pairs:");

        for (UUID[] uuids : Pairs) {
            Player p1 = Bukkit.getPlayer(uuids[0]);
            Player p2 = Bukkit.getPlayer(uuids[1]);
            if (uuids.length == 2) {
                p.sendMessage(ChatColor.GOLD + p1.getName() + ChatColor.GREEN + " <--> " + ChatColor.GOLD + p2.getName());
            } else {
                Player p3 = Bukkit.getPlayer(uuids[2]);
                p.sendMessage(ChatColor.GOLD + p1.getName() + ChatColor.GREEN + " --> " + ChatColor.GOLD + p2.getName() + ChatColor.GREEN + " --> " + ChatColor.GOLD + p3.getName());
            }
        }
    }

    public void teleport0() {

        randomTP.clear();
        randomTP.addAll(inGame);

        try_ = 0;
        trySucces = true;

        teleport00();

        if (!trySucces) {
            Collections.shuffle(inGame);
            teleport1();
            return;
        }

        for (UUID uuid : inGame) {
            Player p = Bukkit.getPlayer(uuid);

            Player t = Bukkit.getPlayer(randomTP.get(inGame.indexOf(uuid)));

            p.teleport(randomLoc.get(inGame.indexOf(uuid)));
            p.sendMessage(ChatColor.GREEN + "You've been teleported to " + ChatColor.GOLD + t.getName());
            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, (int) SettingsManager.instance.GetSetting("Safe"), 255));
        }
    }

    public void teleport00() {
        try_++;

        if (try_ == 500) {
            trys++;
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.isOp() && dev) {
                    p.sendMessage(ChatColor.DARK_RED + "Couldn't find random assortment in " + try_ + " try's! Used random circle method instead!");
                    if (trys == 10) {
                        p.sendMessage(ChatColor.DARK_RED + "This has happend 10 times now, you can change the teleport mode to random circle to get rid of these messages!");
                    }
                }
            }
            trySucces = false;
            return;
        }

        randomLoc.clear();
        Collections.shuffle(randomTP);
        for (UUID uuid : inGame) {

            if (uuid.equals(randomTP.get(inGame.indexOf(uuid)))) {
                teleport00();
            }

            Player t = Bukkit.getPlayer(randomTP.get(inGame.indexOf(uuid)));
            randomLoc.add(t.getLocation());
        }
    }

    public void teleport1() {

        Location location = null;
        String name = "";

        for (UUID uuid : inGame) {
            Player p = Bukkit.getPlayer(uuid);

            if (inGame.indexOf(uuid) == 0) {
                location = p.getLocation();
                name = p.getName();
            }

            if (inGame.indexOf(uuid) + 1 == inGame.size()) {
                p.teleport(location);
                p.sendMessage(ChatColor.GREEN + "You've been teleported to " + ChatColor.GOLD + name);
                p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
                p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, (int) SettingsManager.instance.GetSetting("Safe"), 255));
                continue;
            }

            Player t = Bukkit.getPlayer(inGame.get(inGame.indexOf(uuid) + 1));

            p.teleport(t.getLocation());
            p.sendMessage(ChatColor.GREEN + "You've been teleported to " + ChatColor.GOLD + t.getName());
            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, (int) SettingsManager.instance.GetSetting("Safe"), 255));
        }
    }

    public void teleport2() {

        if (inGame.size() <= 1) {
            teleport1();
            return;
        }

        for (UUID[] uuids : Pairs) {

            Player p1 = Bukkit.getPlayer(uuids[0]);
            Player p2 = Bukkit.getPlayer(uuids[1]);
            Location location = p1.getLocation();

            p1.teleport(p2.getLocation());
            p1.sendMessage(ChatColor.GREEN + "You've been teleported to " + ChatColor.GOLD + p2.getName());
            p1.playSound(p1.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            p1.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, (int) SettingsManager.instance.GetSetting("Safe"), 255));

            if (uuids.length > 2) {
                Player p3 = Bukkit.getPlayer(uuids[2]);

                p2.teleport(p3.getLocation());
                p2.sendMessage(ChatColor.GREEN + "You've been teleported to " + ChatColor.GOLD + p3.getName());
                p2.playSound(p2.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
                p2.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, (int) SettingsManager.instance.GetSetting("Safe"), 255));

                p3.teleport(location);
                p3.sendMessage(ChatColor.GREEN + "You've been teleported to " + ChatColor.GOLD + p1.getName());
                p3.playSound(p3.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
                p3.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, (int) SettingsManager.instance.GetSetting("Safe"), 255));
            } else {
                p2.teleport(location);
                p2.sendMessage(ChatColor.GREEN + "You've been teleported to " + ChatColor.GOLD + p1.getName());
                p2.playSound(p2.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
                p2.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, (int) SettingsManager.instance.GetSetting("Safe"), 255));
            }
        }
    }

    public void updatePairs(UUID uuid) {

        if (inGame.size() <= 1) {
            return;
        }

        for (UUID[] uuids : Pairs) {
            int Index = Pairs.indexOf(uuids);
            if (uuids.length == 3) {
                if (uuids[0].equals(uuid)) {
                    uuids = new UUID[]{uuids[1], uuids[2]};
                } else if (uuids[1].equals(uuid)) {
                    uuids = new UUID[]{uuids[0], uuids[2]};
                } else if (uuids[2].equals(uuid)) {
                    uuids = new UUID[]{uuids[0], uuids[1]};
                }
                Pairs.set(Index, uuids);
            } else {
                if (uuids[0].equals(uuid)) {
                    uuids = new UUID[]{uuids[1]};
                } else if (uuids[1].equals(uuid)) {
                    uuids = new UUID[]{uuids[0]};
                }
                Pairs.set(Index, uuids);
            }
        }

        UUID Change = null;
        UUID[] Change2 = null;

        if (inGame.size() % 2 == 0) {
            for (UUID[] uuids : Pairs) {
                if (uuids.length == 1) {
                    Change = uuids[0];
                    Pairs.remove(uuids);
                } else if (uuids.length == 3) {
                    Change2 = uuids.clone();
                    Pairs.remove(uuids);
                }
            }
            if (Change != null) {
                Pairs.add(new UUID[]{Change, Change2[2]});
                Pairs.add(new UUID[]{Change2[0], Change2[1]});
            }
        } else {
            for (UUID[] uuids : Pairs) {
                if (uuids.length == 1) {
                    Change = uuids[0];
                    Pairs.remove(uuids);
                } else if (Change2 == null) {
                    Change2 = uuids.clone();
                    Pairs.remove(uuids);
                }
            }
            Pairs.add(new UUID[]{Change2[0], Change2[1], Change});
        }

        for (UUID[] uuids : Pairs) {
            if (uuids.length == 2) {
                Player p1 = Bukkit.getPlayer(uuids[0]);
                Player p2 = Bukkit.getPlayer(uuids[1]);
                p1.sendMessage(ChatColor.GREEN + "Your pair: " + ChatColor.GOLD + p1.getName() + ChatColor.GREEN + " <--> " + ChatColor.GOLD + p2.getName());
                p2.sendMessage(ChatColor.GREEN + "Your pair: " + ChatColor.GOLD + p2.getName() + ChatColor.GREEN + " <--> " + ChatColor.GOLD + p1.getName());
            } else {
                Player p1 = Bukkit.getPlayer(uuids[0]);
                Player p2 = Bukkit.getPlayer(uuids[1]);
                Player p3 = Bukkit.getPlayer(uuids[2]);
                p1.sendMessage(ChatColor.GREEN + "Your pair: " + ChatColor.GOLD + p3.getName() + ChatColor.GREEN + " --> " + ChatColor.GOLD + p1.getName() + ChatColor.GREEN + " --> " + ChatColor.GOLD + p2.getName());
                p2.sendMessage(ChatColor.GREEN + "Your pair: " + ChatColor.GOLD + p1.getName() + ChatColor.GREEN + " --> " + ChatColor.GOLD + p2.getName() + ChatColor.GREEN + " --> " + ChatColor.GOLD + p3.getName());
                p3.sendMessage(ChatColor.GREEN + "Your pair: " + ChatColor.GOLD + p2.getName() + ChatColor.GREEN + " --> " + ChatColor.GOLD + p3.getName() + ChatColor.GREEN + " --> " + ChatColor.GOLD + p1.getName());
            }
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            if ((p.isOp() && dev) || !inGame.contains(p.getUniqueId())) showPairs(p);
        }
    }

    public void timer() {

        if (!locations.isEmpty()) {
            int Total = 0;
            int Loaded = 0;
            for (UUID uuid : locations.keySet()) {
                Player p = Bukkit.getServer().getPlayer(uuid);
                Location loc = locations.get(uuid);
                Chunk ch = loc.getChunk();

                if ((float) p.getLocation().getX() != (float) loc.getX() || (float) p.getLocation().getY() != (float) loc.getY() || (float) p.getLocation().getZ() != (float) loc.getZ()) {
                    loc.setYaw(p.getLocation().getYaw());
                    loc.setPitch(p.getLocation().getPitch());
                    p.teleport(loc);
                }

                p.setVelocity(new Vector(0, 0, 0));

                if ((int) SettingsManager.instance.GetSetting("LoadChunkRadius") > 0)
                    for (int i = (int) SettingsManager.instance.GetSetting("LoadChunkRadius") * -1 + 1; i < (int) SettingsManager.instance.GetSetting("LoadChunkRadius"); i++) {
                        for (int j = (int) SettingsManager.instance.GetSetting("LoadChunkRadius") * -1 + 1; j < (int) SettingsManager.instance.GetSetting("LoadChunkRadius"); j++) {
                            Total++;
                            Chunk chunk = Bukkit.getWorld(ch.getWorld().getUID()).getChunkAt(ch.getX() + i, ch.getX() + j);
                            if (chunk.isLoaded()) Loaded++;
                        }
                    }
            }
            timer++;
            for (Player t : Bukkit.getOnlinePlayers()) {
                t.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Loading chunks: " + ChatColor.GREEN + Loaded + ChatColor.WHITE + "/" + ChatColor.GOLD + Total + ChatColor.GREEN + " Begining in: " + (10 - timer / 20)));

            }
            if (Total == Loaded && timer > 200) {
                locations.clear();
                for (Player t : Bukkit.getOnlinePlayers()) {
                    t.setGameMode(GameMode.SURVIVAL);
                }
                timer = 0;
            }
            return;
        }

        if (state != 1) {
            return;
        }

        untilSwap--;
        mainTimer++;
        totalTimer++;
        timer++;
        if (timer == 20) {
            seconds++;
            timer = 0;
        }
        if (seconds == 60) {
            minutes++;
            seconds = 0;
        }

        String TimerMessage = "";
        if ((Boolean) SettingsManager.instance.GetSetting("ShowTime"))
            if (seconds <= 9) {
                TimerMessage += minutes + ":0" + seconds;
            } else {
                TimerMessage += minutes + ":" + seconds;
            }

        TimerMessage += ChatColor.GRAY + " Swaps [" + ChatColor.DARK_GREEN + totalSwap + ChatColor.GRAY + "]";

        if ((int) SettingsManager.instance.GetSetting("Warning") >= untilSwap) {
            BigDecimal US = new BigDecimal((float) untilSwap / 20);
            TimerMessage += ChatColor.DARK_RED + " Swapping in: " + US.setScale(1, RoundingMode.HALF_UP) + " Seconds!";
        }

        for (Player p : Bukkit.getOnlinePlayers()) {

            if (mainTimer < (int) SettingsManager.instance.GetSetting("MinTimer") && (Boolean) SettingsManager.instance.GetSetting("ShowTime")) {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "[safe] Time since swap: " + TimerMessage));
            } else if ((Boolean) SettingsManager.instance.GetSetting("ShowTime")) {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "[unsafe] Time since swap: " + TimerMessage));
            } else {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + TimerMessage));
            }
        }

        if (untilSwap == 0) {
            Bukkit.broadcastMessage(ChatColor.BOLD + "SWAP!");
            timer = 0;
            seconds = 0;
            minutes = 0;
            mainTimer = 0;
            totalSwap++;
            int EPearl = 0;
            if ((Boolean) SettingsManager.instance.GetSetting("KillPearls")) {
                for (World w : Bukkit.getWorlds()) {
                    for (Entity e : w.getEntities()) {
                        if (e.getType() == EntityType.ENDER_PEARL) {
                            e.remove();
                            EPearl++;
                        }
                    }
                }
            }
            if (EPearl > 0) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.isOp() && dev) {
                        p.sendMessage("Removed " + EPearl + " enderpearl(s)!");
                    }
                }
            }
            for (UUID uuid : inGame) {
                Player p = Bukkit.getPlayer(uuid);
                YmlFile ymlfile = new YmlFile();
                ymlfile.WriteData(p, "Stats.Swaps", 1 + ymlfile.ReadData(p.getUniqueId(), "Stats.Swaps"));
            }
        }

        if (untilSwap == 0 && SettingsManager.instance.GetTeleportMode() == 0) {

            teleport0();
            Random rand = new Random();
            untilSwap = rand.nextInt((int) SettingsManager.instance.GetSetting("MaxTimer") + 1 - (int) SettingsManager.instance.GetSetting("MinTimer")) + (int) SettingsManager.instance.GetSetting("MinTimer");
            for (Player p : Bukkit.getOnlinePlayers()) {
                Chunk chunk = p.getLocation().getChunk();
                sendChunk(chunk, p);
            }

        } else if (untilSwap == 0 && SettingsManager.instance.GetTeleportMode() != 0 && SettingsManager.instance.GetTeleportMode() != 4) {

            teleport1();
            Random rand = new Random();
            untilSwap = rand.nextInt((int) SettingsManager.instance.GetSetting("MaxTimer") + 1 - (int) SettingsManager.instance.GetSetting("MinTimer")) + (int) SettingsManager.instance.GetSetting("MinTimer");
            if (SettingsManager.instance.GetTeleportMode() == 2) Collections.shuffle(inGame);
            Boolean first = true;
            String devm = "";
            for (UUID uuid : inGame) {
                if (!first) devm += ", ";
                devm += Bukkit.getPlayer(uuid).getName();
                first = false;
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.isOp() && dev)
                    p.sendMessage(ChatColor.DARK_GREEN + "The next circle of teleportation is: " + devm);
                Chunk chunk = p.getLocation().getChunk();
                sendChunk(chunk, p);
            }
        } else if (untilSwap == 0 && SettingsManager.instance.GetTeleportMode() == 4) {

            teleport2();
            Random rand = new Random();
            untilSwap = rand.nextInt((int) SettingsManager.instance.GetSetting("MaxTimer") + 1 - (int) SettingsManager.instance.GetSetting("MinTimer")) + (int) SettingsManager.instance.GetSetting("MinTimer");

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.isOp() && dev) showPairs(p);
                Chunk chunk = p.getLocation().getChunk();
                sendChunk(chunk, p);
            }
        }
    }

    public void ticker() {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(DeathSwap.main, () -> {

            if (state == 0) return;

            timer();
            if (state == 2) {
                state = 3;
                BukkitScheduler scheduler1 = Bukkit.getServer().getScheduler();
                scheduler1.scheduleSyncDelayedTask(DeathSwap.main, this::stop, 69);
            }

            ticker();
        }, 1);
    }

    public Location getRandomLocation(World world) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        WorldBorder border = world.getWorldBorder();
        Location borderCenter = border.getCenter();
        double borderSize = 10_000;

        int centerX = (int) borderCenter.getX();
        int centerZ = (int) borderCenter.getZ();

        int MaxX = (int) (centerX + borderSize);
        int MinX = (int) (centerX - borderSize);
        int MaxZ = (int) (centerZ + borderSize);
        int MinZ = (int) (centerZ - borderSize);

        int maxRadius = (int) SettingsManager.instance.GetSetting("TeleportRadius");
        if (MaxX > centerX + maxRadius) MaxX = centerX + maxRadius;
        if (MinX < centerX - maxRadius) MinX = centerX - maxRadius;
        if (MaxZ > centerZ + maxRadius) MaxZ = centerZ + maxRadius;
        if (MinZ < centerZ - maxRadius) MinZ = centerZ - maxRadius;

        Location loc = new Location(world, random.nextInt(MinX, MaxX), 62, random.nextInt(MinZ, MaxZ));
        if (world.getHighestBlockAt(loc).isLiquid()) return getRandomLocation(world);
        loc = world.getHighestBlockAt(loc).getLocation().add(0.5, 1, 0.5);
        return loc;
    }

    public void sendChunk(Chunk ch, Player p) {/**/}
}
