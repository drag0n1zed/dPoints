package io.github.drag0n1zed.dpoints.manager;

import io.github.drag0n1zed.dpoints.service.IPointManager;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;

/**
 * Caches player's point balance on the client.
 * Reflects server-synchronized data and does not perform authoritative updates.
 */
@ApiStatus.Internal
public final class ClientPointManager implements IPointManager {

    private long points = 0L;

    @Override
    public long getPoints(Player player) {
        return points;
    }

    @Override
    public void setPoints(Player player, long amount) {
        // Update local cache with received server balance.
        this.points = amount;
    }

    @Override
    public void addPoints(Player player, long amountToAdd) {
        // No-op: client does not handle point additions.
    }

    @Override
    public void removePoints(Player player, long amountToRemove) {
        // No-op: client does not handle point removals.
    }
}