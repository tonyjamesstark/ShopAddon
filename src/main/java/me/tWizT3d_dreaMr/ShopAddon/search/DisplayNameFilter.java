package me.tWizT3d_dreaMr.ShopAddon.search;

import java.util.function.Predicate;
import org.bukkit.ChatColor;
import com.snowgears.shop.shop.AbstractShop;

	public class DisplayNameFilter implements Predicate<AbstractShop>{

		String loredName = "";

		public DisplayNameFilter(String s){
			loredName = s.trim().toLowerCase();
		}

		public boolean test(AbstractShop s){
			boolean ret = false;
			if (s.getItemStack() != null && s.getItemStack().hasItemMeta() && s.getItemStack().getItemMeta().hasDisplayName()){
				ret |= ChatColor.stripColor(s.getItemStack().getItemMeta().getDisplayName()).trim().toLowerCase().contains(loredName);
			}
			if (s.getSecondaryItemStack() != null && s.getSecondaryItemStack().hasItemMeta() && s.getSecondaryItemStack().getItemMeta().hasDisplayName()){
				ret |= ChatColor.stripColor(s.getSecondaryItemStack().getItemMeta().getDisplayName()).trim().toLowerCase().contains(loredName);
			}
			return ret;
		}
	}