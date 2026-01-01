package me.tWizT3d_dreaMr.ShopAddon.offlinesales;

import com.snowgears.shop.util.OfflineTransactions;
import me.tWizT3d_dreaMr.ShopAddon.main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Listener that creates an OfflineTransactions object on player login
 * and caches the formatted offline sales message for later recall.
 */
public class OfflineSalesListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Check if feature is enabled
        if (!main.getCon().getBoolean("OfflineSales.Enabled", true)) {
            return;
        }

        Player player = event.getPlayer();

        // Create a new OfflineTransactions object for this player
        // This will query the database for transactions since their last login
        OfflineTransactions offlineTx = new OfflineTransactions(
                player.getUniqueId(),
                player.getLastPlayed()
        );

        // Schedule a repeating task to wait for the calculation to complete
        BukkitRunnable task = new BukkitRunnable() {
            private int attempts = 0;
            private static final int MAX_ATTEMPTS = 5; // 5 seconds timeout

            @Override
            public void run() {
                attempts++;

                // Check if calculation is complete
                if (!offlineTx.isCalculating()) {
                    // Only cache if there are transactions
                    if (offlineTx.getNumTransactions() > 0) {
                        String message = offlineTx.getTransactionsLore();
                        if (message != null && !message.isEmpty()) {
                            OfflineSalesCache.getInstance().setMessage(player.getUniqueId(), message);
                        }
                    }
                    this.cancel();
                } else if (attempts >= MAX_ATTEMPTS) {
                    // Timeout after 5 seconds
                    this.cancel();
                }
            }
        };

        // Run every second (20 ticks), starting after 1 second
        task.runTaskTimer(main.plugin, 20L, 20L);
    }
}
