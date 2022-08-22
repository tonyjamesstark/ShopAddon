package me.tWizT3d_dreaMr.ShopAddon;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.snowgears.shop.shop.ShopType;


public class CreationCheck {
private ConfigurationSection section;
private ArrayList<Filter> filters;
private boolean whitelist;
public CreationCheck(YamlConfiguration from,boolean wh) {
	if(from==null) return;
	filters=new ArrayList<>();
	section = from.getConfigurationSection("itemListing");
	for(String title:section.getKeys(false)) {
		ConfigurationSection testfor=section.getConfigurationSection(title);
		String shopType=testfor.getString("shoptype");
		String material=testfor.getString("material");
		String lore=testfor.getString("lore-contains");
		String name=testfor.getString("name-contains");
		String listType=testfor.getString("ListType");
		String priceMax=testfor.getString("pricemax");
		String priceMin=testfor.getString("pricemin");
		Filter f= new Filter(shopType, name, lore, listType, title, material, priceMin, priceMax);
		filters.add(f);
	}
	
	whitelist=wh;
}
public MatchType test(ItemStack i, double price, int amount, ShopType st) {
	for(Filter f: filters) {
		boolean match=false;
		if(f.valid()) continue;
		if(!f.MaterialSame(i.getType())) match=true;
		String name="";
		String lore="";
		if(i.hasItemMeta()) {
			ItemMeta im=i.getItemMeta();
			if(im.hasDisplayName())
				name=ChatColor.stripColor(im.getDisplayName());
			if(im.hasLore())
				lore=ChatColor.stripColor(im.getLore().toString());
		}
		if(!f.NameContains(name)) match=true;
		if(!f.LoreContains(lore)) match=true;
		if(match)
			continue;
		if(f.ListType().equalsIgnoreCase("BlackList")) {
			return new MatchType("BlackList", f);
		}
		if(f.ListType().equalsIgnoreCase("WhiteList")&&whitelist) {
			return new MatchType("WhiteList", f);
		}
		if(f.ListType().equalsIgnoreCase("PRICE")) {
			if(!f.maxCheck(price/amount)) {
				return new MatchType("pricemax", f);
			}
			if(!f.minCheck(price/amount)) {
				return new MatchType("pricemin", f);
			}
		}
	}
	
	return null;
}
}