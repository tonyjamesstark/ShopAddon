package me.tWizT3d_dreaMr.ShopAddon;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.snowgears.shop.Shop;
import com.snowgears.shop.gui.HomeWindow;
import com.snowgears.shop.gui.ListSearchResultsWindow;

import net.md_5.bungee.api.ChatColor;

public class CommandSearch {
public static void Search(Player p, String itemString) {
	Material material=Material.getMaterial(itemString);
	if(material==null||material==Material.AIR) {
		p.sendMessage(ChatColor.RED+"Unknown item");
		return;
	}
	ItemStack SearchItem= new ItemStack(material);
    ListSearchResultsWindow searchResultsWindow = new ListSearchResultsWindow(p.getUniqueId(), SearchItem);
    /*
    not sure if i got to do this try null or not at all
    searchResultsWindow.setPrevWindow(null);
    Should be safe and stick with HomeWindow
    */
    searchResultsWindow.setPrevWindow(new HomeWindow(p.getUniqueId()));
    Shop.getPlugin().getGuiHandler().setWindow(p, searchResultsWindow);
}
}
