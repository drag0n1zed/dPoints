package io.github.drag0n1zed.dpoints.api;

import io.github.drag0n1zed.dpoints.service.IPointManager;
import io.github.drag0n1zed.dpoints.service.PointServiceProvider;
import net.minecraft.world.entity.player.Player;

/**
 * Provides static methods for retrieving and modifying a player's point balances.
 * Delegates calls to server or client IPointManager based on logical execution side.
 * Supports multiple currencies identified by string keys (e.g., "dpoints:default").
 */
public class PointsApi {

    /**
     * Default currency identifier for backward compatibility
     */

    /**
     * Retrieves current point balance for a specific currency.
     * Server: authoritative; Client: cached.
     * @param player Player whose balance to retrieve.
     * @param currency Currency identifier (e.g., "dpoints:default")
     * @return Current point balance for specified currency.
     */
    public static long getPlayerPoints(Player player, String currency) {
        boolean isClient = player == null || player.level().isClientSide;
        IPointManager pointManager = PointServiceProvider.get(isClient);
        return pointManager.getPoints(player, currency);
    }

    /**
     * Updates player's point balance for a specific currency to specified amount.
     * Persists and synchronizes new balance to client.
     * @param player Player whose balance to set.
     * @param currency Currency identifier
     * @param amount New balance value.
     */
    public static void setPlayerPoints(Player player, String currency, long amount) {
        if (player.level().isClientSide) return;
        IPointManager pointManager = PointServiceProvider.get(false);
        pointManager.setPoints(player, currency, amount);
    }

    /**
     * Increments player's point balance for a specific currency by specified amount.
     * Persists and synchronizes updated balance to client.
     * @param player Player to credit points.
     * @param currency Currency identifier
     * @param amountToAdd Number of points to add.
     */
    public static void addPlayerPoints(Player player, String currency, long amountToAdd) {
        if (player.level().isClientSide) return;
        IPointManager pointManager = PointServiceProvider.get(false);
        pointManager.addPoints(player, currency, amountToAdd);
    }

    /**
     * Decrements player's point balance for a specific currency by specified amount.
     * Persists and synchronizes updated balance to client.
     * @param player Player to debit points.
     * @param currency Currency identifier
     * @param amountToRemove Number of points to remove.
     */
    public static void removePlayerPoints(Player player, String currency, long amountToRemove) {
        if (player.level().isClientSide) return;
        IPointManager pointManager = PointServiceProvider.get(false);
        pointManager.removePoints(player, currency, amountToRemove);
    }
}