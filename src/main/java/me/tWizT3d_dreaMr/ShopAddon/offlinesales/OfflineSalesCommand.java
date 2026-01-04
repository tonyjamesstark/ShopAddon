package me.tWizT3d_dreaMr.ShopAddon.offlinesales;

import me.tWizT3d_dreaMr.ShopAddon.util.Format;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command handler for /offlinesales command. Displays the cached offline shop sales message for the
 * player.
 */
public class OfflineSalesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Only players can use this command
        if (!(sender instanceof Player)) {
            sender.sendMessage(Format.format("&cThis command can only be used by players."));
            return true;
        }

        Player player = (Player) sender;

        // Check permission
        if (!player.hasPermission("shopaddon.offlinesales")) {
            player.sendMessage(Format.format("&cYou don't have permission to use this command."));
            return true;
        }

        // Get cached message
        String cachedMessage = OfflineSalesCache.getInstance().getMessage(player.getUniqueId());

        if (cachedMessage == null || cachedMessage.isEmpty()) {
            player.sendMessage(Format.format("&7No offline sales to display."));
            return true;
        }

        // Display the cached message
        player.sendMessage(Format.format("&b&l--- Last Offline Shop Sales ---"));
        player.sendMessage("");

        // Send the cached message (already formatted by OfflineTransactions)
        String[] lines = cachedMessage.split("\n");
        for (String line : lines) {
            player.sendMessage(line);
        }

        player.sendMessage("");
        player.sendMessage(
                Format.format("&7Use this command anytime to see your last offline sales."));

        return true;
    }
}
