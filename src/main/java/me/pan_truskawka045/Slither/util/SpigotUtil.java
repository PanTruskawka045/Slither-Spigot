package me.pan_truskawka045.Slither.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import lombok.experimental.UtilityClass;
import me.pan_truskawka045.Slither.user.SlitherUser;
import me.pan_truskawka045.effects3d.points.Point;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class SpigotUtil {

    private final PlainTextComponentSerializer plainSerializer = PlainTextComponentSerializer.plainText();
    private final MiniMessage miniMessage = MiniMessage.miniMessage();


    public void sendPacket(Player player, Packet<?> packet) {
        ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    public void sendPackets(Player player, Packet<?>... packets) {
        for (Packet<?> packet : packets) {
            sendPacket(player, packet);
        }
    }

    public void sendPackets(SlitherUser user, Collection<? extends Packet<?>> packets) {
        for (Packet<?> packet : packets) {
            sendPacket(user.getPlayer(), packet);
        }
    }

    public void sendPacketToNearbyPlayers(Location location, int distance, Packet<? extends PacketListener>... packets) {
        for (Player player : location.getWorld().getPlayers()) {
            if (player.getLocation().distanceSquared(location) <= distance * distance) {
                sendPackets(player, packets);
            }
        }
    }

    public void sendPacketToNearbyPlayers(Level world, Vec3 location, int distance, Packet<? extends PacketListener>... packets) {
        List<HumanEntity> nearbyPlayers = world.findNearbyBukkitPlayers(location.x, location.y, location.z, distance, entity -> true);
        nearbyPlayers.forEach(humanEntity -> {
            if (humanEntity instanceof Player player) {
                sendPackets(player, packets);
            }
        });
    }

    public void sendPacket(SlitherUser user, Packet<?> packet) {
        sendPacket(user.getPlayer(), packet);
    }

    public Location pointToLocation(Point point, World world) {
        return new Location(world, point.getX(), point.getY(), point.getZ());
    }

    public Point locationToPoint(Location location) {
        return new Point(location.getX(), location.getY(), location.getZ());
    }

    public ServerLevel getServerLevel(World world) {
        return ((org.bukkit.craftbukkit.CraftWorld) world).getHandle();
    }

    public ServerLevel getServerLevel(Location location) {
        return ((org.bukkit.craftbukkit.CraftWorld) location.getWorld()).getHandle();
    }

    public PlayerProfile getPlayerProfile(String skinTexture, String skinSignature) {

        if (skinTexture == null || skinSignature == null) {
            return null;
        }

        int i = skinTexture.hashCode();
        long sign = (long) i << 32 | i;

        PlayerProfile bukkitProfile = Bukkit.createProfile(new UUID(sign, sign), skinTexture.substring(0, 16));
        bukkitProfile.setProperty(new ProfileProperty("textures", skinTexture, skinSignature));

        return bukkitProfile;
    }

    public Vec3 toVec3(Location location) {
        return new Vec3(location.getX(), location.getY(), location.getZ());
    }

    public ServerPlayer getServerPlayer(Player player) {
        return ((CraftPlayer) player).getHandle();
    }

    public DedicatedServer getDedicatedServer() {
        return ((CraftServer) Bukkit.getServer()).getServer();
    }
}
