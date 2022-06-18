package me.bram2323.DeathSwap.Game;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_15_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import me.bram2323.DeathSwap.Main;
import net.minecraft.server.v1_15_R1.PacketPlayOutMapChunk;

public class PacketMapChunk {
    
    private final net.minecraft.server.v1_15_R1.Chunk chunk;
   
    /**
     * Creates a PacketMapChunk.
     *
     * @param world The chunk's world.
     * @param x The chunk's X.
     * @param z The chunk's Z.
     */
   
    public PacketMapChunk(final World world, final int x, final int z) {
        this(world.getChunkAt(x, z));
    }
   
    /**
     * Creates a PacketMapChunk.
     *
     * @param chunk The chunk.
     */
   
    public PacketMapChunk(final org.bukkit.Chunk chunk) {
        this.chunk = ((CraftChunk)chunk).getHandle();
    }
   
    /**
     * Sends this packet to a player.
     * <br>You still need to refresh it manually with <code>world.refreshChunk(...)</code>.
     *
     * @param player The player.
     */
    
    public final void send(final Player player) {
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutMapChunk(chunk, 20));
        if (player.isOp() && Main.game.dev) player.sendMessage("Chunk sended! " + chunk.getPos().x + "," + chunk.getPos().z);
    }
   
    /**
     * Refresh a chunk for the selected players.
     *
     * @param world The chunk's world.
     * @param x The chunk's X.
     * @param z The chunk's Z.
     * @param players The players.
     */
   
    @SuppressWarnings("deprecation")
	public static final void refreshChunk(final Chunk chunk, final Player player) {
        final PacketMapChunk packet = new PacketMapChunk(chunk);
        packet.send(player);
        chunk.getWorld().refreshChunk(chunk.getX(), chunk.getZ());
    }
}