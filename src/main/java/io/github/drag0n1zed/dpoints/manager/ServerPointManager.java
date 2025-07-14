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
 * Maintains authoritative player point data for multiple currencies,
 * persists to NBT, and synchronizes updates to clients.
 */
@ApiStatus.Internal
public final class ServerPointManager implements IPointManager {

    // Map<PlayerUUID, Map<Currency, Balance>>
    private final Map<UUID, Map<String, Long>> playerCurrencies = new HashMap<>();
    private static final String PERSISTENCE_KEY = "dpoints";
    private static final String CURRENCIES_KEY = "currencies";

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
     * Fetches current point balance for a currency from in-memory store.
     * @param player Player whose balance to retrieve.
     * @param currency Currency identifier
     * @return Stored point balance; defaults to 0 if absent.
     */
    @Override
    public long getPoints(Player player, String currency) {
        UUID uuid = player.getUUID();
        if (!playerCurrencies.containsKey(uuid)) {
            return 0L;
        }
        return playerCurrencies.get(uuid).getOrDefault(currency, 0L);
    }

    /**
     * Updates point balance for a currency, persists to player NBT, and syncs new value to client.
     * @param player Player whose balance to update.
     * @param currency Currency identifier
     * @param amount New point balance to set.
     */
    @Override
    public void setPoints(Player player, String currency, long amount) {
        UUID uuid = player.getUUID();
        Map<String, Long> currencies = playerCurrencies.computeIfAbsent(uuid, k -> new HashMap<>());
        currencies.put(currency, amount);
        
        // Persist to NBT
        CompoundTag modTag = getModTag(player);
        CompoundTag currenciesTag = modTag.getCompound(CURRENCIES_KEY);
        currenciesTag.putLong(currency, amount);
        modTag.put(CURRENCIES_KEY, currenciesTag);
        
        // Sync to client
        if (player instanceof ServerPlayer sp) {
            Networking.sendToClient(new SyncPointsPacket(currency, amount), sp);
        }
    }

    /**
     * Increments point balance for a currency by given amount.
     * @param player Player to credit points.
     * @param currency Currency identifier
     * @param amountToAdd Points to add.
     */
    @Override
    public void addPoints(Player player, String currency, long amountToAdd) {
        long current = getPoints(player, currency);
        setPoints(player, currency, current + amountToAdd);
    }

    /**
     * Decrements point balance for a currency by given amount.
     * @param player Player to debit points.
     * @param currency Currency identifier
     * @param amountToRemove Points to remove.
     */
    @Override
    public void removePoints(Player player, String currency, long amountToRemove) {
        long current = getPoints(player, currency);
        setPoints(player, currency, current - amountToRemove);
    }

    /**
     * Loads stored points from player NBT and syncs with client on login.
     * Handles migration from legacy single-currency storage.
     * @param player Player entity that joined.
     */
    public void onPlayerJoin(Player player) {
        CompoundTag modTag = getModTag(player);
        UUID uuid = player.getUUID();
        Map<String, Long> currencies = new HashMap<>();
        playerCurrencies.put(uuid, currencies);
        
        // Load currency balances
        if (modTag.contains(CURRENCIES_KEY)) {
            CompoundTag currenciesTag = modTag.getCompound(CURRENCIES_KEY);
            for (String key : currenciesTag.getAllKeys()) {
                currencies.put(key, currenciesTag.getLong(key));
            }
        }
        
        // Sync all currencies to client
        if (player instanceof ServerPlayer sp) {
            for (Map.Entry<String, Long> entry : currencies.entrySet()) {
                Networking.sendToClient(new SyncPointsPacket(entry.getKey(), entry.getValue()), sp);
            }
        }
    }

    /**
     * Removes player's entry from in-memory store on logout.
     * @param player Player entity that left.
     */
    public void onPlayerLeave(Player player) {
        playerCurrencies.remove(player.getUUID());
    }
}