package me.tWizT3d_dreaMr.Gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.snowgears.shop.shop.AbstractShop;

public class gui {
	private Player p;
	private AbstractShop shop;
	private boolean isOpen;
	Inventory inv;
	public gui(Player player, AbstractShop aShop) {
		this.p= player;
		this.shop= aShop;
		this.isOpen= true;
		this.inv=openGui();
		p.openInventory(inv);
	}
	private Inventory openGui() {
		Inventory inventory= Bukkit.createInventory(null, 27, "Shop");
		for(int i=0; i<27; i++) {
			if(i==10||i==11||i==12) {
				inventory.setItem(i, getItem(i));
			}
			inventory.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
		}
		
		
		return inventory;
	}
	private ItemStack getItem(int i) {
		ItemStack item=new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		if(shop.getAmount() < 64) {
			if(i==10||i==11)
				return item;
			return shop.getItemStack();
		}
		if(shop.getAmount() < 128){
			if(i==10)
				return item;
			if(i==11) {
				item=shop.getItemStack();
				item.setAmount(64);
				return item;
			}
			if(i==12) {
				item=shop.getItemStack();
				item.setAmount(shop.getAmount()-64);
				return item;
			}
		}
		if(shop.getAmount() < 196){
			if(i==10||i==11) {
				item=shop.getItemStack();
				item.setAmount(64);
				return item;
			}
			if(i==12) {
				item=shop.getItemStack();
				int left=shop.getAmount()-128;
				if(left>64)
					left=64;
				item.setAmount(left);
				return item;
			}
		}
		return item;
	}
}
