package me.tWizT3d_dreaMr.ShopAddon.Gui;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

import com.snowgears.shop.shop.AbstractShop;
import com.snowgears.shop.util.CurrencyType;

public class Guis {
public static ArrayList<Gui> guilist;
public static CurrencyType type;
public static Gui getGui(InventoryView IV) {
	if(guilist==null) {
		guilist= new ArrayList<Gui>();
	}
	if(guilist.isEmpty()) {
		return null;
	}
	for(Gui gui:guilist) {
		if(gui.is(IV))
			return gui;
	}
	return null;
}
public static void add(Gui gui) {
	if(guilist==null) {
		guilist= new ArrayList<Gui>();
	}
	guilist.add(gui);
}
public static void makeGui(Player p, AbstractShop s) {
	add(new Gui(p, s));
}
public static CurrencyType getCurrencyType() {
	return type;
}
}
