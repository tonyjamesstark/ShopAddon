package me.tWizT3d_dreaMr.ShopAddon.search;

import com.snowgears.shop.Shop;
import me.tWizT3d_dreaMr.ShopAddon.main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CommandSearch {
    public static void Search(Player p, String[] args) {
        if (args.length < 1) {
            p.sendMessage(ChatColor.RED + "Invalid arguments for /searchshops");
            return;
        }

        MyListSearchResultsWindow searchResultsWindow = null;

        if (args[0].equals("player")) {
            // search by player name
            OfflinePlayer shopOwner = lookupPlayer(args[1].strip().toLowerCase());
            if (shopOwner == null) {
                p.sendMessage(ChatColor.RED + "Player not found");
                return;
            }
            searchResultsWindow = new MyListSearchResultsWindow(p.getUniqueId(), shopOwner);
        } else if (args[0].equals("loredName")) {
            // search by custom lored name
            searchResultsWindow = new MyListSearchResultsWindow(p.getUniqueId(), args[1]);
        } else {
            // search by material
            Material material = main.matchMaterial(args[0]);
            if (material == null || material == Material.AIR) {
                p.sendMessage(ChatColor.RED + "Unknown item");
                return;
            }
            searchResultsWindow = new MyListSearchResultsWindow(p.getUniqueId(), material);
        }

        Shop.getPlugin().getGuiHandler().setWindow(p, searchResultsWindow);
    }

    @SuppressWarnings("deprecation")
    public static OfflinePlayer lookupPlayer(String search) {
        OfflinePlayer p = Bukkit.getOfflinePlayer(search);
        if (p.hasPlayedBefore()) {
            return p;
        }

        // if not found, try looking for essentials nickname
        for (OfflinePlayer online : Bukkit.getOnlinePlayers()) {
            if (ChatColor.stripColor(main.ess.getUser(online).getNick())
                    .strip()
                    .toLowerCase()
                    .equals(search)) {
                return online;
            }
        }

        return null;
    }
}
