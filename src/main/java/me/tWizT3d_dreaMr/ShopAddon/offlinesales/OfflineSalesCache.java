package me.tWizT3d_dreaMr.ShopAddon.offlinesales;

import java.util.HashMap;
import java.util.UUID;

/**
 * In-memory cache for storing the last offline shop sales message per player. Thread-safe singleton
 * implementation.
 */
public class OfflineSalesCache {

    private static OfflineSalesCache instance;
    private final HashMap<UUID, String> cache;

    private OfflineSalesCache() {
        this.cache = new HashMap<>();
    }

    /**
     * Get the singleton instance of the cache.
     *
     * @return The OfflineSalesCache instance
     */
    public static synchronized OfflineSalesCache getInstance() {
        if (instance == null) {
            instance = new OfflineSalesCache();
        }
        return instance;
    }

    /**
     * Set or replace the cached message for a player.
     *
     * @param playerUUID The player's UUID
     * @param message The formatted offline sales message
     */
    public synchronized void setMessage(UUID playerUUID, String message) {
        cache.put(playerUUID, message);
    }

    /**
     * Get the cached message for a player.
     *
     * @param playerUUID The player's UUID
     * @return The cached message, or null if none exists
     */
    public synchronized String getMessage(UUID playerUUID) {
        return cache.get(playerUUID);
    }

    /**
     * Clear the cached message for a player.
     *
     * @param playerUUID The player's UUID
     */
    public synchronized void clearMessage(UUID playerUUID) {
        cache.remove(playerUUID);
    }

    /**
     * Check if a player has a cached message.
     *
     * @param playerUUID The player's UUID
     * @return True if a message exists, false otherwise
     */
    public synchronized boolean hasMessage(UUID playerUUID) {
        return cache.containsKey(playerUUID);
    }
}
