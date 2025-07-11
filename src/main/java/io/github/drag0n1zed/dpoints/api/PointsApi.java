package io.github.drag0n1zed.dpoints.api;

import io.github.drag0n1zed.dpoints.service.IPointManager;
import io.github.drag0n1zed.dpoints.service.PointServiceProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Provides static methods for retrieving and modifying a player's point balance.
 * Delegates calls to server or client IPointManager based on logical execution side.
 */
public class PointsApi {

    /**
     * Retrieves current point balance.
     * Server: authoritative; Client: cached.
     * @param player Player whose balance to retrieve.
     * @return Current point balance.
     */
    public static long getPlayerPoints(Player player) {
        boolean isClient = player == null || player.level().isClientSide;
        IPointManager pointManager = PointServiceProvider.get(isClient);
        return pointManager.getPoints(player);
    }

    /**
     * Updates player's point balance to specified amount.
     * Persists and synchronizes new balance to client.
     * @param player Player whose balance to set.
     * @param amount New balance value.
     */
    public static void setPlayerPoints(Player player, long amount) {
        if (player.level().isClientSide) return;
        IPointManager pointManager = PointServiceProvider.get(false);
        pointManager.setPoints(player, amount);
    }

    /**
     * Increments player's point balance by specified amount.
     * Persists and synchronizes updated balance to client.
     * @param player Player to credit points.
     * @param amountToAdd Number of points to add.
     */
    public static void addPlayerPoints(Player player, long amountToAdd) {
        if (player.level().isClientSide) return;
        IPointManager pointManager = PointServiceProvider.get(false);
        pointManager.addPoints(player, amountToAdd);
    }

    /**
     * Decrements player's point balance by specified amount.
     * Persists and synchronizes updated balance to client.
     * @param player Player to debit points.
     * @param amountToRemove Number of points to remove.
     */
    public static void removePlayerPoints(Player player, long amountToRemove) {
        if (player.level().isClientSide) return;
        IPointManager pointManager = PointServiceProvider.get(false);
        pointManager.removePoints(player, amountToRemove);
    }
}