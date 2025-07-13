package io.github.drag0n1zed.dpoints.manager;

import io.github.drag0n1zed.dpoints.service.IPointManager;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Caches player's point balances for multiple currencies on the client.
 * Reflects server-synchronized data and does not perform authoritative updates.
 */
@ApiStatus.Internal
public final class ClientPointManager implements IPointManager {

    // Map<Currency, Balance>
    private final Map<String, Long> currencies = new HashMap<>();

    @Override
    public long getPoints(Player player, String currency) {
        return currencies.getOrDefault(currency, 0L);
    }

    @Override
    public void setPoints(Player player, String currency, long amount) {
        // Update local cache with received server balance for specific currency
        currencies.put(currency, amount);
    }

    @Override
    public void addPoints(Player player, String currency, long amountToAdd) {
        // No-op: client does not handle point additions.
    }

    @Override
    public void removePoints(Player player, String currency, long amountToRemove) {
        // No-op: client does not handle point removals.
    }
}