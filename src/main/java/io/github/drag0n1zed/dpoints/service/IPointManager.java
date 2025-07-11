package io.github.drag0n1zed.dpoints.service;

import net.minecraft.world.entity.player.Player;

/**
 * Provides operations for retrieving, setting, adding, and removing player points.
 * Implementations may be server-authoritative or client-cached.
 */
public interface IPointManager {

    /**
     * Returns current point balance; authoritative on server, cached on client.
     * @param player Target player.
     * @return Current point balance.
     */
    long getPoints(Player player);

    /**
     * Updates the point balance for a player.
     * @param player Target player.
     * @param amount New balance.
     */
    void setPoints(Player player, long amount);

    /**
     * Increments player's point balance by given amount; server-side only.
     * @param player Target player.
     * @param amountToAdd Points to add.
     */
    void addPoints(Player player, long amountToAdd);

    /**
     * Decrements player's point balance by given amount; server-side only.
     * @param player Target player.
     * @param amountToRemove Points to remove.
     */
    void removePoints(Player player, long amountToRemove);
}