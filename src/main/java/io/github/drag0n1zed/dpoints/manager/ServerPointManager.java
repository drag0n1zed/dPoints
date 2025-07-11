package io.github.drag0n1zed.dpoints.manager;

import io.github.drag0n1zed.dpoints.network.Networking;
import io.github.drag0n1zed.dpoints.network.SyncPointsPacket;
import io.github.drag0n1zed.dpoints.service.IPointManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Maintains authoritative player point data, persists to NBT, and synchronizes updates to clients.
 */
@ApiStatus.Internal
public final class ServerPointManager implements IPointManager {

    private final Map<UUID, Long> points = new HashMap<>();
    private static final String PERSISTENCE_KEY = "dpoints";
    private static final String POINTS_KEY = "points";

    /**
     * Retrieves or initializes the mod-specific NBT tag for storing player points.
     * @param player Player whose persistent data to access.
     * @return CompoundTag used for point state persistence.
     */
    private CompoundTag getModTag(Player player) {
        CompoundTag playerData = player.getPersistentData();
        if (!playerData.contains(Player.PERSISTED_NBT_TAG)) {
            playerData.put(Player.PERSISTED_NBT_TAG, new CompoundTag());
        }
        CompoundTag forgeData = playerData.getCompound(Player.PERSISTED_NBT_TAG);
        if (!forgeData.contains(PERSISTENCE_KEY)) {
            forgeData.put(PERSISTENCE_KEY, new CompoundTag());
        }
        return forgeData.getCompound(PERSISTENCE_KEY);
    }

    /**
     * Fetches current point balance from in-memory store.
     * @param player Player whose balance to retrieve.
     * @return Stored point balance; defaults to 0 if absent.
     */
    @Override
    public long getPoints(Player player) {
        return points.getOrDefault(player.getUUID(), 0L);
    }

    /**
     * Updates point balance, persists to player NBT, and syncs new value to client.
     * @param player Player whose balance to update.
     * @param amount New point balance to set.
     */
    @Override
    public void setPoints(Player player, long amount) {
        points.put(player.getUUID(), amount);
        getModTag(player).putLong(POINTS_KEY, amount);
        if (player instanceof ServerPlayer sp) {
            Networking.sendToClient(new SyncPointsPacket(amount), sp);
        }
    }

    /**
     * Increments point balance by invoking setPoints with calculated sum.
     * @param player Player to credit points.
     * @param amountToAdd Number of points to add.
     */
    @Override
    public void addPoints(Player player, long amountToAdd) {
        setPoints(player, getPoints(player) + amountToAdd);
    }

    /**
     * Decrements point balance by invoking setPoints with calculated difference.
     * @param player Player to debit points.
     * @param amountToRemove Number of points to remove.
     */
    @Override
    public void removePoints(Player player, long amountToRemove) {
        setPoints(player, getPoints(player) - amountToRemove);
    }

    /**
     * Loads stored points from player NBT and syncs with client on login.
     * @param player Player entity that joined.
     */
    public void onPlayerJoin(Player player) {
        long balance = getModTag(player).getLong(POINTS_KEY);
        points.put(player.getUUID(), balance);
        if (player instanceof ServerPlayer sp) {
            Networking.sendToClient(new SyncPointsPacket(balance), sp);
        }
    }

    /**
     * Removes player's entry from in-memory store on logout.
     * @param player Player entity that left.
     */
    public void onPlayerLeave(Player player) {
        points.remove(player.getUUID());
    }
}