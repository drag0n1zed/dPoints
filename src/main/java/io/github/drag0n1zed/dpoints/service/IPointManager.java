package io.github.drag0n1zed.dpoints.service;

import net.minecraft.world.entity.player.Player;

/**
 * Provides operations for retrieving, setting, adding, and removing player points for specific currencies.
 * Implementations may be server-authoritative or client-cached.
 */
public interface IPointManager {

    /**
     * Returns current point balance for a specific currency; authoritative on server, cached on client.
     * @param player Target player.
     * @param currency Currency identifier (e.g., "dpoints:default")
     * @return Current point balance for the currency.
     */
    long getPoints(Player player, String currency);

    /**
     * Updates the point balance for a player for a specific currency.
     * @param player Target player.
     * @param currency Currency identifier
     * @param amount New balance.
     */
    void setPoints(Player player, String currency, long amount);

    /**
     * Increments player's point balance for a specific currency by given amount; server-side only.
     * @param player Target player.
     * @param currency Currency identifier
     * @param amountToAdd Points to add.
     */
    void addPoints(Player player, String currency, long amountToAdd);

    /**
     * Decrements player's point balance for a specific currency by given amount; server-side only.
     * @param player Target player.
     * @param currency Currency identifier
     * @param amountToRemove Points to remove.
     */
    void removePoints(Player player, String currency, long amountToRemove);
}